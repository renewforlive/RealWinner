package com.peter.realwinner.model.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.peter.realwinner.model.db.entity.AdvertisingEntity
import com.peter.realwinner.model.db.entity.Entitlements

@Dao
interface EntitlementsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(advertisingEntity: AdvertisingEntity)

    @Update
    fun update(advertisingEntity: AdvertisingEntity)

    @Delete
    fun delete(advertisingEntity: AdvertisingEntity)

    @Query("SELECT * FROM advertising LIMIT 1")
    fun getAdvertisingEntity() : LiveData<AdvertisingEntity>

    @Transaction
    fun insert(vararg entitlements: Entitlements) {
        entitlements.forEach {
            when (it) {
                is AdvertisingEntity -> insert(it)
            }
        }
    }

    @Transaction
    fun update(vararg entitlements: Entitlements) {
        entitlements.forEach {
            when (it) {
                is AdvertisingEntity -> update(it)
            }
        }
    }
}