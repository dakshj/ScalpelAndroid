package com.daksh.scalpelandroid.applibraries

import android.app.Application
import com.daksh.scalpelandroid.BuildConfig
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimberLibrary @Inject constructor() : AppLibrary {

    override fun init(application: Application) {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
