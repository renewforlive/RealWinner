package com.peter.realwinner.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.peter.realwinner.util.SharedPreferencesUtil
import org.koin.java.KoinJavaComponent.inject

open class BaseFragment: Fragment() {

    private val sharedPreferencesUtil by inject(SharedPreferencesUtil::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.apply {
            theme.applyStyle(sharedPreferencesUtil.getStyle(), true)
        }
    }
}