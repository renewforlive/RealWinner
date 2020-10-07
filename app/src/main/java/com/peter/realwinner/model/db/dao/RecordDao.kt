package com.peter.realwinner.model.db.dao

import androidx.paging.DataSource
import androidx.room.*
import com.peter.realwinner.model.db.entity.RecordEntity

@Dao
interface RecordDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(recordEntity: RecordEntity) : Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(recordEntity: RecordEntity)

    @Transaction
    fun updateIfExist(recordEntity: RecordEntity) {
        if (insert(recordEntity) == -1L) {
            update(recordEntity)
        }
    }

    @Delete
    fun delete(recordEntity: RecordEntity)

    @Query("DELETE FROM record WHERE recordId=:recordId")
    fun deleteById(recordId: Int)

    @Query("SELECT * FROM record ORDER BY time DESC")
    fun getRecord() : DataSource.Factory<Int, RecordEntity>

    @Query("SELECT * FROM record WHERE recordId=:recordId")
    suspend fun getRecordById(recordId: Int) : RecordEntity?

}