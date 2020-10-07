package com.peter.realwinner.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.peter.realwinner.R
import com.peter.realwinner.constant.ThemeType
import com.peter.realwinner.model.db.entity.EventEntity
import com.peter.realwinner.ui.callback.EventNotificationListener
import com.peter.realwinner.util.SharedPreferencesUtil
import kotlinx.android.synthetic.main.item_event.view.*
import org.koin.java.KoinJavaComponent.inject

class EventAdapter: PagedListAdapter<EventEntity, RecyclerView.ViewHolder>(diffUtil) {

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<EventEntity>() {
            override fun areItemsTheSame(oldItem: EventEntity, newItem: EventEntity): Boolean {
                return oldItem.eventId == newItem.eventId
            }

            override fun areContentsTheSame(oldItem: EventEntity, newItem: EventEntity): Boolean {
                return oldItem == newItem
            }
        }
    }
    private val sharedPreferencesUtil by inject(SharedPreferencesUtil::class.java)

    private var eventNotificationListener: EventNotificationListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return EventItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is EventItemViewHolder -> {
                getItem(position)?.let {
                    holder.bind(it)
                }
            }
        }
    }

    fun setListener(listener: EventNotificationListener) {
        eventNotificationListener = listener
    }

    inner class EventItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(eventEntity: EventEntity) {
            itemView.event_title_detail.text = eventEntity.title
            itemView.event_content_detail.text = eventEntity.content
            itemView.event_notification_switch.isChecked = eventEntity.isNotification == 1

            if (sharedPreferencesUtil.getStyle() == ThemeType.DEFAULT_THEME) {
                itemView.setBackgroundResource(R.drawable.shape_blue_corner_stroke)
                itemView.event_notification_switch.trackTintList = ContextCompat.getColorStateList(itemView.context, R.color.light_blue)
            } else {
                itemView.setBackgroundResource(R.drawable.shape_pudding_corner_stroke)
                itemView.event_notification_switch.trackTintList = ContextCompat.getColorStateList(itemView.context, R.color.light_brown)
            }

            itemView.event_notification_switch.setOnCheckedChangeListener { _, isChecked ->
                eventNotificationListener?.onNotification(isChecked, eventEntity)
            }
        }
    }
}