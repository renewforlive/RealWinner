package com.peter.realwinner.model.db.entity

import androidx.room.*
import com.android.billingclient.api.Purchase

@Entity(tableName = "purchase_table")
@TypeConverters(PurchaseTypeConverter::class)
class CachedPurchaseEntity(val data: Purchase) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @Ignore
    val purchaseToken = data.purchaseToken
    @Ignore
    val sku = data.sku

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is CachedPurchaseEntity -> data == other.data
            is Purchase -> data == other
            else -> false
        }
    }

    override fun hashCode(): Int {
        return data.hashCode()
    }
}

class PurchaseTypeConverter {
    @TypeConverter
    fun toString(purchase: Purchase): String = purchase.originalJson + '|' + purchase.signature

    @TypeConverter
    fun toPurchase(data: String): Purchase = data.split('|').let {
        Purchase(it[0], it[1])
    }
}
