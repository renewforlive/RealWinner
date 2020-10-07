package com.peter.realwinner.util

import android.content.Context
import android.content.SharedPreferences
import com.peter.realwinner.constant.ThemeType

class SharedPreferencesUtil(context: Context) {

    companion object {
        private const val NAME = "real_winner"

        private const val STYLE = "style"
        private const val SHOW_AD = "show_ad"
    }

    private val editor : SharedPreferences.Editor by lazy {
        sharedPreferences.edit()
    }

    private val sharedPreferences : SharedPreferences by lazy {
        context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
    }

    fun setStyle(style: Int) {
        editor.putInt(STYLE, style).apply()
    }

    fun getStyle() : Int {
        return sharedPreferences.getInt(STYLE, ThemeType.DEFAULT_THEME)
    }

    fun setIsAd(isAd: Boolean) {
        editor.putBoolean(SHOW_AD, isAd).apply()
    }

    fun getIsAd() : Boolean {
        return sharedPreferences.getBoolean(SHOW_AD, true)
    }
}