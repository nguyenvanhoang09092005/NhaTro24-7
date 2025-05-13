package com.example.nhatro24_7

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.nhatro24_7.data.repository.AuthRepository
import com.example.nhatro24_7.data.DataStore.SettingsDataStore
import com.example.nhatro24_7.navigation.AppNavigation
import com.example.nhatro24_7.ui.theme.NhaTro24_7Theme
import com.example.nhatro24_7.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.hilt.navigation.compose.hiltViewModel
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.nhatro24_7.viewmodel.ChatViewModel
import com.example.nhatro24_7.viewmodel.RoomViewModel


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val settingsStore = SettingsDataStore(context)
            val coroutineScope = rememberCoroutineScope()

            // Đọc giá trị từ DataStore
            val isDarkMode by settingsStore.isDarkMode.collectAsState(initial = false)
            val selectedLanguage by settingsStore.selectedLanguage.collectAsState(initial = "Tiếng Việt")

            val navController = rememberNavController()
            val authViewModel: AuthViewModel = hiltViewModel()
            val chatViewModel: ChatViewModel = hiltViewModel()
            val roomViewModel: RoomViewModel = hiltViewModel()
//            val notificationViewModel: NotificationViewModel = hiltViewModel()

            // Áp dụng theme
            NhaTro24_7Theme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        navController = navController,
                        chatViewModel = chatViewModel,
                        romViewModel = roomViewModel,
                        authViewModel = authViewModel,
                        isDarkTheme = isDarkMode,
                        selectedLanguage = selectedLanguage,
                        onToggleTheme = { enabled ->
                            coroutineScope.launch {
                                settingsStore.saveDarkMode(enabled)
                            }
                        },
                        onLanguageChange = { lang ->
                            coroutineScope.launch {
                                settingsStore.saveLanguage(lang)
                            }
                        }
                    )
                }
            }
        }
    }
}
