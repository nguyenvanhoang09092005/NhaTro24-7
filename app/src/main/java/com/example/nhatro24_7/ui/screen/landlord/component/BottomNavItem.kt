package com.example.nhatro24_7.ui.screen.landlord.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.nhatro24_7.R

sealed class BottomNavItem(
    val route: String,
    @DrawableRes val icon: Int,
    val label: String
) {
    object Home : BottomNavItem("landlord_home", R.drawable.ic_home, "Trang chủ")
    object Chats : BottomNavItem("landlord_chats", R.drawable.ic_chat, "Nhắn tin")
    object Post : BottomNavItem("landlord_add_post", R.drawable.ic_add, "Đăng tin")
    object Notification : BottomNavItem("landlord_notify", R.drawable.ic_notification, "Thông báo")
    object Profile : BottomNavItem("landlord_profile", R.drawable.ic_profile, "Tài khoản")

}
