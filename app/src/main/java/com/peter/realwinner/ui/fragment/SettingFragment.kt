package com.peter.realwinner.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.realwinner.R
import com.peter.realwinner.model.data.SettingInfoModel
import com.peter.realwinner.ui.adapter.SettingAdapter
import com.peter.realwinner.ui.callback.RecreateListener
import com.peter.realwinner.viewmodel.SettingViewModel
import kotlinx.android.synthetic.main.fragment_setting.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.IOException

class SettingFragment : BaseFragment() {

    companion object {
        fun newInstance() : SettingFragment {
            return SettingFragment()
        }
    }

    private val viewModel by viewModel(SettingViewModel::class)
    private val adapter by inject<SettingAdapter>()

    private var recreateListener: RecreateListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            val callback = activity as RecreateListener
            recreateListener = callback
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpObserver()
        initListener()
        viewModel.startBillingConnect()
    }

    private fun initView() {
        with(activity) {
            val layoutManager = LinearLayoutManager(this)
            setting_recycler_view.layoutManager = layoutManager
            setting_recycler_view.adapter = adapter
        }
    }

    private fun setUpObserver() {
        viewModel.loadData().observe(viewLifecycleOwner, Observer {
            adapter.setList(it)
        })
        viewModel.versionNameLiveData.observe(viewLifecycleOwner, Observer {
            showToast(String.format(getString(R.string.setting_version_name_display), it))
        })
        viewModel.changeThemeLiveData.observe(viewLifecycleOwner, Observer {
            showToast(getString(R.string.setting_theme_change))
            recreateListener?.onRecreate()
        })
        viewModel.getInAppSkuDetails().observe(viewLifecycleOwner, Observer { skuList ->
            viewModel.setSkuList(skuList)
        })
        viewModel.cancelAdvertisingLiveData.observe(viewLifecycleOwner, Observer {
            activity?.apply {
                viewModel.launchPurchaseFlow(this)
            }
        })
        viewModel.getAdvertisingEntity().observe(viewLifecycleOwner, Observer {
            if (it != null && it.entitled) {
                Toast.makeText(this.activity, getString(R.string.setting_adv_purchase_success), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun initListener() {
        adapter.setListener(object : SettingAdapter.SettingItemCallback {
            override fun onClickItem(model: SettingInfoModel) {
                viewModel.selectItem(model)
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(this.activity, message, Toast.LENGTH_SHORT).show()
    }
}