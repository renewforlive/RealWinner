package com.peter.realwinner.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.peter.realwinner.R
import com.peter.realwinner.constant.BundleType
import com.peter.realwinner.model.db.entity.FriendInfoEntity
import com.peter.realwinner.ui.activity.FriendDetailActivity
import com.peter.realwinner.ui.adapter.HomeAdapter
import com.peter.realwinner.ui.callback.FriendListener
import com.peter.realwinner.util.LogUtil
import com.peter.realwinner.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.java.KoinJavaComponent.inject

class HomeFragment : BaseFragment() {

    companion object {
        fun newInstance() : HomeFragment {
            return HomeFragment()
        }
    }

    private val viewModel by viewModel<HomeViewModel>()
    private val adapter by inject(HomeAdapter::class.java)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpObserver()
        initListener()
    }

    private fun initView() {
        val layoutManager = LinearLayoutManager(this.activity)
        home_recycler_view.layoutManager = layoutManager
        home_recycler_view.adapter = adapter
    }

    private fun setUpObserver() {
        viewModel.getPagedList().observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
        viewModel.showAdLiveData.observe(viewLifecycleOwner, Observer {
            val adRequest = AdRequest.Builder().build()
            home_banner_ad.loadAd(adRequest)
        })
        viewModel.advertisingEntityLiveData.observe(viewLifecycleOwner, Observer {
            if (it != null && it.entitled) {
                home_banner_ad.visibility = View.GONE
            } else {
                loadAd()
            }
        })
    }

    private fun initListener() {
        adapter.setListener(object : FriendListener {
            override fun onDismiss(friendInfoEntity: FriendInfoEntity) {
                viewModel.dismissData(friendInfoEntity)
            }

            override fun onClickItem(friendInfoEntity: FriendInfoEntity) {
                goToPage(friendInfoEntity)
            }
        })
    }

    private fun loadAd() {
        viewModel.loadAd()
    }

    private fun goToPage(friendInfoEntity: FriendInfoEntity) {
        val intent = Intent(context, FriendDetailActivity::class.java)
        intent.putExtra(BundleType.FRIEND_ID, friendInfoEntity.friendId)
        startActivity(intent)
    }

}