package com.daksh.scalpelandroid.inject.module

import android.content.Context
import com.daksh.scalpelandroid.App
import com.daksh.scalpelandroid.R
import com.daksh.scalpelandroid.applibraries.AppLibrariesInitializer
import com.daksh.scalpelandroid.applibraries.ThreeTenAbpLibrary
import com.daksh.scalpelandroid.applibraries.TimberLibrary
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideContext(app: App) = app.applicationContext

    @Provides
    @Singleton
    @Named("APP_NAME")
    fun provideAppName(context: Context) = context.getString(R.string.app_name)

    @Provides
    @Singleton
    fun provideAppLibrariesInitializer(timberLibrary: TimberLibrary,
            threeTenAbpLibrary: ThreeTenAbpLibrary) =
            AppLibrariesInitializer(listOf(
                    timberLibrary,
                    threeTenAbpLibrary
            ))
}
