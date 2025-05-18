package com.example.nhatro24_7.ui.screen.landlord.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.nhatro24_7.R
import com.example.nhatro24_7.navigation.Routes

sealed class BottomNavItem(
    val route: String,
    @DrawableRes val icon: Int,
    val label: String
) {
    object Home : BottomNavItem("landlord_home", R.drawable.ic_home, "Trang chủ")
    object Chats : BottomNavItem(Routes.CHAT_LIST, R.drawable.ic_chat, "Nhắn tin")
    object Post : BottomNavItem("landlord_add_post", R.drawable.ic_add, "Đăng tin")
    object Notification : BottomNavItem("landlord_statistics", R.drawable.graph, "Thống Kê")
    object Profile : BottomNavItem("landlord_profile", R.drawable.ic_profile, "Tài khoản")

}
