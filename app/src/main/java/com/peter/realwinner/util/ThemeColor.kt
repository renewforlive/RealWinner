package com.peter.realwinner.util

import android.app.Activity
import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import kotlin.math.round

class ThemeColor(context: Context) {

    companion object {
        private const val NAME = "ThemeColors"
        private const val KEY = "color"

        fun setNewThemeColor(activity: Activity, red: Int, green: Int, blue: Int) {
            val colorStep: Int = 15
            val newRed = round((red / colorStep).toDouble()).toInt() * colorStep
            val newGreen = round((green / colorStep).toDouble()).toInt() * colorStep
            val newBlue = round((blue / colorStep).toDouble()).toInt() * colorStep
            val stringColor = Integer.toHexString(Color.rgb(newRed, newGreen, newBlue)).substring(2)
            val editor = activity.getSharedPreferences(NAME, Context.MODE_PRIVATE).edit()
            editor.putString(KEY, stringColor)
            editor.apply()

            activity.recreate()
        }
    }

    @ColorInt
    var color = 0

    init {
        val sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
        val stringColor = sharedPreferences.getString(KEY, "ffffff")
        color = Color.parseColor("#$stringColor")
        context.setTheme(context.resources.getIdentifier("T_$stringColor", "style", context.packageName))
    }
}