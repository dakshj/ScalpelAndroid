package com.daksh.scalpelandroid.storage

import android.content.Context
import com.daksh.scalpelandroid.R
import com.daksh.scalpelandroid.storage.room.entity.Rule
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DirectoryManager @Inject constructor(
        private val context: Context
) {

    private fun getRootDirectory(): File {
        val rootDir = File(context.filesDir, context.getString(R.string.app_name))

        if (!rootDir.exists()) {
            rootDir.mkdirs()
        }

        return rootDir
    }

    fun getCurrentRunDirectory(): File {
        val runDir = File(getRootDirectory(), LocalDateTime.now()
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))

        runDir.mkdirs()

        return runDir
    }

    fun getDirectoryForRule(rule: Rule, runDirectory: File): File {
        val dirForRule = File(runDirectory, rule.extension)
        if (!dirForRule.exists()) dirForRule.mkdirs()
        return dirForRule
    }
}
