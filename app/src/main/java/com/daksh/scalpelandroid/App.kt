package com.daksh.scalpelandroid

import com.daksh.scalpelandroid.inject.component.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication

class App : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<App> =
            DaggerAppComponent.builder().create(this)
}
