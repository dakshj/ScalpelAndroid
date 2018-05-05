package com.daksh.scalpelandroid.storage.prefs

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.core.content.edit
import com.daksh.scalpelandroid.R

class AppSettingsImpl(private val context: Context) : AppSettings {

    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    private fun getString(resId: Int): String = context.getString(resId)

    override var selectedFile: String?
        get() = prefs.getString(getString(R.string.prefs_key_selected_file), null)
        set(value) {
            value?.let {
                prefs.edit {
                    putString(getString(R.string.prefs_key_selected_file), it)
                }
            }
        }
}
