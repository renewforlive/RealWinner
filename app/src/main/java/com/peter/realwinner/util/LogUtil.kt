package com.peter.realwinner.util

import android.util.Log

object LogUtil {
    private const val TAG = "RealWinner"

    sealed class LogLevel {
        object ERROR : LogLevel()
        object INFO : LogLevel()
        object DEBUG : LogLevel()
        object VERBOSE : LogLevel()
        object WARNING : LogLevel()
        object ASSERT : LogLevel()
    }

    private fun showLog(level: LogLevel, msg: String) {
        val priority = when (level) {
            LogLevel.ERROR -> Log.ERROR
            LogLevel.DEBUG -> Log.DEBUG
            LogLevel.INFO -> Log.INFO
            LogLevel.VERBOSE -> Log.VERBOSE
            LogLevel.WARNING -> Log.WARN
            LogLevel.ASSERT -> Log.ASSERT
        }
        Log.println(priority, TAG, msg)
    }

    fun showDebugLog(msg: String) {
        showLog(LogLevel.DEBUG, msg)
    }

    fun showInfoLog(msg: String) {
        showLog(LogLevel.INFO, msg)
    }

    fun showWarning(msg: String) {
        showLog(LogLevel.WARNING, msg)
    }

    fun showError(msg: String) {
        showLog(LogLevel.ERROR, msg)
    }

    fun showAssert(msg: String) {
        showLog(LogLevel.ASSERT, msg)
    }

    fun showVerbose(msg: String) {
        showLog(LogLevel.VERBOSE, msg)
    }
}