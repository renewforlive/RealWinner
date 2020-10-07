package com.peter.realwinner.model.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EditInfoModel (
    val title: String,
    val hint: String,
    val buttonText: String? = null
) : Parcelable