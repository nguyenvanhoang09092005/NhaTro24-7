package com.example.nhatro24_7

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.nhatro24_7.data.repository.AuthRepository
import com.example.nhatro24_7.navigation.AppNavigation
import com.example.nhatro24_7.ui.theme.NhaTro24_7Theme
import com.example.nhatro24_7.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NhaTro24_7Theme {
                val navController = rememberNavController()

                val authViewModel: AuthViewModel = AuthViewModel(AuthRepository())

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(navController = navController, authViewModel = authViewModel)
                }
            }
        }
    }
}
