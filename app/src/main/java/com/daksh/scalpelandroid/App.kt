package com.daksh.scalpelandroid

import com.daksh.scalpelandroid.applibraries.AppLibrariesInitializer
import com.daksh.scalpelandroid.inject.component.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import javax.inject.Inject

class App : DaggerApplication() {

    @Inject
    lateinit var appLibrariesInitializer: AppLibrariesInitializer

    override fun onCreate() {
        super.onCreate()
        appLibrariesInitializer.init(this)
    }

    override fun applicationInjector(): AndroidInjector<App> =
            DaggerAppComponent.builder().create(this)
}
