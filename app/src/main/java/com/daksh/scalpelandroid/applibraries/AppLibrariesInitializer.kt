package com.daksh.scalpelandroid.applibraries

import android.app.Application

/**
 * Accepts a [List] of [AppLibrary]s, and initializes them.
 */
class AppLibrariesInitializer(private val libraries: List<AppLibrary>) : AppLibrary {

    override fun init(application: Application) {
        libraries.forEach { it.init(application) }
    }
}
