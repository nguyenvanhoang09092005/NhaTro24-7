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
    const val CUSTOMER_SEARCH = "customer_search"
    const val CUSTOMER_SAVED = "customer_saved"
    const val CUSTOMER_HISTORY = "customer_activity_history"
    const val CUSTOMER_PROFILE_DETAIL = "customer_profile_detail"
    const val CUSTOMER_LIKED_HISTORY = "customer_liked_history"
    const val ROOM_DETAIL = "roomDetail/{roomId}"
    const val BOOKING_PENDING = "bookingPending"
    const val CUSTOMER_NOTIFICATIONS = "customer_notifications"
    const val CUSTOMER_BOOKING_HISTORY = "customer_booking_history"
    const val PAYMENT_SCREEN = "paymentScreen/{bookingRequestId}/{roomId}"
    fun paymentScreenWithId(bookingRequestId: String, roomId: String) = "paymentScreen/$bookingRequestId/$roomId"

    // Landlord
    const val LANDLORD_HOME = "landlord_home"
    const val LANDLORD_ADD_POST = "landlord_add_post"
    const val LANDLORD_NOTIFY = "landlord_notify"
    const val LANDLORD_PROFILE = "landlord_profile"
    const val LANDLORD_PROFILE_DETAIL = "landlord_profile_detail"
    const val LANDLORD_BOOKING_REQUESTS = "landlord_booking_requests"
    const val BOOKING_DETAIL = "booking_detail/{bookingRequestId}"

    const val CUSTOMER_CHAT = "customer_chat/{chatId}/{receiverId}/{receiverName}/{receiverAvatarUrl}"
    const val LANDLORD_CHAT = "landlord_chat/{chatId}/{receiverId}/{receiverName}/{receiverAvatarUrl}"

    fun customerChatRoute(chatId: String, receiverId: String, receiverName: String, receiverAvatarUrl: String) =
        "customer_chat/$chatId/$receiverId/$receiverName/$receiverAvatarUrl"

    fun landlordChatRoute(chatId: String, receiverId: String, receiverName: String, receiverAvatarUrl: String) =
        "landlord_chat/$chatId/$receiverId/$receiverName/$receiverAvatarUrl"

    const val CHAT_LIST = "chat_list"


    // Account settings
    const val CHANGE_PASSWORD = "change_password"
    const val VERIFY_ACCOUNT = "verify_account"
    const val LINK_ACCOUNTS = "link_accounts"
    const val DELETE_ACCOUNT = "delete_account"
}
