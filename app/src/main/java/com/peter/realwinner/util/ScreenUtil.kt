package com.peter.realwinner.util

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
import com.peter.realwinner.RealWinnerApplication

object ScreenUtil {

    fun getScreenWidth() : Int {
        val windowManager : WindowManager = RealWinnerApplication.getBaseContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        return dm.widthPixels
    }

    fun getScreenHeight() : Int {
        val windowManager : WindowManager = RealWinnerApplication.getBaseContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        return dm.heightPixels
    }
}