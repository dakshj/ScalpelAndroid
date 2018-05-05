package com.daksh.scalpelandroid.screens.carve

import android.arch.lifecycle.MutableLiveData
import com.daksh.scalpelandroid.rx.RxAwareViewModel
import com.daksh.scalpelandroid.storage.prefs.AppSettings
import java.io.File
import javax.inject.Inject

class CarveViewModel @Inject constructor(
        private val appSettings: AppSettings
) : RxAwareViewModel() {

    val liveSelectedFilePath: MutableLiveData<String> = MutableLiveData()

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
        set(value) {
            value?.absolutePath?.let {
                appSettings.selectedFile = it
                liveSelectedFilePath.value = it
            }
        }
}
