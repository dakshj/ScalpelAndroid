package com.daksh.scalpelandroid.screens.carve

import android.arch.lifecycle.MutableLiveData
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

        lateinit var sourceFileBytes: ByteArray

        val currentRunDirectory = directoryManager.getCurrentRunDirectory()

        readSourceFile()
                .doOnSuccess { sourceFileBytes = it }

                .observeOn(RxSchedulers.database)
                .flatMap { Single.fromCallable { ruleDao.getAll() } }
                .toFlowable()
                .flatMapIterable { it }
                .parallel()
                .runOn(RxSchedulers.disk)
                .flatMap {
                    val dirForRule = directoryManager.getDirectoryForRule(it, currentRunDirectory)
                    val carvedFiles = mutableListOf<String>()

                    // TODO Check if sourceFileBytes has been set properly or not
                    // TODO Carve files in sourceFileBytes

                    // For each found file:
                    val carvedFileBytes = ByteArray(1)
                    val carvedFile = File(dirForRule, generateCarvedFileName(it))
                    carvedFile.writeBytes(carvedFileBytes)
                    carvedFiles.add(carvedFile.absolutePath)
                    //

                    carvedFiles.toFlowable()
                }
                .sequential()
                .observeOn(RxSchedulers.main)
                .subscribe({
                    allCarvedFiles.add(it)
                    liveCarvedFiles.value = allCarvedFiles
                    // TODO Update UI after each file carved
                }, {
                    Timber.e(it, "Error while carving data!")
                }, {
                    // TODO  Update UI after carving complete
                })
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
        TODO("not implemented")
    }
}
