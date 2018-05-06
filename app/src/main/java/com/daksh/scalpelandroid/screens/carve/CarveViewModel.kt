package com.daksh.scalpelandroid.screens.carve

import android.arch.lifecycle.MutableLiveData
import com.daksh.scalpelandroid.extensions.toScalpelBytes
import com.daksh.scalpelandroid.rx.RxAwareViewModel
import com.daksh.scalpelandroid.rx.RxSchedulers
import com.daksh.scalpelandroid.storage.DirectoryManager
import com.daksh.scalpelandroid.storage.prefs.AppSettings
import com.daksh.scalpelandroid.storage.room.dao.RuleDao
import com.daksh.scalpelandroid.storage.room.entity.Rule
import io.reactivex.Single
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
    val liveCarvedFiles: MutableLiveData<List<String>> = MutableLiveData()

    init {
        liveSelectedSourceFilePath.value = appSettings.selectedSourceFile
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
        val allCarvedFiles = mutableListOf<String>()

        liveCarving.value = true

        lateinit var sourceFileBytes: List<Byte>

        val currentRunDirectory = directoryManager.getCurrentRunDirectory()

        readSourceFile()
                .doOnSuccess { sourceFileBytes = it.toList() }

                .observeOn(RxSchedulers.database)
                .flatMap { Single.fromCallable { ruleDao.getAll() } }
                .toFlowable()
                .flatMapIterable { it }
                .parallel()
                .runOn(RxSchedulers.disk)
                .flatMap { rule ->
                    // TODO Check if sourceFileBytes has been set properly or not

                    val dirForRule = directoryManager.getDirectoryForRule(rule, currentRunDirectory)
                    val carvedFiles = mutableListOf<String>()

                    val headerBytes = rule.header.toScalpelBytes().toList()
                    val footerBytes = rule.footer?.toScalpelBytes()?.toList()

                    sourceFileBytes
                            .windowed(headerBytes.size)
                            .mapIndexed { index, list -> index to list }
                            .filter { it.second == headerBytes }
                            .map { it.first }

                            // Get List of Bytes from Header to max bytes amount
                            .map {
                                // Prevent overflow by choosing min of max bytes, or the end of the
                                // byte stream
                                val end = minOf(it + rule.maxBytesAmount, sourceFileBytes.size)
                                sourceFileBytes.subList(it, end)
                            }

                            // If footer bytes are null, then save the entire List<Byte> as a file
                            .filter {
                                if (footerBytes == null) {
                                    saveToFile(it, rule, dirForRule).let {
                                        carvedFiles.add(it)
                                    }

                                    return@filter false
                                }

                                return@filter true
                            }

                            // If not, then iterate over the window to find the footer
                            .windowed(footerBytes!!.size)
                            .mapIndexed { index, list -> index to list }
                            .filter { it.second == footerBytes }
                            // Map to index of footer window
                            .map { it.first }

                            // Reverse it if rule says so
                            .run {
                                if (rule.reverseSearchFooter) {
                                    this.reversed()
                                }

                                this
                            }

                            // Ensure a minimum carve size
                            .filter {
                                val carveSize = if (rule.skipFooter) {
                                    it
                                } else {
                                    it + footerBytes.size
                                }

                                carveSize >= rule.minBytesAmount
                            }


                    carvedFiles.toFlowable()
                }
                .sequential()
                .observeOn(RxSchedulers.main)
                .subscribe({
                    allCarvedFiles.add(it)
                    liveCarvedFiles.value = allCarvedFiles
                }, {
                    Timber.e(it, "Error while carving data!")
                }, {
                    // TODO  Update UI after carving complete
                })
    }

    private fun saveToFile(list: List<Byte>, rule: Rule, dirForRule: File): String {
        val carvedFileBytes = list.toByteArray()
        val carvedFile = File(dirForRule, generateCarvedFileName(rule))
        carvedFile.writeBytes(carvedFileBytes)
        return carvedFile.absolutePath
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
