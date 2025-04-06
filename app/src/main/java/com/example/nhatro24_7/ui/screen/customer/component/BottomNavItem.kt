package com.example.nhatro24_7.ui.screen.customer.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.nhatro24_7.R

sealed class BottomNavItem(
    val route: String,
    @DrawableRes val icon: Int,
    val label: String
) {
    object Home : BottomNavItem("home", R.drawable.ic_home, "Trang chủ")
    object Chats : BottomNavItem("chats", R.drawable.ic_chat, "Nhắn tin")
    object Search : BottomNavItem("search", R.drawable.ic_search, "Tìm kiếm")
    object Saved : BottomNavItem("saved", R.drawable.ic_saved, "Đã lưu")
    object Profile : BottomNavItem("profile", R.drawable.ic_profile, "Tài khoản")
}
