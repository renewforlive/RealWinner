package com.peter.realwinner.model.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.SkuDetails
import com.peter.realwinner.model.db.entity.AugmentedSkuDetailsEntity

@Dao
interface AugmentedSkuDetailDao {

    @Query("SELECT * FROM AugmentedSkuDetailsEntity WHERE type = '${BillingClient.SkuType.SUBS}'")
    fun getSubscriptionSkuDetails(): LiveData<List<AugmentedSkuDetailsEntity>>

    @Query("SELECT * FROM AugmentedSkuDetailsEntity WHERE type = '${BillingClient.SkuType.INAPP}'")
    fun getInappSkuDetails(): LiveData<List<AugmentedSkuDetailsEntity>>

    @Transaction
    fun insertOrUpdate(skuDetails: SkuDetails) = skuDetails.apply {
        val result = getById(sku)
        val bool = result?.canPurchase ?: true
        val originalJson = toString().substring("SkuDetails: ".length)
        val detail = AugmentedSkuDetailsEntity(bool, sku, type, price, title, description, originalJson)
        insert(detail)
    }

    @Transaction
    fun insertOrUpdate(sku: String, canPurchase: Boolean) {
        val result = getById(sku)
        if (result != null) {
            update(sku, canPurchase)
        } else {
            insert(AugmentedSkuDetailsEntity(canPurchase, sku, null, null, null, null, null))
        }
    }

    @Query("SELECT * FROM AugmentedSkuDetailsEntity WHERE sku = :sku")
    fun getById(sku: String): AugmentedSkuDetailsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(augmentedSkuDetails: AugmentedSkuDetailsEntity)

    @Query("UPDATE AugmentedSkuDetailsEntity SET canPurchase = :canPurchase WHERE sku = :sku")
    fun update(sku: String, canPurchase: Boolean)
}