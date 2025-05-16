package com.example.nhatro24_7.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nhatro24_7.data.model.Statistic
import com.example.nhatro24_7.data.repository.StatisticRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StatisticViewModel(
    private val repository: StatisticRepository = StatisticRepository()
) : ViewModel() {

    private val _statistic = MutableStateFlow(Statistic())
    val statistic: StateFlow<Statistic> = _statistic

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchStatistic(landlordId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // Lấy thống kê từ repository
                val result = repository.getStatisticByLandlord(landlordId)

                // Cập nhật giá trị vào _statistic
                _statistic.value = Statistic(
                    revenue = result.revenue,
                    totalBookings = result.totalBookings,
                    totalCancellations = result.totalCancellations,
                    totalCheckouts = result.totalCheckouts,
                    totalViews = result.totalViews,
                    averageRating = result.averageRating,
                    paidRoomCount = result.paidRoomCount,
                    revenueByMonth = result.revenueByMonth,
                    viewsByDay = result.viewsByDay
                )
            } catch (e: Exception) {
                Log.e("StatisticViewModel", "Lỗi: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
