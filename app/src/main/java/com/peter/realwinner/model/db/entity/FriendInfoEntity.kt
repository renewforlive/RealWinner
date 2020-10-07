package com.peter.realwinner.model.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friend_info")
data class FriendInfoEntity (
    @PrimaryKey(autoGenerate = true)
    val friendId: Int = 0,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "nickname")
    val nickName: String? = null,
    @ColumnInfo(name = "height")
    val height: Int? = null,
    @ColumnInfo(name = "post")
    val post: String? = null,
    @ColumnInfo(name = "birth")
    val birth: String? = null,
    @ColumnInfo(name = "personality")
    val personality: String? = null,
    @ColumnInfo(name = "blood")
    val blood: String? = null,
    @ColumnInfo(name = "cellphone")
    val cellphone: String? = null,
    @ColumnInfo(name = "line")
    val line: String? = null,
    @ColumnInfo(name = "facebook")
    val facebook: String? = null,
    @ColumnInfo(name = "instagram")
    val instagram: String? = null,
    @ColumnInfo(name = "hobby")
    val hobby: String? = null,
    @ColumnInfo(name = "other")
    val other: String? = null
)