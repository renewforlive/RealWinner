package com.peter.realwinner.ui.callback

import android.net.Uri

interface PickMediaCallback {
    fun onClickImage(uri: Uri)
    fun onClickPhoto(uri: Uri)
    fun onClickRecorder(uri: Uri)
}