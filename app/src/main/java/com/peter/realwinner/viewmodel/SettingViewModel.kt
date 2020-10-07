package com.peter.realwinner.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.peter.realwinner.R
import com.peter.realwinner.constant.ProductSku
import com.peter.realwinner.constant.ThemeType
import com.peter.realwinner.model.data.SettingInfoModel
import com.peter.realwinner.model.db.entity.AdvertisingEntity
import com.peter.realwinner.model.db.entity.AugmentedSkuDetailsEntity
import com.peter.realwinner.model.repository.BillingRepository
import com.peter.realwinner.util.SharedPreferencesUtil
import org.koin.java.KoinJavaComponent.inject

class SettingViewModel(application: Application) : AndroidViewModel(application) {

    private val context: Context = application
    private val sharedPreferencesUtil by inject(SharedPreferencesUtil::class.java)
    private val billingRepository by inject(BillingRepository::class.java)
    private var skuList = emptyList<AugmentedSkuDetailsEntity>()

    val versionNameLiveData = MutableLiveData<String>()
    val changeThemeLiveData = MutableLiveData<Int>()
    val cancelAdvertisingLiveData = MutableLiveData<Boolean>()
    val cancelNotificationLiveData = MutableLiveData<Boolean>()

    fun loadData() : LiveData<List<SettingInfoModel>> {
        val loadDataLiveData = MutableLiveData<List<SettingInfoModel>>()
        val settingInfoList = ArrayList<SettingInfoModel>()
        for (i in 0 until 4) {
            val model = setSettingInfo(i)
            settingInfoList.add(model)
        }
        return loadDataLiveData.also { it.value = settingInfoList }
    }

    private fun setSettingInfo(position: Int) : SettingInfoModel {
        return when(position) {
            0 -> {
                SettingInfoModel(order = position, title = context.getString(R.string.setting_version_title), resourceId = R.drawable.ic_version)
            }
            1 -> {
                SettingInfoModel(order = position, title = context.getString(R.string.setting_theme_title), resourceId = R.drawable.ic_theme)
            }
            2 -> {
                SettingInfoModel(order = position, title = context.getString(R.string.setting_adv_title), resourceId = R.drawable.ic_advertising)
            }
            3 -> {
                SettingInfoModel(order = position, title = context.getString(R.string.setting_notification_title), resourceId = R.drawable.ic_notification)
            }
            else -> {
                SettingInfoModel(order = position, title = String(), resourceId = 0)
            }
        }
    }

    fun selectItem(model: SettingInfoModel) {
        when(model.order) {
            0 -> {
                val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                val versionName = packageInfo.versionName
                versionNameLiveData.value = versionName
            }
            1 -> {
                var style = sharedPreferencesUtil.getStyle()
                style = if (ThemeType.PUDDING_THEME == style) {
                    sharedPreferencesUtil.setStyle(ThemeType.DEFAULT_THEME)
                    ThemeType.DEFAULT_THEME
                } else {
                    sharedPreferencesUtil.setStyle(ThemeType.PUDDING_THEME)
                    ThemeType.PUDDING_THEME
                }
                changeThemeLiveData.value = style
            }
            2 -> {
                cancelAdvertisingLiveData.value = true
            }
            3 -> {
                cancelNotificationLiveData.value = true
            }
            else -> {

            }
        }
    }

    fun startBillingConnect() {
        billingRepository.startDataSourceConnection()
    }

    fun getInAppSkuDetails() : LiveData<List<AugmentedSkuDetailsEntity>> {
        return billingRepository.inAppSkuDetailsListLiveData
    }

    fun setSkuList(list: List<AugmentedSkuDetailsEntity>) {
        skuList = list
    }

    fun getAdvertisingEntity() : LiveData<AdvertisingEntity> {
        return billingRepository.advertisingEntityLiveData
    }

    fun launchPurchaseFlow(activity: Activity) {
        skuList.forEach {
            if (it.sku == ProductSku.CANCEL_AD) {
                if (it.canPurchase) {
                    billingRepository.launchBillingFlow(activity, it)
                } else {
                    Toast.makeText(activity, activity.getString(R.string.setting_adv_purchase_already), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        billingRepository.endDataSourceConnection()
    }
}