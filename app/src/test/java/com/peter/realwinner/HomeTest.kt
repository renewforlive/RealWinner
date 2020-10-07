package com.peter.realwinner

import com.peter.realwinner.di.appModule
import com.peter.realwinner.di.dbModule
import com.peter.realwinner.di.viewModelModule
import com.peter.realwinner.model.db.dao.FriendInfoDao
import com.peter.realwinner.model.db.entity.FriendInfoEntity
import com.peter.realwinner.viewmodel.HomeViewModel
import io.mockk.MockKAnnotations
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject

class HomeTest : KoinTest {

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(appModule, dbModule, viewModelModule)
    }

    private val homeViewModel by inject<HomeViewModel>()
    private val dao by inject<FriendInfoDao>()

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        val mainThreadSurrogate = newSingleThreadContext("UI thread")
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @Test
    fun home_delete_friendInfoEntity() {
        val friendInfoEntity = mockk<FriendInfoEntity>()
        verify { dao.delete(friendInfoEntity) }
    }
}