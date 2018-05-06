package com.daksh.scalpelandroid.screens.carve

import android.arch.lifecycle.MutableLiveData
import com.daksh.scalpelandroid.rx.RxAwareViewModel
import com.daksh.scalpelandroid.rx.RxSchedulers
import com.daksh.scalpelandroid.storage.DirectoryManager
import com.daksh.scalpelandroid.storage.prefs.AppSettings
import com.daksh.scalpelandroid.storage.room.dao.RuleDao
import com.daksh.scalpelandroid.storage.room.entity.Rule
import io.reactivex.Single
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

    val liveSelectedFilePath: MutableLiveData<String> = MutableLiveData()
    val liveCarving: MutableLiveData<Boolean> = MutableLiveData()

    init {
        liveSelectedFilePath.value = appSettings.selectedFile
    }

    var selectedFile: File?
        get() {
            appSettings.selectedFile?.let {
                return File(it)
            }

            return null
        }
        set(file) {
            file?.run { if (this.exists()) this else null }?.absolutePath?.let {
                appSettings.selectedFile = it
                liveSelectedFilePath.value = it
            }
        }

    fun carve() {
        liveCarving.value = true

        lateinit var fileBytes: ByteArray

        val currentRunDirectory = directoryManager.getCurrentRunDirectory()

        readFileToCarve()
                .doOnSuccess { fileBytes = it }

                .observeOn(RxSchedulers.database)
                .flatMap { Single.fromCallable { ruleDao.getAll() } }
                .toFlowable()
                .flatMapIterable { it }
                .parallel()
                .runOn(RxSchedulers.disk)
                .map {
                    val carvedFile: ByteArray = ByteArray(1)

                    // TODO Actually carve the file here

                    it to carvedFile
                }
                .sequential()
                .subscribe({
                    val dirForRule = directoryManager
                            .getDirectoryForRule(it.first, currentRunDirectory)

                    val carvedFile = File(dirForRule, generateCarvedFileName(it.first))
                    carvedFile.writeBytes(it.second)

                    // TODO Update UI after each file carved
                }, {
                    Timber.e(it, "Error while carving data!")
                }, {
                    // TODO  Update UI after carving complete
                })
    }

    private fun generateCarvedFileName(rule: Rule): String =
            "${LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)}.${rule.extension}"

    private fun readFileToCarve(): Single<ByteArray> {
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
}
