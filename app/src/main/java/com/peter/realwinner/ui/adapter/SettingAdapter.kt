package com.peter.realwinner.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.peter.realwinner.R
import com.peter.realwinner.model.data.SettingInfoModel
import kotlinx.android.synthetic.main.item_setting.view.*

class SettingAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val settingList = ArrayList<SettingInfoModel>()
    private var settingItemCallback: SettingItemCallback? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SettingItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_setting, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SettingItemViewHolder -> {
                holder.bind(settingList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return settingList.size
    }

    fun setList(list: List<SettingInfoModel>) {
        settingList.clear()
        settingList.addAll(list)
        notifyDataSetChanged()
    }

    fun setListener(listener: SettingItemCallback) {
        settingItemCallback = listener
    }

    inner class SettingItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(model: SettingInfoModel) {
            itemView.item_setting_img.setImageResource(model.resourceId)
            itemView.item_setting_title.text = model.title
            itemView.setOnClickListener {
                settingItemCallback?.onClickItem(model)
            }
        }
    }

    interface SettingItemCallback {
        fun onClickItem(model: SettingInfoModel)
    }
}