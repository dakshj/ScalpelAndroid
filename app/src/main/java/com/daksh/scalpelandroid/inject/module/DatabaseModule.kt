package com.daksh.scalpelandroid.inject.module

import android.arch.persistence.room.Room
import android.content.Context
import com.daksh.scalpelandroid.storage.room.Database
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(context: Context, @Named("APP_NAME") appName: String) =
            Room.databaseBuilder(context, Database::class.java, "$appName.db")
                    .fallbackToDestructiveMigration().build()

    @Provides
    @Singleton
    fun provideRuleDao(database: Database) = database.ruleDao()
}
