package com.peter.realwinner.model.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.peter.realwinner.model.db.dao.*
import com.peter.realwinner.model.db.entity.*

@Database(entities = [FriendInfoEntity::class,
    RecordEntity::class,
    EventEntity::class,
    AugmentedSkuDetailsEntity::class,
    CachedPurchaseEntity::class,
    AdvertisingEntity::class], version = 7)
@TypeConverters(PurchaseTypeConverter::class)
abstract class RealWinnerDatabase : RoomDatabase() {
    abstract fun friendInfoDao(): FriendInfoDao
    abstract fun recordDao(): RecordDao
    abstract fun eventDao(): EventDao
    abstract fun skuDetailsDao(): AugmentedSkuDetailDao
    abstract fun purchaseDao(): PurchaseDao
    abstract fun entitlementsDao(): EntitlementsDao
}