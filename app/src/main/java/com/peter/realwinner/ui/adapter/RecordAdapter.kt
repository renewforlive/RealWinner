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
import com.peter.realwinner.model.db.entity.RecordEntity
import com.peter.realwinner.util.TimeUtil
import kotlinx.android.synthetic.main.item_record.view.*

class RecordAdapter(private val glide: RequestManager) : PagedListAdapter<RecordEntity, RecyclerView.ViewHolder>(diffUtil) {

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<RecordEntity>() {
            override fun areItemsTheSame(oldItem: RecordEntity, newItem: RecordEntity): Boolean {
                return oldItem.recordId == newItem.recordId
            }

            override fun areContentsTheSame(oldItem: RecordEntity, newItem: RecordEntity): Boolean {
                return oldItem == newItem
            }
        }
    }

    private var recordItemCallback: RecordItemCallback? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return RecordItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_record, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RecordItemViewHolder -> {
                getItem(position)?.let {
                    holder.bind(it)
                }
            }
        }
    }

    fun setListener(listener: RecordItemCallback) {
        recordItemCallback = listener
    }

    inner class RecordItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(recordEntity: RecordEntity) {
            val requestOption = RequestOptions.centerCropTransform().error(R.drawable.ic_no_image)
            glide.asBitmap().load(recordEntity.post).apply(requestOption).into(itemView.item_record_post)

            itemView.item_record_title_content.text = recordEntity.title
            val timeModel = TimeUtil.getTimeModelByTimestamp(recordEntity.time)
            itemView.item_record_time_content.text = String.format(itemView.context.getString(R.string.item_record_time_content), timeModel.year, timeModel.month, timeModel.day)
            itemView.item_record_content_info.text = recordEntity.content
            itemView.setOnClickListener {
                recordItemCallback?.onClickRecord(recordEntity)
            }
        }
    }

    interface RecordItemCallback {
        fun onClickRecord(recordEntity: RecordEntity)
    }


}