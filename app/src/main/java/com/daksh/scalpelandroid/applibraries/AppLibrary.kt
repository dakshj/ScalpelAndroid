package com.daksh.scalpelandroid.applibraries

import android.app.Application

/**
 * An interface implemented by all classes that are responsible for initializing libraries that
 * require an [Application] context and need to be set up at app start.
 */
interface AppLibrary {

    /**
     * Uses [application] to initialize itself
     */
    fun init(application: Application)
}
