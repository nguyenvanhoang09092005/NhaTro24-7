package com.example.nhatro24_7.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nhatro24_7.data.model.User
import com.example.nhatro24_7.data.repository.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    var authState: AuthState = AuthState.Idle
        private set
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

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

    fun updateLandlordInfo(user: User, onResult: (Boolean) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            onResult(false)
            return
        }

        viewModelScope.launch {
            try {
                val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                db.collection("users")
                    .document(userId)
                    .set(user.copy(id = userId))
                    .addOnSuccessListener {
                        onResult(true)
                    }
                    .addOnFailureListener {
                        onResult(false)
                    }
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }


    fun getCurrentUserId(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }

    fun addBankAccount(accountNumber: String, bankName: String, accountHolder: String, onResult: (Boolean) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            onResult(false)
            return
        }

        val userRef = FirebaseFirestore.getInstance().collection("users").document(userId)

        // Cập nhật thông tin tài khoản ngân hàng vào document người dùng
        userRef.update(
            "landlordBankAccount", accountNumber,
            "landlordBankName", bankName,
            "landlordName", accountHolder
        )
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener { exception ->
                Log.e("AddBankAccount", "Lỗi khi cập nhật tài khoản ngân hàng: ", exception)
                onResult(false)
            }
    }

    fun getBankAccounts(onResult: (List<User>) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            onResult(emptyList())
            return
        }

        val userRef = FirebaseFirestore.getInstance().collection("users").document(userId)

        // Nếu tài khoản ngân hàng được lưu trong document người dùng
        userRef.get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                if (user != null && user.landlordBankAccount.isNotEmpty()) {
                    onResult(listOf(user)) // Trả về danh sách chứa thông tin của tài khoản ngân hàng
                } else {
                    onResult(emptyList()) // Nếu không có tài khoản ngân hàng
                }
            }
            .addOnFailureListener { exception ->
                Log.e("BankAccountList", "Lỗi khi lấy tài khoản ngân hàng: ", exception)
                onResult(emptyList())
            }
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
