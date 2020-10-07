package com.peter.realwinner.model.repository

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.peter.realwinner.model.db.dao.FriendInfoDao
import com.peter.realwinner.model.db.entity.FriendInfoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject

class HomeRepository {

    companion object {
        private const val PAGE_SIZE = 30
        private const val DISTANCE = 5
    }

    private val friendInfoDao by inject(FriendInfoDao::class.java)

    fun getList() : LiveData<PagedList<FriendInfoEntity>> {
        val dataSourceFactory = friendInfoDao.getFriendInfoList()
        return LivePagedListBuilder(dataSourceFactory, getConfig()).build()
    }

    private fun getConfig() : PagedList.Config {
        return PagedList.Config.Builder()
            .setPageSize(PAGE_SIZE)
            .setInitialLoadSizeHint(PAGE_SIZE * 2)
            .setPrefetchDistance(DISTANCE)
            .build()
    }

    suspend fun dismissData(friendInfoEntity: FriendInfoEntity) {
        withContext(Dispatchers.IO) {
            friendInfoDao.delete(friendInfoEntity)
        }
    }
}