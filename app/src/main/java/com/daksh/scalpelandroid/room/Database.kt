package com.daksh.scalpelandroid.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.daksh.scalpelandroid.room.dao.RuleDao
import com.daksh.scalpelandroid.room.entity.Rule

@Database(
        entities = [
            Rule::class
        ],
        version = 1
)
abstract class Database : RoomDatabase() {

    abstract fun ruleDao(): RuleDao
}
