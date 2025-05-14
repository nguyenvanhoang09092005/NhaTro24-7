package com.example.nhatro24_7.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.work.*
import com.example.nhatro24_7.util.NotificationWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor() : ViewModel() {

    fun startNotificationWork(context: Context, message: String) {
        val inputData = workDataOf("user_message" to message)

        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInputData(inputData)
            .setInitialDelay(5, java.util.concurrent.TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }

    fun showNotification(context: Context, title: String, message: String) {
        val inputData = workDataOf(
            "title" to title,
            "user_message" to message
        )

        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }
}
