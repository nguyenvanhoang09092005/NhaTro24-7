package com.example.nhatro24_7.viewmodel

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

    private val _statistic = MutableStateFlow<Statistic?>(null)
    val statistic: StateFlow<Statistic?> = _statistic

    fun fetchStatistic(landlordId: String) {
        viewModelScope.launch {
            _statistic.value = repository.getStatisticByLandlord(landlordId)
        }
    }
}
