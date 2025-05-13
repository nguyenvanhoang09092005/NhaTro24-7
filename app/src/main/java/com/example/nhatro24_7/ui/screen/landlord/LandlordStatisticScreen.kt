package com.example.nhatro24_7.ui.screen.landlord

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nhatro24_7.viewmodel.StatisticViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandlordStatisticScreen(
    landlordId: String,
    viewModel: StatisticViewModel = viewModel()
) {
    val statistic by viewModel.statistic.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchStatistic(landlordId)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Thống kê") })
        }
    ) { paddingValues ->
        statistic?.let {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                StatisticCard("Doanh thu", "${it.revenue} VNĐ")
                StatisticCard("Lượt đặt phòng", "${it.totalBookings}")
                StatisticCard("Lượt huỷ phòng", "${it.totalCancellations}")
                StatisticCard("Lượt xem phòng", "${it.totalViews}")
                StatisticCard("Đánh giá trung bình", "${it.averageRating}/5")
            }
        } ?: run {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun StatisticCard(title: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineMedium)
        }
    }
}
