package com.peter.realwinner.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.peter.realwinner.model.db.entity.RecordEntity
import com.peter.realwinner.model.repository.RecordRepository
import org.koin.java.KoinJavaComponent.inject

class RecordViewModel : ViewModel() {

    private val recordRepository by inject(RecordRepository::class.java)

    fun getPagedList() : LiveData<PagedList<RecordEntity>> {
        return recordRepository.getList()
    }
}