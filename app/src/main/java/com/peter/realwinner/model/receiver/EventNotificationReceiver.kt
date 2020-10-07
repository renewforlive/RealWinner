package com.peter.realwinner.model.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.peter.realwinner.R
import com.peter.realwinner.RealWinnerApplication
import com.peter.realwinner.constant.BundleType
import com.peter.realwinner.model.db.dao.EventDao
import com.peter.realwinner.model.db.entity.EventEntity
import com.peter.realwinner.util.LogUtil
import kotlinx.coroutines.*
import org.koin.java.KoinJavaComponent.inject

class EventNotificationReceiver : BroadcastReceiver() {

    companion object {
        private const val CHANNEL_ID = "event_notification"
    }

    private val eventDao by inject(EventDao::class.java)

    private var notificationId = 0

    init {
        createNotificationChannel()
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                LogUtil.showInfoLog("onReceiver ACTION_BOOT_COMPLETED")
                val eventId = intent.getIntExtra(BundleType.EVENT_ID, 0)
                notificationId = eventId
                CoroutineScope(Dispatchers.IO).launch {
                    val result = eventDao.getEventById(eventId)
                    if (result.isNotEmpty()) {
                        showNotification(result[0])
                    }
                }
            }
            else -> {
                val eventId = intent?.getIntExtra(BundleType.EVENT_ID, 0)
                LogUtil.showInfoLog("onReceiver other eventId = $eventId")
                eventId?.let {
                    notificationId = it
                    CoroutineScope(Dispatchers.IO).launch {
                        val result = eventDao.getEventById(it)
                        LogUtil.showInfoLog("onReceiver other result = $result")
                        if (result.isNotEmpty()) {
                            showNotification(result[0])
                        }
                    }
                }
            }
        }
    }

    private fun showNotification(eventEntity: EventEntity) {
        val builder = NotificationCompat.Builder(RealWinnerApplication.getBaseContext(), CHANNEL_ID)
        builder.setSmallIcon(R.drawable.ic_calendar)
        builder.setContentTitle(eventEntity.title)
        builder.setContentText(eventEntity.content)
        builder.setAutoCancel(true)
        builder.setDefaults(NotificationCompat.DEFAULT_ALL)
        with(NotificationManagerCompat.from(RealWinnerApplication.getBaseContext())) {
            notify(notificationId, builder.build())
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = RealWinnerApplication.getBaseContext().getString(R.string.event_notification_channel_name)
            val descriptionText = RealWinnerApplication.getBaseContext().getString(R.string.event_notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                RealWinnerApplication.getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}