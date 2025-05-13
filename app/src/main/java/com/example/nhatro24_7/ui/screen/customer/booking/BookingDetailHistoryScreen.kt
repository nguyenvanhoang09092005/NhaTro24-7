//package com.example.nhatro24_7.ui.screen.customer.booking
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.runtime.livedata.observeAsState
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavController
//import com.example.nhatro24_7.data.model.BookingRequest
//import com.example.nhatro24_7.data.model.Room
//import com.example.nhatro24_7.viewmodel.RoomViewModel
//
//@Composable
//fun BookingDetailHistoryScreen(
//    navController: NavController,
//    roomId: String,
//    bookingId: String,
//    roomViewModel: RoomViewModel = viewModel()
//) {
//    var room by remember { mutableStateOf<Room?>(null) }
//    var booking by remember { mutableStateOf<BookingRequest?>(null) }
//
//
//
//    LaunchedEffect(Unit) {
//        roomViewModel.getRoomById(roomId) {
//            room = it
//        }
//        roomViewModel.getBookingRequestById(bookingId) {
//            booking = it
//        }
//    }
//
//    if (room == null || booking == null) {
//        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//            CircularProgressIndicator()
//        }
//        return
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//        Text("Chi tiết phòng", style = MaterialTheme.typography.headlineSmall)
//        Spacer(modifier = Modifier.height(8.dp))
//        Text("Tên phòng: ${room!!.title}")
//        Text("Địa chỉ: ${room!!.location}")
//        Text("Giá: ${room!!.price} VNĐ/tháng")
//        Text("Trạng thái đặt phòng: ${booking!!.status}")
//        Spacer(modifier = Modifier.height(24.dp))
//
//        Button(
//            onClick = {
//                roomViewModel.cancelBooking(bookingId)
//            },
//            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text("Huỷ phòng", color = Color.White)
//        }
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        Button(
//            onClick = {
//                roomViewModel.checkoutBooking(bookingId)
//            },
//            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text("Trả phòng", color = Color.White)
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        cancelStatus?.let {
//            Text(text = it, color = Color.Red, fontWeight = FontWeight.Bold)
//        }
//
//        checkoutStatus?.let {
//            Text(text = it, color = Color.Green, fontWeight = FontWeight.Bold)
//        }
//    }
//}
