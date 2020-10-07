package com.peter.realwinner.ui.dialog

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.peter.realwinner.R
import com.peter.realwinner.constant.ThemeType
import com.peter.realwinner.model.db.entity.FriendInfoEntity
import com.peter.realwinner.util.ScreenUtil
import com.peter.realwinner.viewmodel.AddViewModel
import kotlinx.android.synthetic.main.fragment_add.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class AddDialog : DialogFragment() {

    companion object {
        private const val WRITE_EXTERNAL_STORAGE = 1002
        private const val PICKER_IMAGE = 111
        const val TAG = "add_dialog"

        fun newInstance() : AddDialog {
            return AddDialog()
        }
    }

    private val viewModel by viewModel(AddViewModel::class)
    private lateinit var bloodAdapter: ArrayAdapter<String>
    private lateinit var heightAdapter: ArrayAdapter<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomDialog)
    }

    override fun onResume() {
        super.onResume()
        if (showsDialog) {
            val width = (ScreenUtil.getScreenWidth() * 0.85).toInt()
            val height = (ScreenUtil.getScreenHeight() * 0.85).toInt()
            dialog?.window?.setLayout(width, height)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        context?.apply {
            bloodAdapter = ArrayAdapter(this, R.layout.spinner_theme, this.resources.getStringArray(R.array.blood_type))
            add_blood_spinner.adapter = bloodAdapter
            Glide.with(this).load(R.drawable.ic_user).into(add_head_image)
        }
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
                    add_top_layout.setBackgroundResource(R.color.colorPrimary)
                    add_head_image_icon.setImageResource(R.drawable.ic_add_blue)
                    add_full_name_edit.setBackgroundResource(R.drawable.shape_edit_bg)
                    add_nick_name_edit.setBackgroundResource(R.drawable.shape_edit_bg)
                    add_cellphone_edit.setBackgroundResource(R.drawable.shape_edit_bg)
                    add_personality_edit.setBackgroundResource(R.drawable.shape_edit_bg)
                    add_hobby_edit.setBackgroundResource(R.drawable.shape_edit_bg)
                    add_line_edit.setBackgroundResource(R.drawable.shape_edit_bg)
                    add_facebook_edit.setBackgroundResource(R.drawable.shape_edit_bg)
                    add_ig_edit.setBackgroundResource(R.drawable.shape_edit_bg)
                    add_other_edit.setBackgroundResource(R.drawable.shape_edit_bg)
                    add_update_button.backgroundTintList = ContextCompat.getColorStateList(this, R.color.medium_blue)
                } else {
                    add_top_layout.setBackgroundResource(R.color.light_brown)
                    add_head_image_icon.setImageResource(R.drawable.ic_add_brown)
                    add_full_name_edit.setBackgroundResource(R.drawable.shape_edit_bg_brown)
                    add_nick_name_edit.setBackgroundResource(R.drawable.shape_edit_bg_brown)
                    add_cellphone_edit.setBackgroundResource(R.drawable.shape_edit_bg_brown)
                    add_personality_edit.setBackgroundResource(R.drawable.shape_edit_bg_brown)
                    add_hobby_edit.setBackgroundResource(R.drawable.shape_edit_bg_brown)
                    add_line_edit.setBackgroundResource(R.drawable.shape_edit_bg_brown)
                    add_facebook_edit.setBackgroundResource(R.drawable.shape_edit_bg_brown)
                    add_ig_edit.setBackgroundResource(R.drawable.shape_edit_bg_brown)
                    add_other_edit.setBackgroundResource(R.drawable.shape_edit_bg_brown)
                    add_update_button.backgroundTintList = ContextCompat.getColorStateList(this, R.color.light_brown)
                }
            }
        })
        viewModel.getHeightArray().observe(viewLifecycleOwner, Observer { num ->
            context?.apply {
                heightAdapter = ArrayAdapter(this, R.layout.spinner_theme, num)
                add_height_spinner.adapter = heightAdapter
            }
        })
        viewModel.errorMsgLiveData.observe(viewLifecycleOwner, Observer {
            showToast(it)
        })
        viewModel.validateLiveData.observe(viewLifecycleOwner, Observer {
            viewModel.saveToDb()
        })
        viewModel.saveToInternalLiveData.observe(viewLifecycleOwner, Observer {
            viewModel.decodeFile(it)
        })
        viewModel.saveLiveData.observe(viewLifecycleOwner, Observer {
            showToast(getString(R.string.add_update_success))
            dismiss()
        })
        viewModel.newBitmapLiveData.observe(viewLifecycleOwner, Observer {
            Glide.with(this)
                .load(it)
                .apply(RequestOptions.circleCropTransform())
                .into(add_head_image)
        })
    }

    private fun initListener() {
        add_birth_content.setOnClickListener {
            showBirthDialog()
        }
        add_update_button.setOnClickListener {
            getViewByUi()
        }
        add_head_image_icon.setOnClickListener {
            validatePermission()
        }
    }

    private fun showBirthDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        context?.apply {
            DatePickerDialog(this,
                { _, year, month, dayOfMonth ->
                    val birth = "$year-${month + 1}-$dayOfMonth"
                    add_birth_content.text = birth
                }, year, month, day).show()
        }
    }

    private fun getViewByUi() {
        val name = add_full_name_edit.text.toString()
        val nickname = add_nick_name_edit.text.toString()
        val birth = add_birth_content.text.toString()
        val blood = add_blood_spinner.selectedItem.toString()
        val height = add_height_spinner.selectedItem.toString().toInt()
        val cellphone = add_cellphone_edit.text.toString()
        val personality = add_personality_edit.text.toString()
        val hobby = add_hobby_edit.text.toString()
        val line = add_line_edit.text.toString()
        val facebook = add_facebook_edit.text.toString()
        val ig = add_ig_edit.text.toString()
        val other = add_other_edit.text.toString()
        val friendInfoEntity = FriendInfoEntity (
            name = name,
            nickName = nickname,
            birth = birth,
            blood = blood,
            height = height,
            cellphone = cellphone,
            personality = personality,
            hobby = hobby,
            line = line,
            facebook = facebook,
            instagram = ig,
            other = other
        )
        viewModel.validate(friendInfoEntity)
    }

    private fun validatePermission() {
        context?.apply {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), WRITE_EXTERNAL_STORAGE)
            } else {
                startLoadImage()
            }
        }
    }

    private fun startLoadImage() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayListOf("image/*"))
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICKER_IMAGE)
    }

    private fun showToast(msg: String) {
        Toast.makeText(this.activity, msg, Toast.LENGTH_LONG).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICKER_IMAGE && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            uri?.let {
                viewModel.saveToInternal(it)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLoadImage()
        }
    }
}