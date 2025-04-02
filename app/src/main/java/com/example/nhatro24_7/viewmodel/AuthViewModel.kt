package com.example.nhatro24_7.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nhatro24_7.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    var authState: AuthState = AuthState.Idle
        private set

    fun signUp(email: String, password: String, username: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            authState = AuthState.Loading
            authRepository.registerUser(email, password, username, "customer") { success, role ->
                if (success) {
                    authState = AuthState.Authenticated(role ?: "customer")
                    onResult(true, role ?: "customer")
                } else {
                    authState = AuthState.Error("Đăng ký thất bại. Vui lòng thử lại.")
                    onResult(false, "customer")
                }
            }
        }
    }


    fun signIn(email: String, password: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            authState = AuthState.Loading
            authRepository.loginUser(email, password) { success, role ->
                if (success) {
                    authState = AuthState.Authenticated(role ?: "customer")
                    onResult(true, role ?: "customer")
                } else {
                    authState = AuthState.Error("Đăng nhập thất bại.")
                    onResult(false, "customer")
                }
            }
        }
    }

    fun signOut() {
        authRepository.logout()
        authState = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Authenticated(val role: String) : AuthState()
    data class Error(val message: String) : AuthState()
}
