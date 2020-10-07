package com.peter.realwinner.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.peter.realwinner.R
import com.peter.realwinner.RealWinnerApplication
import com.peter.realwinner.constant.ThemeType
import com.peter.realwinner.model.db.entity.FriendInfoEntity
import com.peter.realwinner.model.repository.FriendDetailRepository
import com.peter.realwinner.util.SharedPreferencesUtil
import org.koin.java.KoinJavaComponent.inject

class FriendDetailViewModel : ViewModel() {

    private var friendId = 0

    private val sharedPreferencesUtil by inject(SharedPreferencesUtil::class.java)
    private val friendDetailRepository by inject(FriendDetailRepository::class.java)

    val errorMsgLiveData = MutableLiveData<String>()

    fun setFriendId(friendId: Int) {
        this.friendId = friendId
        if (friendId == 0) {
            errorMsgLiveData.value = RealWinnerApplication.getBaseContext().getString(R.string.friend_detail_id_error)
        }
    }

    fun getFriendData() : LiveData<FriendInfoEntity> {
        return friendDetailRepository.getFriendInfo(friendId)
    }

    fun getTheme() : LiveData<Int> {
        return if (sharedPreferencesUtil.getStyle() == ThemeType.DEFAULT_THEME) {
            MutableLiveData(R.color.colorPrimary)
        } else {
            MutableLiveData(R.color.light_brown)
        }
    }
}