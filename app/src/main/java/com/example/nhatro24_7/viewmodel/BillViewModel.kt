package com.example.nhatro24_7.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.nhatro24_7.data.model.Bill
import com.example.nhatro24_7.data.repository.BillRepository


class BillViewModel(private val repository: BillRepository = BillRepository()) : ViewModel() {

    var userBills by mutableStateOf<List<Bill>>(emptyList())
    var isLoading by mutableStateOf(false)

    fun loadUserBills(userId: String) {
        isLoading = true
        repository.getBillsByUser(userId) { bills ->
            userBills = bills
            isLoading = false
        }
    }
}
