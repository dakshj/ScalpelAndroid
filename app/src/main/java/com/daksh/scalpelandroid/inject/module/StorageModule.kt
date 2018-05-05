package com.daksh.scalpelandroid.inject.module

import android.content.Context
import com.daksh.scalpelandroid.storage.prefs.AppSettings
import com.daksh.scalpelandroid.storage.prefs.AppSettingsImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class StorageModule {

    @Provides
    @Singleton
    fun provideAppSettings(context: Context): AppSettings = AppSettingsImpl(context)
}
