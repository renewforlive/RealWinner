package com.peter.realwinner.ui.activity

import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.ads.MobileAds
import com.peter.realwinner.R
import com.peter.realwinner.ui.adapter.MainPagerAdapter
import com.peter.realwinner.ui.callback.RecreateListener
import com.peter.realwinner.ui.dialog.AddDialog
import com.peter.realwinner.ui.fragment.*
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

class MainActivity : BaseActivity(), RecreateListener {

    private val menu by lazy {
        bottom_navigation_view.menu
    }

    private val fragmentList by lazy {
        listOf(
            HomeFragment.newInstance(),
            RecordFragment.newInstance(),
            EventFragment.newInstance(),
            SettingFragment.newInstance()
        )
    }

    private val adapter by lazy {
        MainPagerAdapter(this, fragmentList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initListener()
        initAdmob()
    }

    private fun initView() {
        bottom_navigation_viewpager.adapter = adapter
    }

    private fun initListener() {
        navigation_add_button.setOnClickListener {
            showAddDialog()
        }
        bottom_navigation_view.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    bottom_navigation_viewpager.currentItem = 0
                    true
                }
                R.id.navigation_record -> {
                    bottom_navigation_viewpager.currentItem = 1
                    true
                }
                R.id.navigation_event -> {
                    bottom_navigation_viewpager.currentItem = 2
                    true
                }
                R.id.navigation_setting -> {
                    bottom_navigation_viewpager.currentItem = 3
                    true
                }
                else -> false
            }
        }
        bottom_navigation_viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> menu.getItem(0).isChecked = true
                    1 -> menu.getItem(1).isChecked = true
                    2 -> menu.getItem(3).isChecked = true
                    3 -> menu.getItem(4).isChecked = true
                }
            }
        })
    }

    private fun initAdmob() {
        MobileAds.initialize(this) {}
    }

    private fun showAddDialog() {
        val dialog = AddDialog.newInstance()
        dialog.show(supportFragmentManager, AddDialog.TAG)
    }

    override fun onRecreate() {
        try {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            for (fragment in fragmentList) {
                fragmentTransaction.remove(fragment)
            }
            fragmentTransaction.commitAllowingStateLoss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        recreate()
    }
}
