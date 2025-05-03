package com.example.nhatro24_7.navigation

object Routes {
    // Auth
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"

    // Admin
    const val ADMIN_HOME = "admin_home"

    // Customer
    const val CUSTOMER_HOME = "customer_home"
    const val CUSTOMER_PROFILE = "customer_profile"
    const val CUSTOMER_CHATS = "customer_chat"
    const val CUSTOMER_SEARCH = "customer_search"
    const val CUSTOMER_SAVED = "customer_saved"
    const val CUSTOMER_HISTORY = "customer_activity_history"
    const val CUSTOMER_PROFILE_DETAIL = "customer_profile_detail"
    const val CUSTOMER_LIKED_HISTORY = "customer_liked_history"
    const val ROOM_DETAIL = "roomDetail/{roomId}"
    const val BOOKING_PENDING = "bookingPending"
    const val PAYMENT_SCREEN = "paymentScreen"
    const val CUSTOMER_NOTIFICATIONS = "customer_notifications"


    // Landlord
    const val LANDLORD_HOME = "landlord_home"
    const val LANDLORD_CHATS = "landlord_chats"
    const val LANDLORD_ADD_POST = "landlord_add_post"
    const val LANDLORD_NOTIFY = "landlord_notify"
    const val LANDLORD_PROFILE = "landlord_profile"
    const val LANDLORD_BOOKING_REQUESTS = "landlord_booking_requests"
    const val BOOKING_DETAIL = "booking_detail/{roomId}/{userId}"



    // Account settings
    const val CHANGE_PASSWORD = "change_password"
    const val VERIFY_ACCOUNT = "verify_account"
    const val LINK_ACCOUNTS = "link_accounts"
    const val DELETE_ACCOUNT = "delete_account"
}
