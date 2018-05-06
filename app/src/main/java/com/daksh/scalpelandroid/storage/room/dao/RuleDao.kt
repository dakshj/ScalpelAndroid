package com.daksh.scalpelandroid.storage.room.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction
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

    @Transaction
    open fun addStaticRules() {
        if (!this.isEmpty()) {
            this.clear()
        }

        this.insert(
                Rule(
                        extension = "jpg",
                        maxBytesAmount = 25 * 1024 * 1024,// 25 MB
                        header = "\\xff\\xd8\\xff\\xe1",
                        footer = "\\xff\\xd9"
                ),
                Rule(
                        extension = "gif",
                        maxBytesAmount = 30 * 1024 * 1024, // 30 MB
                        header = "\\x47\\x49\\x46\\x38\\x37\\x61",
                        footer = "\\x00\\x3b"
                ),
                Rule(
                        extension = "gif",
                        maxBytesAmount = 30 * 1024 * 1024, // 30 MB
                        header = "\\x47\\x49\\x46\\x38\\x39\\x61",
                        footer = "\\x00\\x00\\x3b"
                ),
                Rule(
                        extension = "html",
                        maxBytesAmount = 10 * 1024 * 1024, // 10 MB
                        header = "<html>",
                        footer = "</html>"
                ),
                Rule(
                        extension = "pdf",
                        maxBytesAmount = 20 * 1024 * 1024, // 20 MB
                        header = "%PDF",
                        footer = "%EOF\\x0d"
                ),
                Rule(
                        extension = "pdf",
                        maxBytesAmount = 20 * 1024 * 1024, // 20 MB
                        header = "%PDF",
                        footer = "%EOF\\x0a"
                )
        )
    }
}
