package com.peter.realwinner.util

import com.peter.realwinner.model.data.TimeModel
import java.text.SimpleDateFormat
import java.util.*

object TimeUtil {
    fun getTimeModelByTimestamp(timestamp: Long) : TimeModel {
        val realTimestamp = timestamp * 1000
        val df = SimpleDateFormat("yyyy-MM-dd-HH-mm", Locale.getDefault())
        val realTimeString = df.format(realTimestamp)
        val year = realTimeString.split("-")[0].toInt()
        val month = realTimeString.split("-")[1].toInt()
        val day = realTimeString.split("-")[2].toInt()
        val hour = realTimeString.split("-")[3].toInt()
        val minute = realTimeString.split("-")[4].toInt()
        return TimeModel(year, month, day, hour, minute)
    }

    fun getDateByTimestamp(timestamp: Long): String {
        val realTimestamp = timestamp * 1000
        val df = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        return df.format(realTimestamp)
    }

    fun getTimestampByDate(date: String): Long {
        val df = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val time = df.parse(date)
        return time?.time ?: 0L
    }
}