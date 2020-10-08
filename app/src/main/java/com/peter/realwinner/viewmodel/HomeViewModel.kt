package com.peter.realwinner.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import com.peter.realwinner.model.db.dao.EntitlementsDao
import com.peter.realwinner.model.db.entity.AdvertisingEntity
import com.peter.realwinner.model.db.entity.Entitlements
import com.peter.realwinner.model.db.entity.FriendInfoEntity
import com.peter.realwinner.model.repository.HomeRepository
import com.peter.realwinner.util.SharedPreferencesUtil
import kotlinx.coroutines.*
import org.koin.java.KoinJavaComponent.inject

class HomeViewModel : ViewModel() {

    private val homeRepository by inject(HomeRepository::class.java)
    private val sharedPreferencesUtil by inject(SharedPreferencesUtil::class.java)
    private val entitlementsDao by inject(EntitlementsDao::class.java)
    val advertisingEntityLiveData: LiveData<AdvertisingEntity> by lazy {
        entitlementsDao.getAdvertisingEntity()
    }

    val showAdLiveData = MutableLiveData<Boolean>()

    fun getPagedList() : LiveData<PagedList<FriendInfoEntity>> {
        return homeRepository.getList()
    }

    fun loadAd() {
        showAdLiveData.value = true
    }

    fun dismissData(friendInfoEntity: FriendInfoEntity) {
        viewModelScope.launch {
            homeRepository.dismissData(friendInfoEntity)
        }
    }
}