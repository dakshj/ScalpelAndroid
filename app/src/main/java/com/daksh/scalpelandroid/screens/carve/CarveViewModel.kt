package com.daksh.scalpelandroid.screens.carve

import android.arch.lifecycle.MutableLiveData
import com.daksh.scalpelandroid.extensions.matchWithWildCard
import com.daksh.scalpelandroid.extensions.toScalpelBytes
import com.daksh.scalpelandroid.livedata.SingleLiveEvent
import com.daksh.scalpelandroid.rx.RxAwareViewModel
import com.daksh.scalpelandroid.rx.RxSchedulers
import com.daksh.scalpelandroid.storage.DirectoryManager
import com.daksh.scalpelandroid.storage.prefs.AppSettings
import com.daksh.scalpelandroid.storage.room.dao.RuleDao
import com.daksh.scalpelandroid.storage.room.entity.Rule
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.toFlowable
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException
import javax.inject.Inject

class CarveViewModel @Inject constructor(
        private val appSettings: AppSettings,
        private val ruleDao: RuleDao,
        private val directoryManager: DirectoryManager
) : RxAwareViewModel() {

    val liveSelectedSourceFilePath: MutableLiveData<String> = MutableLiveData()
    val liveCarving: MutableLiveData<Boolean> = MutableLiveData()
    val liveCarvedFiles: MutableLiveData<List<File>> = MutableLiveData()
    val singleLiveMessage: SingleLiveEvent<String> = SingleLiveEvent()

    init {
        liveSelectedSourceFilePath.value = appSettings.selectedSourceFile
        addStaticRulesToDb()
    }

    private fun addStaticRulesToDb() {
        Completable.fromAction { ruleDao.addStaticRules() }
                .subscribeOn(RxSchedulers.database)
                .subscribe({}, { Timber.e(it, "Error while inserting rules!") })
    }

    var selectedFile: File?
        get() {
            appSettings.selectedSourceFile?.let {
                return File(it)
            }

            return null
        }
        set(file) {
            file?.run { if (this.exists()) this else null }?.absolutePath?.let {
                appSettings.selectedSourceFile = it
                liveSelectedSourceFilePath.value = it
            }
        }

    fun carve() {
        val allCarvedFiles = mutableListOf<File>()

        liveCarving.value = true

        lateinit var sourceFileBytes: List<Byte>

        val currentRunDirectory = directoryManager.getCurrentRunDirectory()

        readSourceFile()

                // Save source file bytes to an object for later use
                .flatMap {
                    sourceFileBytes = it.toList()
                    Single.just(it)
                }

                // Read all rules from DB
                .observeOn(RxSchedulers.database)
                .flatMap { Single.fromCallable { ruleDao.getAll() } }
                .toFlowable()
                .flatMapIterable { it }

                // Process each rule in parallel, to be run on disk
                .parallel()
                .runOn(RxSchedulers.disk)

                .flatMap { rule ->
                    val dirForRule = directoryManager.getDirectoryForRule(rule, currentRunDirectory)
                    val carvedFiles = mutableListOf<File>()

                    val headerBytes = rule.header.toScalpelBytes()
                    val footerBytes = rule.footer?.toScalpelBytes()

                    val possibleCarves =
                    // Iterate over all start indices of each window
                            (0..sourceFileBytes.size - headerBytes.size)

                                    // Filter those windows which match header bytes
                                    .filter {
                                        sourceFileBytes.subList(it, it + headerBytes.size)
                                                .matchWithWildCard(headerBytes)
                                    }

                                    // Get Bytes from Header to max bytes amount
                                    .map {
                                        // Prevent overflow by choosing min of max bytes,
                                        // or the end of the byte stream
                                        val end = minOf(it + rule.maxBytesAmount,
                                                sourceFileBytes.size)
                                        sourceFileBytes.subList(it, end)
                                    }

                                    // If footer bytes are null, then save the entire
                                    // List<Byte> as a file
                                    .filter {
                                        if (footerBytes == null) {
                                            if (rule.forceSave) {
                                                // Save the file and skip it from going into
                                                // `possibleCarves`
                                                saveToFile(it, rule, dirForRule)?.let {
                                                    carvedFiles.add(it)
                                                }
                                            }

                                            false
                                        } else {
                                            // Put this carving into `possibleCarves`
                                            true
                                        }
                                    }

                    // If footer bytes are non-null:

                    possibleCarves.forEach { currCarve ->
                        val footerStartIndex = maxOf(headerBytes.size, rule.minBytesAmount)

                        (footerStartIndex..(currCarve.size - footerBytes!!.size))
                                .filter {
                                    currCarve.subList(it, it + footerBytes.size)
                                            .matchWithWildCard(footerBytes)
                                }

                                // Reverse it if rule says so
                                .run {
                                    if (rule.reverseSearchFooter) {
                                        this.reversed()
                                    } else {
                                        this
                                    }
                                }

                                // Map carve size (since index is not required anymore)
                                .map {
                                    if (rule.skipFooter) {
                                        it
                                    } else {
                                        it + footerBytes.size
                                    }
                                }

                                // Ensure a minimum carve size
                                .filter {
                                    it >= rule.minBytesAmount
                                }

                                // Map to bytes carved from starting to carve size
                                .map {
                                    currCarve.subList(0, it)
                                }

                                // Save carves for each found footer
                                .onEach {
                                    saveToFile(it, rule, dirForRule)?.let {
                                        carvedFiles.add(it)
                                    }
                                }

                                .run {
                                    // If the list of carves is empty
                                    if (this.isEmpty()) {
                                        if (rule.forceSave) {
                                            // Save the entire carving
                                            saveToFile(currCarve, rule, dirForRule)?.let {
                                                carvedFiles.add(it)
                                            }
                                        }
                                    }
                                }
                    }

                    // Return Flowable of carved Files, which continues the Rx chain
                    carvedFiles.toFlowable()
                }

                // Bring back to Flowable (from ParallelFlowable)
                .sequential()

                .observeOn(RxSchedulers.main)

                // Carving over -- update UI
                .doFinally { liveCarving.value = false }

                .subscribe(
                        // onNext
                        {
                            allCarvedFiles.add(it)
                            liveCarvedFiles.value = allCarvedFiles
                        },

                        // onError
                        {
                            singleLiveMessage.value = "Error while carving data!"
                        },

                        // onComplete
                        {
                            singleLiveMessage.value = "Completed carving!"
                        }
                )

                // Dispose it
                .let { disposables += it }
    }

    private fun saveToFile(bytes: List<Byte>, rule: Rule, dirForRule: File): File? {
        // TODO Validate the file, and save only if it is a valid file format!
        // Else, return null

        val fileCarved = File(dirForRule, generateCarvedFileName(rule))
        fileCarved.writeBytes(bytes.toByteArray())
        return fileCarved
    }

    private fun generateCarvedFileName(rule: Rule): String =
            "${LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)}.${rule.extension}"

    private fun readSourceFile(): Single<ByteArray> {
        return Single.create<ByteArray> {
            if (selectedFile == null) {
                it.onError(IllegalArgumentException("Please select a file!"))
            }

            selectedFile?.run {
                if (!this.exists()) {
                    it.onError(FileNotFoundException(this.absolutePath))
                }

                it.onSuccess(this.readBytes())
            }
        }
                .subscribeOn(RxSchedulers.disk)
    }

    fun cancelCarving() {
        liveCarving.value = false
    }

    fun fileClicked() {
        // TODO Open file using appropriate MIME Types
    }
}
