package com.peter.realwinner.model.repository

import android.app.Activity
import android.app.Application
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.android.billingclient.api.*
import com.peter.realwinner.constant.ProductSku
import com.peter.realwinner.model.db.*
import com.peter.realwinner.model.db.entity.AdvertisingEntity
import com.peter.realwinner.model.db.entity.AugmentedSkuDetailsEntity
import com.peter.realwinner.model.db.entity.Entitlements
import com.peter.realwinner.util.LogUtil
import kotlinx.coroutines.*
import org.koin.java.KoinJavaComponent.inject

class BillingRepository(private val application: Application): PurchasesUpdatedListener, BillingClientStateListener {

    companion object {
        val inAppSkuList = listOf(ProductSku.CANCEL_AD, ProductSku.TEST)
        val subsSkuList = listOf<String>(ProductSku.CANCEL_AD, ProductSku.TEST)
        val consumableSkuList = emptyList<String>()
    }

    private lateinit var playStoreBillingClient: BillingClient

    private val localCacheBillingClient by inject(RealWinnerDatabase::class.java)

    val subsSkuDetailsListLiveData: LiveData<List<AugmentedSkuDetailsEntity>> by lazy {
        localCacheBillingClient.skuDetailsDao().getSubscriptionSkuDetails()
    }

    val inAppSkuDetailsListLiveData: LiveData<List<AugmentedSkuDetailsEntity>> by lazy {
        localCacheBillingClient.skuDetailsDao().getInappSkuDetails()
    }

    val advertisingEntityLiveData: LiveData<AdvertisingEntity> by lazy {
        localCacheBillingClient.entitlementsDao().getAdvertisingEntity()
    }

    private fun instantiateAndConnectToPlayBillingService() {
        playStoreBillingClient = BillingClient.newBuilder(application.applicationContext)
            .enablePendingPurchases()
            .setListener(this).build()
        connectToPlayBillingService()
    }

    private fun connectToPlayBillingService() : Boolean {
        if (!playStoreBillingClient.isReady) {
            playStoreBillingClient.startConnection(this)
            return true
        }
        return false
    }

