package com.peter.realwinner.model.db.dao

import androidx.paging.DataSource
import androidx.room.*
import com.peter.realwinner.model.db.entity.EventEntity

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(eventEntity: EventEntity) : Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(eventEntity: EventEntity)

    @Transaction
    fun updateIfExist(eventEntity: EventEntity) : Long {
        val id = insert(eventEntity)
        if (id == -1L) {
            update(eventEntity)
        }
        return id
    }

    @Delete
    fun delete(eventEntity: EventEntity)

    @Query("SELECT * FROM event WHERE start_time=:date ORDER BY insert_time ASC")
    fun getEventByTime(date: String) : DataSource.Factory<Int, EventEntity>

    @Query("SELECT * FROM event WHERE eventId=:eventId")
    suspend fun getEventById(eventId: Int): List<EventEntity>
}