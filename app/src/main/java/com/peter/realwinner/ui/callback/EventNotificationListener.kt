package com.peter.realwinner.ui.callback

import com.peter.realwinner.model.db.entity.EventEntity

interface EventNotificationListener {
    fun onNotification(isChecked: Boolean, eventEntity: EventEntity)
}