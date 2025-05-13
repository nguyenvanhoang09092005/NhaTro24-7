@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.nhatro24_7.ui.screen.customer.saveroom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nhatro24_7.data.model.Room
import com.example.nhatro24_7.data.model.SavedRoom
import com.example.nhatro24_7.navigation.Routes
import com.example.nhatro24_7.ui.screen.customer.component.BottomNavBar
import com.example.nhatro24_7.ui.screen.customer.home.components.RoomItem
import com.example.nhatro24_7.viewmodel.AuthViewModel
import com.example.nhatro24_7.viewmodel.RoomViewModel
import kotlinx.coroutines.launch

@Composable
fun SavedRoomScreen(
    navController: NavController,
    viewModel: AuthViewModel,
    roomViewModel: RoomViewModel = viewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val userId = viewModel.getCurrentUserId()
    val savedRooms = remember { mutableStateListOf<Room>() }

    LaunchedEffect(userId) {
        if (userId != null) {
            roomViewModel.getSavedRooms(userId) { rooms ->
                savedRooms.clear()
                savedRooms.addAll(rooms)
            }
        }
    }

    Scaffold(
        topBar = { EnhancedTopAppBarSave("Phòng đã lưu") },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = { BottomNavBar(navController = navController) }
    ) { padding ->
        if (savedRooms.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Bạn chưa lưu phòng nào.")
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .padding(padding)
                    .padding(5.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(savedRooms) { room ->
                    RoomItem(
                        room = room,
                        isSaved = true,
                        onClick = {
                            navController.navigate(Routes.ROOM_DETAIL.replace("{roomId}", room.id))
                        },
                        onToggleSave = {
                            val savedRoom = SavedRoom(userId = userId!!, roomId = room.id)
                            roomViewModel.unsaveRoom(savedRoom) { success ->
                                if (success) {
                                    savedRooms.remove(room)
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Đã bỏ lưu phòng.")
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun EnhancedTopAppBarSave(title: String) {
    val gradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF4FC3F7), // Light Blue
            Color(0xFF2689F1)  // Darker Blue
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(gradient)
            .statusBarsPadding()
            .height(60.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = " Icon",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = title,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}