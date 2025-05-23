package com.example.nhatro24_7.data.repository

import com.example.nhatro24_7.data.model.User
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun registerUser(
        email: String,
        password: String,
        username: String,
        role: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: throw Exception("User ID is null")

            val user = User(id = userId, email = email, username = username, role = role)
            db.collection("users").document(userId).set(user).await()

            onComplete(true, role)
        } catch (e: Exception) {
            onComplete(false, e.localizedMessage)
        }
    }

    suspend fun loginUser(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        try {
            auth.signInWithEmailAndPassword(email, password).await()
            val role = fetchUserRole()
            onComplete(true, role)
        } catch (e: Exception) {
            onComplete(false, null)
        }
    }

    suspend fun loginWithGoogle(credential: AuthCredential, onComplete: (Boolean, String?) -> Unit) {
        try {
            val authResult = auth.signInWithCredential(credential).await()

            val userId = authResult.user?.uid ?: return onComplete(false, null)
            val email = authResult.user?.email ?: ""
            val rawUsername = authResult.user?.displayName
            val username = if (!rawUsername.isNullOrBlank()) rawUsername else "Username"

            val user = db.collection("users").document(userId).get().await()

            if (!user.exists()) {
                val newUser = User(id = userId, email = email, username = username, role = "customer")
                db.collection("users").document(userId).set(newUser).await()
            }

            val role = fetchUserRole()
            onComplete(true, role)
        } catch (e: Exception) {
            onComplete(false, null)
        }
    }

    suspend fun loginWithGithub(credential: AuthCredential, onComplete: (Boolean, String?) -> Unit) {
        try {
            val authResult = auth.signInWithCredential(credential).await()
            val userId = authResult.user?.uid ?: return onComplete(false, null)
            val email = authResult.user?.email ?: ""
            val rawUsername = authResult.user?.displayName
            val username = if (!rawUsername.isNullOrBlank()) rawUsername else "Username"

            val userDoc = db.collection("users").document(userId).get().await()
            var role = "customer"

            if (!userDoc.exists()) {
                val newUser = User(id = userId, email = email, username = username, role = role)
                db.collection("users").document(userId).set(newUser).await()
            } else {
                role = userDoc.getString("role") ?: "customer"
            }

            onComplete(true, role)
        } catch (e: Exception) {
            onComplete(false, null)
        }
    }

    suspend fun fetchUserRole(): String {
        val firebaseUser = auth.currentUser ?: return "guest"
        val userId = firebaseUser.uid

        return try {
            val document = db.collection("users").document(userId).get().await()
            document.getString("role") ?: "customer"
        } catch (e: Exception) {
            "customer"
        }
    }

    fun logout() {
        auth.signOut()
    }
}
