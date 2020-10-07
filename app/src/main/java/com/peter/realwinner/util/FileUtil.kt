package com.peter.realwinner.util

import android.content.ContentResolver
import android.net.Uri
import android.webkit.MimeTypeMap
import com.peter.realwinner.RealWinnerApplication
import com.peter.realwinner.constant.MediaType

object FileUtil {

    fun getMimeType(uri: Uri) : String {
        var mimeType = String()
        val contentResolver = RealWinnerApplication.getBaseContext().contentResolver
        uri.scheme?.let {
            mimeType = if (it == ContentResolver.SCHEME_CONTENT) {
                contentResolver.getType(uri) ?: String()
            } else {
                val mime = MimeTypeMap.getSingleton()
                mime.getExtensionFromMimeType(contentResolver.getType(uri)) ?: String()
            }
        }
        return mimeType
    }

    fun getMediaType(mimeType: String) : String {
        return when {
            mimeType.startsWith("image/") -> {
                MediaType.IMAGE
            }
            mimeType.startsWith("video/") -> {
                MediaType.VIDEO
            }
            else -> {
                MediaType.NONE
            }
        }
    }
}