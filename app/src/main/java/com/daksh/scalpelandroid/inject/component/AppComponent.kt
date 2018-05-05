package com.daksh.scalpelandroid.inject.component

import com.daksh.scalpelandroid.App
import com.daksh.scalpelandroid.inject.module.AndroidInjectionContributorModule
import com.daksh.scalpelandroid.inject.module.AppModule
import com.daksh.scalpelandroid.inject.module.DatabaseModule
import com.daksh.scalpelandroid.inject.module.StorageModule
import com.daksh.scalpelandroid.inject.viewmodel.ViewModelModule
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    AppModule::class,
    DatabaseModule::class,
    StorageModule::class,
    ViewModelModule::class,
    AndroidInjectionContributorModule::class
])
interface AppComponent : AndroidInjector<App> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<App>()
}
