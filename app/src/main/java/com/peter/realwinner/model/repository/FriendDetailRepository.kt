package com.peter.realwinner.model.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.peter.realwinner.model.db.dao.FriendInfoDao
import com.peter.realwinner.model.db.entity.FriendInfoEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject

class FriendDetailRepository {

    private val friendInfoDao by inject(FriendInfoDao::class.java)

    fun getFriendInfo(friendId: Int) : LiveData<FriendInfoEntity> {
        val info =  MutableLiveData<FriendInfoEntity>()
        CoroutineScope(Dispatchers.Main).launch {
            val result = withContext(Dispatchers.IO) {
                friendInfoDao.getFriendInfoById(friendId)
            }
            if (result.isNotEmpty()) {
                val friendInfoEntity = result[0]
                info.postValue(friendInfoEntity)
            }
        }
        return info
    }


}