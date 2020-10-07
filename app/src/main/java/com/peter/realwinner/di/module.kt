package com.peter.realwinner.di

import androidx.room.Room
import com.bumptech.glide.Glide
import com.peter.realwinner.RealWinnerApplication
import com.peter.realwinner.model.db.RealWinnerDatabase
import com.peter.realwinner.model.repository.*
import com.peter.realwinner.ui.adapter.EventAdapter
import com.peter.realwinner.ui.adapter.HomeAdapter
import com.peter.realwinner.ui.adapter.RecordAdapter
import com.peter.realwinner.ui.adapter.SettingAdapter
import com.peter.realwinner.util.SharedPreferencesUtil
import com.peter.realwinner.util.ThemeColor
import com.peter.realwinner.viewmodel.*
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { SharedPreferencesUtil(androidApplication()) }
    factory { HomeRepository() }
    factory { HomeAdapter(Glide.with(RealWinnerApplication.getBaseContext())) }
    factory { RecordRepository() }
    factory { RecordAdapter(Glide.with(RealWinnerApplication.getBaseContext())) }
    factory { EventRepository() }
    factory { EventAdapter() }
    single { EventNotificationRepository() }
    factory { SettingAdapter() }
    factory { FriendDetailRepository() }
    single { ThemeColor(androidApplication()) }
    single { BillingRepository(androidApplication()) }
}

val dbModule = module(override = true) {
    single { provideDatabase() }
    single { get<RealWinnerDatabase>().friendInfoDao() }
    single { get<RealWinnerDatabase>().recordDao() }
    single { get<RealWinnerDatabase>().eventDao() }
    single { get<RealWinnerDatabase>().skuDetailsDao() }
    single { get<RealWinnerDatabase>().purchaseDao() }
    single { get<RealWinnerDatabase>().entitlementsDao() }
}

val viewModelModule = module(override = true) {
    viewModel { AddViewModel() }
    viewModel { HomeViewModel() }
    viewModel { RecordDetailViewModel(androidApplication()) }
    viewModel { RecordViewModel() }
    viewModel { EventViewModel() }
    viewModel { EventAddViewModel(androidApplication()) }
    viewModel { SettingViewModel(androidApplication()) }
    viewModel { FriendDetailViewModel() }
}

private fun provideDatabase() =
    Room.databaseBuilder(RealWinnerApplication.getBaseContext(),
    RealWinnerDatabase::class.java,
    "real_winner_db")
        .fallbackToDestructiveMigration()
        .build()