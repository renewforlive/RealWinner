package com.peter.realwinner.model.repository

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.peter.realwinner.model.db.dao.RecordDao
import com.peter.realwinner.model.db.entity.RecordEntity
import org.koin.java.KoinJavaComponent.inject

class RecordRepository {

    companion object {
        private const val PAGE_SIZE = 30
        private const val DISTANCE = 5
    }

    private val recordDao by inject(RecordDao::class.java)

    fun getList() : LiveData<PagedList<RecordEntity>> {
        val dataSourceFactory = recordDao.getRecord()
        return LivePagedListBuilder(dataSourceFactory, getConfig()).build()
    }

    private fun getConfig() : PagedList.Config {
        return PagedList.Config.Builder()
            .setPageSize(PAGE_SIZE)
            .setInitialLoadSizeHint(PAGE_SIZE * 2)
            .setPrefetchDistance(DISTANCE)
            .build()
    }
}