package com.example.nhatro24_7.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nhatro24_7.data.repository.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

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

    fun checkIfLoggedIn(onLoggedIn: (String) -> Unit) {
        viewModelScope.launch {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val role = authRepository.fetchUserRole()
                onLoggedIn(role)
            } else {
                onLoggedIn("guest") // Quan trọng: Luôn gọi callback để tránh kẹt màn hình splash
            }
        }
    }

    fun getCurrentUserId(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }


    fun signOut(onDone: () -> Unit = {}) {
        authRepository.logout()
        authState = AuthState.Idle
        onDone()
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Authenticated(val role: String) : AuthState()
    data class Error(val message: String) : AuthState()
}
