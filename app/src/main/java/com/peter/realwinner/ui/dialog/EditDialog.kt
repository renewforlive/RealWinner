package com.peter.realwinner.ui.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.peter.realwinner.R
import com.peter.realwinner.constant.ThemeType
import com.peter.realwinner.model.data.EditInfoModel
import com.peter.realwinner.ui.callback.EditInfoListener
import com.peter.realwinner.util.ScreenUtil
import com.peter.realwinner.util.SharedPreferencesUtil
import kotlinx.android.synthetic.main.dialog_edit.*
import org.koin.java.KoinJavaComponent.inject
import java.lang.ClassCastException

class EditDialog : DialogFragment() {

    companion object {
        const val TAG = "edit_dialog"
        private const val EDIT_INFO = "edit_info"
        fun newInstance(editInfoModel: EditInfoModel) : EditDialog {
            val bundle = Bundle()
            bundle.putParcelable(EDIT_INFO, editInfoModel)
            val dialog = EditDialog()
            dialog.arguments = bundle
            return dialog
        }
    }

    private val sharedPreferencesUtil by inject(SharedPreferencesUtil::class.java)
    private var editInfoModel: EditInfoModel? = null
    private var listener: EditInfoListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = activity as EditInfoListener
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.WhiteRoundCornerDialogTheme)
        arguments?.let {
            editInfoModel = it.getParcelable(EDIT_INFO)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        context?.apply {
            if (sharedPreferencesUtil.getStyle() == ThemeType.DEFAULT_THEME) {
                edit_button.backgroundTintList = ContextCompat.getColorStateList(this, R.color.medium_blue)
                edit_edit_text.setBackgroundResource(R.drawable.shape_edit_bg)
            } else {
                edit_button.backgroundTintList = ContextCompat.getColorStateList(this, R.color.light_brown)
                edit_edit_text.setBackgroundResource(R.drawable.shape_edit_bg_brown)
            }
        }

        editInfoModel?.let {
            edit_title.text = it.title
            edit_edit_text.hint = it.hint
            it.buttonText?.let { buttonText ->
                edit_button.text = buttonText
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initListener()
    }

    private fun initListener() {
        edit_button.setOnClickListener {
            val content = edit_edit_text.text.toString()
            if (content.isNotBlank()) {
                listener?.onEdit(content)
                dismiss()
            }
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
}