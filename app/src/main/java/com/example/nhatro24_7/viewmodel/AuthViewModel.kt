package com.example.nhatro24_7.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nhatro24_7.data.repository.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
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

//     Đăng nhập bằng Google
fun signInWithGoogle(account: GoogleSignInAccount, onResult: (Boolean, String?) -> Unit) {
    viewModelScope.launch {
        val credential: AuthCredential = GoogleAuthProvider.getCredential(account.idToken, null)
        authState = AuthState.Loading
        authRepository.loginWithGoogle(credential) { success, role ->
            if (success) {
                authState = AuthState.Authenticated(role ?: "customer")
                onResult(true, role ?: "customer")
            } else {
                authState = AuthState.Error("Đăng nhập Google thất bại.")
                onResult(false, "customer")
            }
        }
    }
}

    //     Đăng nhập bằng Github
    fun signUpWithGithub(credential: AuthCredential, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            authState = AuthState.Loading
            authRepository.loginWithGithub(credential) { success, role ->
                if (success) {
                    authState = AuthState.Authenticated(role ?: "customer")
                    onResult(true, role ?: "customer")
                } else {
                    authState = AuthState.Error("Đăng nhập GitHub thất bại.")
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