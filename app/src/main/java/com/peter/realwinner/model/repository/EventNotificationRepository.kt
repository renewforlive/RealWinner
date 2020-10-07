package com.peter.realwinner.model.repository

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.peter.realwinner.RealWinnerApplication
import com.peter.realwinner.constant.BundleType
import com.peter.realwinner.model.receiver.EventNotificationReceiver
import com.peter.realwinner.util.LogUtil

class EventNotificationRepository {

    companion object {
        private const val REQUEST_CODE = 0
    }

    fun setupNotification(eventId: Int, time: Long) {
        val intent = Intent(RealWinnerApplication.getBaseContext(), EventNotificationReceiver::class.java)
        LogUtil.showInfoLog("setupNotification, eventId= $eventId")
        intent.putExtra(BundleType.EVENT_ID, eventId)
        val pendingIntent = PendingIntent.getBroadcast(RealWinnerApplication.getBaseContext(), REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = RealWinnerApplication.getBaseContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent)
    }

    fun cancelNotification() {
        val intent = Intent(RealWinnerApplication.getBaseContext(), EventNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(RealWinnerApplication.getBaseContext(), REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = RealWinnerApplication.getBaseContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }
}