package com.peter.realwinner.ui.callback

import com.peter.realwinner.model.db.entity.FriendInfoEntity

interface FriendListener {
    fun onDismiss(friendInfoEntity: FriendInfoEntity)
    fun onClickItem(friendInfoEntity: FriendInfoEntity)
}