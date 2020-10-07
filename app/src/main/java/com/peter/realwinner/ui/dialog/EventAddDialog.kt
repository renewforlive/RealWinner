package com.peter.realwinner.ui.dialog

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.peter.realwinner.R
import com.peter.realwinner.constant.ThemeType
import com.peter.realwinner.model.db.entity.EventEntity
import com.peter.realwinner.util.ScreenUtil
import com.peter.realwinner.util.TimeUtil
import com.peter.realwinner.viewmodel.EventAddViewModel
import kotlinx.android.synthetic.main.dialog_event_add.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class EventAddDialog : DialogFragment() {

    companion object {
        const val TAG = "event_dialog"
        fun newInstance() : EventAddDialog {
            return EventAddDialog()
        }
    }

    private val viewModel by viewModel<EventAddViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.WhiteRoundCornerDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_event_add, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpObserver()
        initListener()
    }

    private fun setUpObserver() {
        viewModel.getTheme().observe(viewLifecycleOwner, Observer {
            context?.apply {
                if (it == ThemeType.DEFAULT_THEME) {
                    event_add_name.setTextColor(ContextCompat.getColor(this, R.color.dark_blue))
                    event_add_name_text.setBackgroundResource(R.drawable.shape_edit_bg)
                    event_add_content.setTextColor(ContextCompat.getColor(this, R.color.dark_blue))
                    event_add_content_text.setBackgroundResource(R.drawable.shape_edit_bg)
                    event_add_notification.setTextColor(ContextCompat.getColor(this, R.color.dark_blue))
                    event_add_start_time.setTextColor(ContextCompat.getColor(this, R.color.dark_blue))
                    event_add_switch.trackTintList = ContextCompat.getColorStateList(this, R.color.light_blue)
                    event_add_button.backgroundTintList = ContextCompat.getColorStateList(this, R.color.medium_blue)
                } else {
                    event_add_name.setTextColor(ContextCompat.getColor(this, R.color.brown))
                    event_add_name_text.setBackgroundResource(R.drawable.shape_edit_bg_brown)
                    event_add_content.setTextColor(ContextCompat.getColor(this, R.color.brown))
                    event_add_content_text.setBackgroundResource(R.drawable.shape_edit_bg_brown)
                    event_add_notification.setTextColor(ContextCompat.getColor(this, R.color.brown))
                    event_add_start_time.setTextColor(ContextCompat.getColor(this, R.color.brown))
                    event_add_switch.trackTintList = ContextCompat.getColorStateList(this, R.color.brown)
                    event_add_button.backgroundTintList = ContextCompat.getColorStateList(this, R.color.light_brown)
                }
            }

        })
        viewModel.errorMsgLiveData.observe(viewLifecycleOwner, Observer {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        })
        viewModel.validateLiveDate.observe(viewLifecycleOwner, Observer {
            if (it) {
                viewModel.saveInfoToDb()
            }
        })
        viewModel.saveLiveData.observe(viewLifecycleOwner, Observer {
            if (it) {
                Toast.makeText(context, getString(R.string.event_add_save_success), Toast.LENGTH_LONG).show()
                dismiss()
            }
        })
    }

    private fun initListener() {
        event_add_start_time_text.setOnClickListener {
            showDatePicker()
        }
        event_add_button.setOnClickListener {
            viewModel.validate(getViewInfo())
        }
    }

    override fun onResume() {
        super.onResume()
        if (showsDialog) {
            val width = (ScreenUtil.getScreenWidth() * 0.8).toInt()
            val height = ConstraintLayout.LayoutParams.WRAP_CONTENT
            dialog?.window?.setLayout(width, height)
        }
    }

    private fun getViewInfo() : EventEntity {
        return EventEntity(
            title = event_add_name_text.text.toString(),
            content = event_add_content_text.text.toString(),
            isNotification = if (event_add_switch.isChecked) 1 else 0,
            insertTime = System.currentTimeMillis(),
            startTime = viewModel.getStartTime()
        )
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        context?.apply {
            DatePickerDialog(this,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    val date = TimeUtil.getDateByTimestamp(calendar.timeInMillis / 1000)
                    viewModel.setStartTime(date)
                    val birth = "$year-${month + 1}-$dayOfMonth"
                    event_add_start_time_text.text = birth
                }, year, month, day).show()
        }
    }
}