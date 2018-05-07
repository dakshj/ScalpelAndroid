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
                ),
                Rule(
                        extension = "png",
                        maxBytesAmount = 20 * 1024 * 1024, // 20 MB
                        header = "\\x89\\x50\\x4E\\x47\\x0D\\x0A\\x1A\\x0A",
                        footer = "\\x49\\x45\\x4E\\x44\\xAE\\x42\\x60\\x82"
                ),
                Rule(
                        extension = "tif",
                        maxBytesAmount = 40 * 1024 * 1024, // 40 MB
                        header = "\\x49\\x49\\x2a\\x00",
                        forceSave = true
                ),
                Rule(
                        extension = "tif",
                        maxBytesAmount = 40 * 1024 * 1024, // 40 MB
                        header = "\\x4D\\x4D\\x00\\x2A",
                        forceSave = true
                ),

                // Microsoft Office Open XML format: DOCX, PPTX, XLSX
                // Can't say which file directly by carving it
                Rule(
                        extension = "ooxml",
                        maxBytesAmount = 200 * 1024 * 1024, // 200 MB
                        header = "\\x50\\x4B\\x03\\x04\\x14\\x00\\x06\\x00",
                        footer = "\\x50\\x4B\\x05\\x06??????????????????"
                ),

                Rule(
                        extension = "doc",
                        maxBytesAmount = 200 * 1024 * 1024, // 200 MB
                        header = "\\xd0\\xcf\\x11\\xe0\\xa1\\xb1\\x1a\\xe1\\x00\\x00",
                        footer = "\\xd0\\xcf\\x11\\xe0\\xa1\\xb1\\x1a\\xe1\\x00\\x00",
                        skipFooter = true
                ),

                Rule(
                        extension = "doc",
                        maxBytesAmount = 200 * 1024 * 1024, // 200 MB
                        header = "\\xd0\\xcf\\x11\\xe0\\xa1\\xb1\\x1a\\xe1\\x00\\x00"
                ),

                Rule(
                        extension = "zip",
                        maxBytesAmount = 200 * 1024 * 1024, // 200 MB
                        header = "PK\\x03\\x04",
                        footer = "\\x3c\\xac"
                ),

                Rule(
                        extension = "java",
                        maxBytesAmount = 5 * 1024 * 1024, // 5 MB
                        header = "\\xca\\xfe\\xba\\xbe"
                ),

                Rule(
                        extension = "mpg",
                        maxBytesAmount = 200 * 1024 * 1024, // 200 MB
                        header = "\\x00\\x00\\x01\\xba",
                        footer = "\\x00\\x00\\x01\\xb9"
                ),

                Rule(
                        extension = "mpg",
                        maxBytesAmount = 200 * 1024 * 1024, // 200 MB
                        header = "\\x00\\x00\\x01\\xb3",
                        footer = "\\x00\\x00\\x01\\xb7"
                ),

                Rule(
                        extension = "rtf",
                        maxBytesAmount = 200 * 1024 * 1024, // 200 MB
                        header = "\\x7B\\x5C\\x72\\x74\\x66\\x31",
                        footer = "\\x5C\\x70\\x61\\x72\\x20\\x7D\\x7D"
                )
        )
    }
}
