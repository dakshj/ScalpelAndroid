package com.daksh.scalpelandroid.applibraries

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThreeTenAbpLibrary @Inject constructor() : AppLibrary {

    override fun init(application: Application) {
        AndroidThreeTen.init(application)
    }
}
