    package com.example.nhatro24_7.ui.screen.customer.component

    import androidx.annotation.DrawableRes
    import androidx.annotation.StringRes
    import com.example.nhatro24_7.R
    import com.example.nhatro24_7.navigation.Routes

    sealed class BottomNavItem(
        val route: String,
        @DrawableRes val icon: Int,
        val label: String
    ) {
        object Home : BottomNavItem("customer_home", R.drawable.ic_home, "Trang chủ")
        object Chats : BottomNavItem(Routes.CHAT_LIST, R.drawable.ic_chat, "Nhắn tin")
        object Search : BottomNavItem("customer_search", R.drawable.ic_search, "Tìm kiếm")
        object Saved : BottomNavItem("customer_saved", R.drawable.ic_saved, "Đã lưu")
        object Profile : BottomNavItem("customer_profile", R.drawable.ic_profile, "Tài khoản")
    }
