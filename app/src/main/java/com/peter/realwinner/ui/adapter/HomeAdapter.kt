package com.peter.realwinner.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.peter.realwinner.R
import com.peter.realwinner.constant.ThemeType
import com.peter.realwinner.model.db.entity.FriendInfoEntity
import com.peter.realwinner.ui.callback.FriendListener
import com.peter.realwinner.ui.callback.ItemTouchHelperListener
import com.peter.realwinner.util.SharedPreferencesUtil
import kotlinx.android.synthetic.main.item_home.view.*
import org.koin.java.KoinJavaComponent.inject

class HomeAdapter(private val glide: RequestManager) : PagedListAdapter<FriendInfoEntity, HomeAdapter.ItemViewHolder>(diffUtil), ItemTouchHelperListener {

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<FriendInfoEntity>() {
            override fun areItemsTheSame(
                oldItem: FriendInfoEntity,
                newItem: FriendInfoEntity
            ): Boolean {
                return oldItem.friendId == newItem.friendId
            }

            override fun areContentsTheSame(
                oldItem: FriendInfoEntity,
                newItem: FriendInfoEntity
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    private val sharedPreferencesUtil by inject(SharedPreferencesUtil::class.java)

    private var listener: FriendListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_home, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    fun setListener(friendListener: FriendListener) {
        listener = friendListener
    }

    override fun onViewRecycled(holder: ItemViewHolder) {
        super.onViewRecycled(holder)
        glide.clear(holder.itemView)
        holder.itemView.home_item_head_img.setImageDrawable(null)
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(model: FriendInfoEntity) {
            val requestOptions = RequestOptions.circleCropTransform()
            glide.asBitmap()
                .load(model.post)
                .apply(requestOptions)
                .into(itemView.home_item_head_img)
            itemView.home_item_full_name.text = model.name
            model.nickName?.let {
                itemView.home_item_nick_name.text = it
            }
            itemView.setOnClickListener {
                listener?.onClickItem(model)
            }
            if (sharedPreferencesUtil.getStyle() == ThemeType.DEFAULT_THEME) {
                itemView.setBackgroundResource(R.drawable.shape_blue_corner_stroke)
            } else {
                itemView.setBackgroundResource(R.drawable.shape_pudding_corner_stroke)
            }
        }
    }

    override fun onItemDismiss(position: Int) {
        getItem(position)?.let {
            listener?.onDismiss(it)
        }
    }
}