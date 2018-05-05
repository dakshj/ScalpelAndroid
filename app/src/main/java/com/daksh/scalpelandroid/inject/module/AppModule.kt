package com.daksh.scalpelandroid.inject.module

import com.daksh.scalpelandroid.App
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideContext(app: App) = app.applicationContext
}
