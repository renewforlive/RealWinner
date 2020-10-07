package com.peter.realwinner.model.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event")
data class EventEntity (
    @PrimaryKey(autoGenerate = true)
    val eventId: Int = 0,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "content")
    val content: String,
    @ColumnInfo(name = "is_notification")
    val isNotification: Int,
    @ColumnInfo(name = "start_time")
    val startTime: String,
    @ColumnInfo(name = "insert_time")
    val insertTime: Long
)