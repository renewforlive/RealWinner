package com.peter.realwinner.ui.activity

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.peter.realwinner.R
import com.peter.realwinner.constant.BundleType
import com.peter.realwinner.constant.MediaType
import com.peter.realwinner.constant.ThemeType
import com.peter.realwinner.model.data.EditInfoModel
import com.peter.realwinner.model.db.entity.RecordEntity
import com.peter.realwinner.ui.callback.EditInfoListener
import com.peter.realwinner.ui.callback.PickMediaCallback
import com.peter.realwinner.ui.dialog.EditDialog
import com.peter.realwinner.ui.dialog.UploadMediaDialog
import com.peter.realwinner.util.TimeUtil
import com.peter.realwinner.viewmodel.RecordDetailViewModel
import kotlinx.android.synthetic.main.activity_record_detail.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class RecordDetailActivity : BaseActivity(), PickMediaCallback, EditInfoListener {

    private val viewModel by viewModel<RecordDetailViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_detail)
        initData()
        initView()
        setUpObserver()
        initListener()
    }

    private fun initData() {
        val intent = intent
        val recordId = intent.getIntExtra(BundleType.RECORD_ID, -1)
        viewModel.setRecordId(recordId)
    }

    private fun initView() {
        if (viewModel.isNewData()) {
            record_detail_insert_button.visibility = View.VISIBLE
            record_detail_insert.visibility = View.VISIBLE
            record_detail_edit.visibility = View.GONE
            record_detail_delete.visibility = View.GONE
        } else {
            record_detail_insert_button.visibility = View.GONE
            record_detail_insert.visibility = View.GONE
            record_detail_edit.visibility = View.VISIBLE
            record_detail_delete.visibility = View.VISIBLE
        }
    }

    private fun setUpObserver() {
        viewModel.getTheme().observe(this, Observer {
            if (it == ThemeType.DEFAULT_THEME) {
                record_detail_top_layout.setBackgroundResource(R.color.colorPrimary)
                record_detail_post_layout.setBackgroundResource(R.drawable.shape_edit_bg)
                record_detail_title.setTextColor(ContextCompat.getColor(this, R.color.dark_blue))
                record_detail_time.setTextColor(ContextCompat.getColor(this, R.color.dark_blue))
                record_detail_content.setTextColor(ContextCompat.getColor(this, R.color.dark_blue))
                record_detail_content_text.setBackgroundResource(R.drawable.shape_edit_bg)
                record_detail_bottom_layout.setBackgroundResource(R.color.light_blue)
                record_detail_insert_button.setImageResource(R.drawable.ic_add_blue)
            } else {
                record_detail_top_layout.setBackgroundResource(R.color.light_brown)
                record_detail_post_layout.setBackgroundResource(R.drawable.shape_edit_bg_brown)
                record_detail_title.setTextColor(ContextCompat.getColor(this, R.color.brown))
                record_detail_time.setTextColor(ContextCompat.getColor(this, R.color.brown))
                record_detail_content.setTextColor(ContextCompat.getColor(this, R.color.brown))
                record_detail_content_text.setBackgroundResource(R.drawable.shape_edit_bg_brown)
                record_detail_bottom_layout.setBackgroundResource(R.color.yellow)
                record_detail_insert_button.setImageResource(R.drawable.ic_add_brown)
            }
        })
        viewModel.recordLiveData.observe(this, Observer {
            setUi(it)
        })
        viewModel.errorMsg.observe(this, Observer {
            showToast(it)
        })
        viewModel.saveDataLiveData.observe(this, Observer {
            showToast(getString(R.string.record_detail_save_success))
            finish()
        })
        viewModel.deleteDataLiveData.observe(this, Observer {
            showToast(getString(R.string.record_detail_delete_success))
            finish()
        })
    }

    private fun initListener() {
        record_detail_back.setOnClickListener {
            finish()
        }
        record_detail_insert_button.setOnClickListener {
            showUploadMediaDialog()
        }
        record_detail_insert.setOnClickListener {
            viewModel.setRecordEntity(getViewInfo())
            if (viewModel.validateRecord()) {
                viewModel.saveData()
            }
        }
        record_detail_edit.setOnClickListener {
            viewModel.setRecordEntity(getViewInfo())
            if (viewModel.validateRecord()) {
                viewModel.saveData()
            }
        }
        record_detail_delete.setOnClickListener {
            viewModel.deleteData()
        }
        record_detail_title_content.setOnClickListener {
            showEditInfoDialog()
        }
        record_detail_time_content.setOnClickListener {
            showTimeDialog()
        }
    }

    private fun setUi(recordEntity: RecordEntity) {
        val requestOptions = RequestOptions.centerCropTransform().error(R.drawable.ic_no_image)
        Glide.with(this)
            .load(recordEntity.post)
            .apply(requestOptions)
            .into(record_detail_post)
        record_detail_title_content.text = recordEntity.title
        val timeModel = TimeUtil.getTimeModelByTimestamp(recordEntity.time)
        val dateString = "${timeModel.year}-${timeModel.month}-${timeModel.day}"
        record_detail_time_content.text = dateString
        record_detail_content_text.setText(recordEntity.content)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getRecordEntity()
    }

    private fun getViewInfo() : RecordEntity {
        val title = record_detail_title_content.text.toString()
        val content = record_detail_content_text.text.toString()
        return RecordEntity (
            title = title,
            time = viewModel.getTime(),
            content = content,
            post = viewModel.getPost(),
            postType = viewModel.getPostType()
        )
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    private fun showUploadMediaDialog() {
        val dialog = UploadMediaDialog.newInstance()
        dialog.show(supportFragmentManager, UploadMediaDialog.TAG)
    }

    private fun showEditInfoDialog() {
        val editInfoModel = EditInfoModel(getString(R.string.edit_dialog_record_title), getString(R.string.edit_dialog_record_hint), getString(R.string.edit_dialog_record_button))
        val dialog = EditDialog.newInstance(editInfoModel)
        dialog.show(supportFragmentManager, EditDialog.TAG)
    }

    private fun showTimeDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val combineCal = GregorianCalendar(TimeZone.getDefault())
        val dialog = DatePickerDialog(this,
            { _, newYear, newMonth, dayOfMonth ->
                val dateString = "$newYear-${newMonth + 1}-$dayOfMonth"
                record_detail_time_content.text = dateString
                combineCal.set(newYear, newMonth, dayOfMonth)
                viewModel.setTime(combineCal.timeInMillis / 1000)
            }, year, month, day)
        dialog.show()
    }

    override fun onClickImage(uri: Uri) {
        val requestOptions = RequestOptions.centerCropTransform()
            .error(R.drawable.ic_no_image)
        Glide.with(this)
            .load(uri)
            .apply(requestOptions)
            .into(record_detail_post)
        viewModel.setPost(uri.toString())
        viewModel.setPostType(MediaType.IMAGE)
    }

    override fun onClickPhoto(uri: Uri) {
        val requestOptions = RequestOptions.centerCropTransform()
            .error(R.drawable.ic_no_image)
        Glide.with(this)
            .load(uri)
            .apply(requestOptions)
            .into(record_detail_post)
        viewModel.setPost(uri.toString())
        viewModel.setPostType(MediaType.IMAGE)
    }

    override fun onClickRecorder(uri: Uri) {
        val requestOptions = RequestOptions.centerCropTransform()
            .error(R.drawable.ic_no_image)
        Glide.with(this)
            .load(uri)
            .apply(requestOptions)
            .into(record_detail_post)
        viewModel.setPost(uri.toString())
        viewModel.setPostType(MediaType.VIDEO)
    }

    override fun onEdit(info: String) {
        record_detail_title_content.text = info
    }
}