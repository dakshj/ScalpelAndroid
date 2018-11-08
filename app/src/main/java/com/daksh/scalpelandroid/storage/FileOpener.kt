package com.daksh.scalpelandroid.storage

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.daksh.scalpelandroid.BuildConfig
import java.io.File

object FileOpener {

    fun open(file: File, context: Context) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            val uri = FileProvider.getUriForFile(context,
                    "${BuildConfig.APPLICATION_ID}.provider", file)
            intent.setDataAndType(uri, getMimeType(file))
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // no Activity to handle this kind of files
        }
    }

    private fun getMimeType(file: File) =
            MimeTypeMap.getFileExtensionFromUrl(file.absolutePath).let {
                MimeTypeMap.getSingleton().getMimeTypeFromExtension(it)
            }
}
