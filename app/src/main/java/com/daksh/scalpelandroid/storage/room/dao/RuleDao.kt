package com.daksh.scalpelandroid.storage.room.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.daksh.scalpelandroid.storage.room.entity.Rule
import io.reactivex.Flowable

@Dao
abstract class RuleDao : BaseDao<Rule> {

    @Query("SELECT * FROM Rule")
    abstract override fun getAll(): List<Rule>

    @Query("SELECT * FROM Rule")
    abstract override fun getAllRx(): Flowable<List<Rule>>

    @Query("SELECT COUNT(*) FROM Rule")
    abstract fun getCount(): Int

    fun isEmpty() = getCount() == 0

    @Query("DELETE FROM Rule")
    abstract fun clear()
}