    override fun onPurchasesUpdated(p0: BillingResult, p1: MutableList<Purchase>?) {
        when (p0.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                p1?.apply { processPurchases(this.toSet()) }
            }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                queryPurchaseAsync()
            }
            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> {
                connectToPlayBillingService()
            }
            else -> {
                LogUtil.showDebugLog(p0.debugMessage)
            }
        }
    }

    override fun onBillingSetupFinished(p0: BillingResult) {
        when (p0.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                querySkuDetailAsync(BillingClient.SkuType.INAPP, inAppSkuList)
                querySkuDetailAsync(BillingClient.SkuType.SUBS, subsSkuList)
                queryPurchaseAsync()
            }
            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> {
                LogUtil.showDebugLog(p0.debugMessage)
            }
            else -> {
                LogUtil.showDebugLog(p0.debugMessage)
            }
        }
    }

    override fun onBillingServiceDisconnected() {
        LogUtil.showDebugLog("onBillingServiceDisconnected")
        connectToPlayBillingService()
    }

    private fun isSubscriptionSupported() : Boolean {
        val billingResult = playStoreBillingClient.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS)
        var succeeded = false
        when(billingResult.responseCode) {
            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> connectToPlayBillingService()
            BillingClient.BillingResponseCode.OK -> succeeded = true
            else -> LogUtil.showDebugLog(billingResult.debugMessage)
        }
        return succeeded
    }

    private fun querySkuDetailAsync(@BillingClient.SkuType skuType: String, skuList: List<String>) {
        LogUtil.showInfoLog("skuType=$skuType, skuList=$skuList")
        val params = SkuDetailsParams.newBuilder().setSkusList(skuList).setType(skuType).build()
        playStoreBillingClient.querySkuDetailsAsync(params) { billingResult, skuDetailList ->
            LogUtil.showInfoLog("billingResult=${billingResult.responseCode}, skuDetailList=$skuDetailList")
            when (billingResult.responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    if (skuDetailList.orEmpty().isNotEmpty()) {
                        skuDetailList?.forEach {
                            CoroutineScope(Job() + Dispatchers.IO).launch {
                                localCacheBillingClient.skuDetailsDao().insertOrUpdate(it)
                            }
                        }
                    } else {
                        LogUtil.showDebugLog(billingResult.debugMessage)
                    }
                }
            }
        }
    }

    private fun queryPurchaseAsync() {
        val purchaseResult = HashSet<Purchase>()
        var result = playStoreBillingClient.queryPurchases(BillingClient.SkuType.INAPP)
        result.purchasesList?.apply {
            purchaseResult.addAll(this)
        }
        if (isSubscriptionSupported()) {
            result = playStoreBillingClient.queryPurchases(BillingClient.SkuType.SUBS)
            result.purchasesList?.apply {
                purchaseResult.addAll(this)
            }
        }
        processPurchases(purchaseResult)
    }

    private fun processPurchases(purchasesResult: Set<Purchase>) {
        CoroutineScope(Job() + Dispatchers.IO).launch {
            val validPurchases = HashSet<Purchase>(purchasesResult.size)
            purchasesResult.forEach { purchase ->
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    if (isSignatureValid(purchase)) {
                        validPurchases.add(purchase)
                    }
                } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
                    LogUtil.showDebugLog("Received a pending of Sku: ${purchase.sku}")
                }
            }
            val (consumables, nonConsumbles) = validPurchases.partition {
                consumableSkuList.contains(it.sku)
            }
            LogUtil.showDebugLog("processPurchases consumbles content $consumables")
            LogUtil.showDebugLog("processPurchases non-consumbles content $nonConsumbles")

            val testing = localCacheBillingClient.purchaseDao().getPurchases()
            LogUtil.showDebugLog("processPurchases purchases in the lcl db ${testing.size}")
            localCacheBillingClient.purchaseDao().insert(*validPurchases.toTypedArray())
            handleConsumablePurchaseAsync(consumables)
            acknowledgeNonConsumablePurchasesAsync(nonConsumbles)
        }
    }

    private fun isSignatureValid(purchase: Purchase): Boolean {
        return true
    }

    private fun handleConsumablePurchaseAsync(consumables: List<Purchase>) {
        consumables.forEach {
            val params = ConsumeParams.newBuilder().setPurchaseToken(it.purchaseToken).build()
            playStoreBillingClient.consumeAsync(params) { billingResult, purchaseToken ->
                when (billingResult.responseCode) {
                    BillingClient.BillingResponseCode.OK -> {
                        purchaseToken.apply { disburseConsumableEntitlements(it) }
                    }
                    else -> {
                        LogUtil.showDebugLog(billingResult.debugMessage)
                    }
                }
            }
        }
    }

    private fun acknowledgeNonConsumablePurchasesAsync(nonConsumable: List<Purchase>) {
        nonConsumable.forEach { purchase ->
            val params = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
            playStoreBillingClient.acknowledgePurchase(params) { billingResult ->
                when (billingResult.responseCode) {
                    BillingClient.BillingResponseCode.OK -> {
                        disburseNonConsumableEntitlement(purchase)
                    }
                    else -> {
                        LogUtil.showDebugLog("acknowledgeNonConsumablePurchasesAsync response is ${billingResult.debugMessage}")
                    }
                }
            }
        }
    }

    private fun disburseConsumableEntitlements(purchase: Purchase) =
        CoroutineScope(Job() + Dispatchers.IO).launch {
            localCacheBillingClient.purchaseDao().delete(purchase)
        }

    private fun disburseNonConsumableEntitlement(purchase: Purchase) =
        CoroutineScope(Job() + Dispatchers.IO).launch {
            when (purchase.sku) {
                ProductSku.CANCEL_AD -> {
                    val advertisingEntity = AdvertisingEntity(true)
                    insert(advertisingEntity)
                    localCacheBillingClient.skuDetailsDao().insertOrUpdate(purchase.sku, advertisingEntity.mayPurchase())
                }
            }
            localCacheBillingClient.purchaseDao().delete(purchase)
        }

    @WorkerThread
    private suspend fun insert(entitlement: Entitlements) = withContext(Dispatchers.IO) {
        localCacheBillingClient.entitlementsDao().insert(entitlement)
    }

    @WorkerThread
    suspend fun update(advertisingEntity: AdvertisingEntity) = withContext(Dispatchers.IO) {
        var update: AdvertisingEntity = advertisingEntity
        advertisingEntityLiveData.value?.apply {
            if (this != advertisingEntity) {
                update = AdvertisingEntity(true)
            }
            localCacheBillingClient.entitlementsDao().update(update)
        }
    }

    private fun launchBillingFlow(activity: Activity, skuDetails: SkuDetails) {
        val purchaseParams = BillingFlowParams.newBuilder().setSkuDetails(skuDetails).build()
        playStoreBillingClient.launchBillingFlow(activity, purchaseParams)
    }

    //open
    fun startDataSourceConnection() {
        instantiateAndConnectToPlayBillingService()
    }

    fun endDataSourceConnection() {
        playStoreBillingClient.endConnection()
    }

    fun launchBillingFlow(activity: Activity, augmentedSkuDetailsEntity: AugmentedSkuDetailsEntity) {
        augmentedSkuDetailsEntity.originalJson?.let {
            launchBillingFlow(activity, SkuDetails(it))
        }
    }
}