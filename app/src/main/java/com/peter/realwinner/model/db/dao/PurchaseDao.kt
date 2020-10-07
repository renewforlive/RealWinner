package com.peter.realwinner.model.db.dao

import androidx.room.*
import com.android.billingclient.api.Purchase
import com.peter.realwinner.model.db.entity.CachedPurchaseEntity

@Dao
interface PurchaseDao {
    @Query("SELECT * FROM purchase_table")
    fun getPurchases(): List<CachedPurchaseEntity>

    @Insert
    fun insert(purchase: CachedPurchaseEntity)

    @Transaction
    fun insert(vararg purchases: Purchase) {
        purchases.forEach {
            insert(CachedPurchaseEntity(data = it))
        }
    }

    @Delete
    fun delete(vararg purchases: CachedPurchaseEntity)

    @Query("DELETE FROM purchase_table")
    fun deleteAll()

    @Query("DELETE FROM purchase_table WHERE data = :purchase")
    fun delete(purchase: Purchase)
}