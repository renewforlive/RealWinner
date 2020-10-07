package com.peter.realwinner.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.peter.realwinner.model.db.entity.EventEntity
import com.peter.realwinner.model.repository.EventRepository
import com.peter.realwinner.util.LogUtil
import com.peter.realwinner.util.TimeUtil
import org.koin.java.KoinJavaComponent.inject
import java.util.*

class EventViewModel : ViewModel() {

    private val eventRepository by inject(EventRepository::class.java)

    private var currentTime = String()

    init {
        val time = System.currentTimeMillis()
        currentTime = TimeUtil.getDateByTimestamp(time / 1000)
    }

    fun getList(time: String): LiveData<PagedList<EventEntity>> {
        return eventRepository.getList(time)
    }

    fun setTime(year: Int, month: Int, day: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        val date = TimeUtil.getDateByTimestamp(calendar.timeInMillis / 1000)
        currentTime = date
    }

    fun getCurrentTime() : String {
        LogUtil.showInfoLog("currentTime=$currentTime")
        return currentTime
    }

    fun updateEvent(isChecked: Boolean, eventEntity: EventEntity) {
        eventRepository.updateNotification(isChecked, eventEntity)
    }
}