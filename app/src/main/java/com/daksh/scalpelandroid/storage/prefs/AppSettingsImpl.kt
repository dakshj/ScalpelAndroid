package com.daksh.scalpelandroid.storage.prefs

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class AppSettingsImpl(private val context: Context) : AppSettings {

    // NOTE Clear any more user-specific data necessary in clearUserData()

    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    private fun getString(resId: Int): String = context.getString(resId)
}
