package com.peter.realwinner.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.peter.realwinner.R
import com.peter.realwinner.constant.BundleType
import com.peter.realwinner.model.db.entity.FriendInfoEntity
import com.peter.realwinner.viewmodel.FriendDetailViewModel
import kotlinx.android.synthetic.main.activity_friend_detail.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class FriendDetailActivity : BaseActivity() {

    private val viewModel by viewModel(FriendDetailViewModel::class)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_detail)
        initData()
        setUpObserver()
        initListener()
    }

    private fun initData() {
        viewModel.setFriendId(intent.getIntExtra(BundleType.FRIEND_ID, 0))
    }

    private fun setUpObserver() {
        viewModel.getTheme().observe(this, Observer {
            friend_detail_top_layout.setBackgroundResource(it)
        })
        viewModel.errorMsgLiveData.observe(this, Observer {
            showToast(it)
        })
        viewModel.getFriendData().observe(this, Observer {
            setUi(it)
        })
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun setUi(model: FriendInfoEntity) {
        val requestOptions = RequestOptions().circleCrop()
        Glide.with(this)
            .load(model.post)
            .apply(requestOptions)
            .into(friend_detail_head_img)
        friend_detail_name.text = String.format(getString(R.string.friend_detail_name), model.name)
        friend_detail_nickname.text = String.format(getString(R.string.friend_detail_nickname), model.nickName)
        friend_detail_birthday.text = String.format(getString(R.string.friend_detail_birthday), model.birth)
        friend_detail_height.text = String.format(getString(R.string.friend_detail_height), model.height)
        friend_detail_personality.text = String.format(getString(R.string.friend_detail_personality), model.personality)
        friend_detail_blood.text = String.format(getString(R.string.friend_detail_blood), model.blood)
        friend_detail_cellphone.text = String.format(getString(R.string.friend_detail_cellphone), model.cellphone)
        friend_detail_hobby.text = String.format(getString(R.string.friend_detail_hobby), model.hobby)
        friend_detail_line.text = String.format(getString(R.string.friend_detail_line), model.line)
        friend_detail_facebook.text = String.format(getString(R.string.friend_detail_facebook), model.facebook)
        friend_detail_instagram.text = String.format(getString(R.string.friend_detail_ig), model.instagram)
        friend_detail_other.text = String.format(getString(R.string.friend_detail_other), model.other)
    }

    private fun initListener() {
        friend_detail_back.setOnClickListener {
            finish()
        }
    }
}