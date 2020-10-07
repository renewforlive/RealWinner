package com.peter.realwinner.model.db.dao

import androidx.paging.DataSource
import androidx.room.*
import com.peter.realwinner.model.db.entity.FriendInfoEntity

@Dao
interface FriendInfoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(friendInfoEntity: FriendInfoEntity) : Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(friendInfoEntity: FriendInfoEntity)

    @Transaction
    fun updateIfExist(friendInfoEntity: FriendInfoEntity) {
        if (insert(friendInfoEntity) == -1L) {
            update(friendInfoEntity)
        }
    }

    @Delete
    fun delete(friendInfoEntity: FriendInfoEntity)

    @Query("SELECT * FROM friend_info")
    fun getFriendInfoList() : DataSource.Factory<Int, FriendInfoEntity>

    @Query("UPDATE friend_info SET post=:post WHERE friendId=:friendId")
    fun updatePost(friendId: Int, post: String)

    @Query("SELECT * FROM friend_info WHERE friendId=:friendId")
    suspend fun getFriendInfoById(friendId: Int) : List<FriendInfoEntity>
}