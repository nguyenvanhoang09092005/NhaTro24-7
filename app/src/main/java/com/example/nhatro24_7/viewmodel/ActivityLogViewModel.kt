package com.example.nhatro24_7.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nhatro24_7.data.model.ActivityLog
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ActivityLogViewModel @Inject constructor(
    private val db: FirebaseFirestore
) : ViewModel() {

    private val _activityLogs = MutableStateFlow<List<ActivityLog>>(emptyList())
    val activityLogs: StateFlow<List<ActivityLog>> = _activityLogs

    fun loadActivityLogsForUser(userId: String) {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("activity_logs")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("action", "Xem phòng")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()

                val logs = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(ActivityLog::class.java)?.copy(id = doc.id)
                }

                _activityLogs.value = logs
            } catch (e: Exception) {
                Log.e("ActivityLogViewModel", "Lỗi khi tải lịch sử hoạt động", e)
            }
        }
    }
}
