package com.peter.realwinner.ui.dialog

import android.Manifest.permission.CAMERA
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import com.peter.realwinner.R
import com.peter.realwinner.ui.callback.PickMediaCallback
import com.peter.realwinner.util.ScreenUtil
import kotlinx.android.synthetic.main.dialog_upload_media.*
import java.lang.ClassCastException

class UploadMediaDialog : DialogFragment() {

    companion object {
        const val TAG = "upload_media"
        private const val PERMISSION_REQUEST_CODE = 106
        private const val PICK_MEDIA = 1002
        private const val TAKE_PHOTO = 1003
        private const val RECORDER_VIDEO = 1004
        fun newInstance() : UploadMediaDialog {
            return UploadMediaDialog()
        }
    }

    private var type = 0
    private var mCurrentPath = String()

    private var listener: PickMediaCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = activity as PickMediaCallback
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.WhiteRoundCornerDialogTheme)
    }

    override fun onResume() {
        super.onResume()
        if (showsDialog) {
            val width = (ScreenUtil.getScreenWidth() * 0.8).toInt()
            val height = ConstraintLayout.LayoutParams.WRAP_CONTENT
            dialog?.window?.setLayout(width, height)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_upload_media, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initListener()
    }

    private fun initListener() {
        upload_media_picture_layout.setOnClickListener {
            selectAlbum()
        }
        upload_media_photo_layout.setOnClickListener {
            type = TAKE_PHOTO
            validatePermission()
        }
        upload_media_video_layout.setOnClickListener {
            type = RECORDER_VIDEO
            validatePermission()
        }
        upload_media_cancel_layout.setOnClickListener {
            dismiss()
        }
    }

    private fun selectAlbum() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayListOf("video/*", "image/*"))
        startActivityForResult(Intent.createChooser(intent, "select album"), PICK_MEDIA)
    }

    private fun validatePermission() {
        context?.apply {
            if (ActivityCompat.checkSelfPermission(this, CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(CAMERA, WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
            } else {
                when (type) {
                    TAKE_PHOTO -> {
                        startPhoto()
                    }
                    RECORDER_VIDEO -> {
                        startRecord()
                    }
                }
            }
        }
    }

    private fun startPhoto() {
        val values = ContentValues(1)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        context?.apply {
            val fileUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if(intent.resolveActivity(packageManager) != null) {
                mCurrentPath = fileUri?.toString() ?: String()
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                startActivityForResult(intent, TAKE_PHOTO)
            }
        }
    }

    private fun startRecord() {
        val values = ContentValues(1)
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
        context?.apply {
            val fileUri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            if(intent.resolveActivity(packageManager) != null) {
                mCurrentPath = fileUri?.toString() ?: String()
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                startActivityForResult(intent, RECORDER_VIDEO)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            when (type) {
                TAKE_PHOTO -> {
                    startPhoto()
                }
                RECORDER_VIDEO -> {
                    startRecord()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PICK_MEDIA -> {
                if (resultCode == RESULT_OK) {
                    data?.let {
                        it.data?.let { uri ->
                            listener?.onClickImage(uri)
                            dismiss()
                        }
                    }
                }
            }
            TAKE_PHOTO -> {
                listener?.onClickPhoto(Uri.parse(mCurrentPath))
                dismiss()
            }
            RECORDER_VIDEO -> {
                listener?.onClickRecorder(Uri.parse(mCurrentPath))
                dismiss()
            }
        }
    }
}