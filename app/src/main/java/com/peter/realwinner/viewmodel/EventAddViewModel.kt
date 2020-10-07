package com.peter.realwinner.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.peter.realwinner.R
import com.peter.realwinner.model.db.dao.EventDao
import com.peter.realwinner.model.db.entity.EventEntity
import com.peter.realwinner.model.repository.EventNotificationRepository
import com.peter.realwinner.util.SharedPreferencesUtil
import com.peter.realwinner.util.TimeUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject

class EventAddViewModel(application: Application) : AndroidViewModel(application) {

    private val context: Context = application
    private val sharedPreferencesUtil by inject(SharedPreferencesUtil::class.java)

    private val eventDao by inject(EventDao::class.java)
    private val eventNotificationRepository by inject(EventNotificationRepository::class.java)
    private var eventEntity: EventEntity? = null
    private var startTime = String()

    val errorMsgLiveData = MutableLiveData<String>()
    val validateLiveDate = MutableLiveData<Boolean>()
    val saveLiveData = MutableLiveData<Boolean>()

    fun getTheme() : MutableLiveData<Int> {
        return MutableLiveData(sharedPreferencesUtil.getStyle())
    }

    fun validate(eventEntity: EventEntity) {
        if (eventEntity.title.isBlank()) {
            errorMsgLiveData.value = context.getString(R.string.event_add_validate_title_error)
            validateLiveDate.value = false
            return
        }
        if (eventEntity.content.isBlank()) {
            errorMsgLiveData.value = context.getString(R.string.event_add_validate_content_error)
            validateLiveDate.value = false
            return
        }
        if (eventEntity.startTime.isBlank()) {
            errorMsgLiveData.value = context.getString(R.string.event_add_validate_start_time_error)
            validateLiveDate.value = false
            return
        }
        this.eventEntity = eventEntity
        validateLiveDate.value = true
    }

    fun saveInfoToDb() {
        viewModelScope.launch {
            eventEntity?.let {
                val id = withContext(Dispatchers.IO) {
                    eventDao.updateIfExist(it)
                }
                if (it.isNotification == 1 && id.toInt() != -1) {
                    val startTime = TimeUtil.getTimestampByDate(it.startTime)
                    eventNotificationRepository.setupNotification(id.toInt(), System.currentTimeMillis() + 5000)
                }
                saveLiveData.value = true
            }
        }
    }

    fun setStartTime(time: String) {
        startTime = time
    }

    fun getStartTime() : String {
        return startTime
    }
}