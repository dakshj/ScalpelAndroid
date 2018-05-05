package com.daksh.scalpelandroid.storage.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.daksh.scalpelandroid.storage.room.dao.RuleDao
import com.daksh.scalpelandroid.storage.room.entity.Rule

@Database(
        entities = [
            Rule::class
        ],
        version = 1
)
abstract class Database : RoomDatabase() {

    abstract fun ruleDao(): RuleDao
}
