package com.peter.realwinner.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.peter.realwinner.util.LogUtil
import com.peter.realwinner.util.SharedPreferencesUtil
import org.koin.android.ext.android.inject

open class BaseActivity : AppCompatActivity() {

    private val sharedPreferencesUtil by inject<SharedPreferencesUtil>()

    override fun onCreate(savedInstanceState: Bundle?) {
        val style = sharedPreferencesUtil.getStyle()
        LogUtil.showInfoLog("style=$style")
        setTheme(style)
        super.onCreate(savedInstanceState)
    }


}