@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.nhatro24_7.ui.screen.customer.saveroom

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
        topBar = { TopAppBar(title = { Text("Phòng đã lưu") }) },
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
