package com.peter.realwinner.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.peter.realwinner.R
import com.peter.realwinner.model.db.dao.RecordDao
import com.peter.realwinner.model.db.entity.RecordEntity
import com.peter.realwinner.util.SharedPreferencesUtil
import kotlinx.coroutines.*
import org.koin.java.KoinJavaComponent.inject

class RecordDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val context: Context = application
    private val sharedPreferencesUtil by inject(SharedPreferencesUtil::class.java)

    private val recordDao by inject(RecordDao::class.java)
    private var recordId = 0

    val recordLiveData = MutableLiveData<RecordEntity>()
    val errorMsg = MutableLiveData<String>()
    val saveDataLiveData = MutableLiveData<Boolean>()
    val deleteDataLiveData = MutableLiveData<Boolean>()

    private var path: String = String()
    private var time: Long = 0L
    private var postType: String = String()
    private var recordEntity: RecordEntity? = null

    fun setRecordId(recordId: Int) {
        this.recordId = recordId
    }

    fun isNewData() : Boolean {
        return recordId == -1
    }

    fun getTheme() : LiveData<Int> {
        return MutableLiveData(sharedPreferencesUtil.getStyle())
    }

    fun getRecordEntity() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                recordDao.getRecordById(recordId)
            }
            result?.let {
                path = it.post
                postType = it.postType
                time = it.time
                recordLiveData.value = result
            }
        }
    }

    fun setPost(path: String) {
        this.path = path
    }

    fun getPost() : String {
        return path
    }

    fun setPostType(type: String) {
        postType = type
    }

    fun getPostType() : String {
        return postType
    }

    fun setTime(timestamp: Long) {
        this.time = timestamp
    }

    fun getTime() : Long {
        return time
    }

    fun setRecordEntity(recordEntity: RecordEntity) {
        this.recordEntity = recordEntity
    }

    fun validateRecord(): Boolean {
        recordEntity?.let {
            when {
                it.post.isBlank() -> {
                    errorMsg.value = context.getString(R.string.record_detail_validate_post)
                    return false
                }
                it.title.isBlank() -> {
                    errorMsg.value = context.getString(R.string.record_detail_validate_title)
                    return false
                }
                it.time == 0L -> {
                    errorMsg.value = context.getString(R.string.record_detail_validate_time)
                    return false
                }
                it.content.isBlank() -> {
                    errorMsg.value = context.getString(R.string.record_detail_validate_content)
                    return false
                }
                else -> {
                    return true
                }
            }
        }
        return false
    }

    fun saveData() {
        viewModelScope.launch {
            recordEntity?.let {
                withContext(Dispatchers.IO) {
                    recordDao.updateIfExist(it)
                }
                saveDataLiveData.value = true
            }
        }
    }

    fun deleteData() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                recordDao.deleteById(recordId)
            }
            deleteDataLiveData.value = true
        }
    }
}