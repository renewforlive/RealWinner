package com.peter.realwinner.model.repository

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.peter.realwinner.model.db.dao.EventDao
import com.peter.realwinner.model.db.entity.EventEntity
import com.peter.realwinner.util.TimeUtil
import kotlinx.coroutines.*
import org.koin.java.KoinJavaComponent.inject

class EventRepository {

    companion object {
        private const val PAGE_SIZE = 30
        private const val DISTANCE = 5
    }

    private val eventDao by inject(EventDao::class.java)
    private val eventNotificationRepository by inject(EventNotificationRepository::class.java)

    fun getList(time: String) : LiveData<PagedList<EventEntity>> {
        val dataSourceFactory = eventDao.getEventByTime(time)
        return LivePagedListBuilder(dataSourceFactory, getConfig()).build()
    }

    private fun getConfig() : PagedList.Config {
        return PagedList.Config.Builder()
            .setPageSize(PAGE_SIZE)
            .setInitialLoadSizeHint(PAGE_SIZE * 2)
            .setPrefetchDistance(DISTANCE)
            .build()
    }

    fun updateNotification(isChecked: Boolean, eventEntity: EventEntity) {
        CoroutineScope(Job() + Dispatchers.IO).launch {
            val newEntity = EventEntity (
                eventId = eventEntity.eventId,
                title = eventEntity.title,
                content = eventEntity.content,
                isNotification = if (isChecked) 1 else 0,
                insertTime = System.currentTimeMillis(),
                startTime = eventEntity.startTime
            )
            eventDao.updateIfExist(newEntity)

            if (isChecked) {
                val time = TimeUtil.getTimestampByDate(eventEntity.startTime)
                eventNotificationRepository.setupNotification(eventEntity.eventId, time)
            }
        }
    }
}