package com.peter.realwinner.model.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

abstract class Entitlements {
    @PrimaryKey
    var id: Int = 1

    abstract fun mayPurchase(): Boolean
}

@Entity(tableName = "advertising")
data class AdvertisingEntity(val entitled: Boolean) : Entitlements() {
    override fun mayPurchase(): Boolean {
        return !entitled
    }
}