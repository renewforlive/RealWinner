package com.peter.realwinner.model.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "record")
data class RecordEntity (
    @PrimaryKey(autoGenerate = true)
    val recordId: Int = 0,
    @ColumnInfo
    val post: String,
    @ColumnInfo
    val postType: String,
    @ColumnInfo
    val title: String,
    @ColumnInfo
    val time: Long,
    @ColumnInfo
    val content: String
)