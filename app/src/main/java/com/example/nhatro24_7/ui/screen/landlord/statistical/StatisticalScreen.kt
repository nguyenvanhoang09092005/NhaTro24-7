package com.example.nhatro24_7.ui.screen.landlord.statistical

import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nhatro24_7.ui.screen.landlord.component.BottomNavBar
import com.example.nhatro24_7.viewmodel.StatisticViewModel
import com.google.firebase.auth.FirebaseAuth
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticalScreen(
    navController: NavController,
    viewModel: StatisticViewModel = viewModel()
) {
    val statistic by viewModel.statistic.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val landlordId = FirebaseAuth.getInstance().currentUser?.uid ?: return

    LaunchedEffect(Unit) {
        viewModel.fetchStatistic(landlordId)
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF4FC3F7), Color(0xFF1976D2))
                        )
                    ),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 20.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.3f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = "Thống kê",
                            tint = Color.White,
                            modifier = Modifier.padding(6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Thống kê",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        },
        bottomBar = { BottomNavBar(navController = navController) }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                SummarySection(statistic = statistic)
                Spacer(modifier = Modifier.height(24.dp))
                RevenueBarChart(revenueByMonth = statistic.revenueByMonth)

                Spacer(modifier = Modifier.height(24.dp))
//                Text(
//                    text = "Biểu đồ đường: Đặt, Trả, Hủy, Thanh toán theo tháng",
//                    fontSize = 20.sp,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier.padding(bottom = 8.dp)
//                )
//                BookingLineChart(
//                    bookingsByMonth = statistic.bookingsByMonth,
//                    checkoutsByMonth = statistic.checkoutsByMonth,
//                    cancellationsByMonth = statistic.cancellationsByMonth,
//                    paidRoomsByMonth = statistic.paidRoomsByMonth
//                )
            }
        }
    }
}

@Composable
fun BookingLineChart(
    bookingsByMonth: Map<String, Int>,
    checkoutsByMonth: Map<String, Int>,
    cancellationsByMonth: Map<String, Int>,
    paidRoomsByMonth: Map<String, Int>
) {
    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    600
                )
            }
        },
        update = { chart ->
            val months = (1..12).map { it.toString().padStart(2, '0') }

            fun createEntry(data: Map<String, Int>): List<Entry> =
                months.mapIndexed { index, month ->
                    Entry((index + 1).toFloat(), data[month]?.toFloat() ?: 0f)
                }

            val dataSets = listOf(
                LineDataSet(createEntry(bookingsByMonth), "Đặt phòng").apply {
                    color = android.graphics.Color.BLUE
                    valueTextColor = android.graphics.Color.BLACK
                    lineWidth = 2f
                    circleRadius = 4f
                },
                LineDataSet(createEntry(checkoutsByMonth), "Trả phòng").apply {
                    color = android.graphics.Color.GREEN
                    valueTextColor = android.graphics.Color.BLACK
                    lineWidth = 2f
                    circleRadius = 4f
                },
                LineDataSet(createEntry(cancellationsByMonth), "Hủy phòng").apply {
                    color = android.graphics.Color.RED
                    valueTextColor = android.graphics.Color.BLACK
                    lineWidth = 2f
                    circleRadius = 4f
                },
                LineDataSet(createEntry(paidRoomsByMonth), "Thanh toán").apply {
                    color = android.graphics.Color.MAGENTA
                    valueTextColor = android.graphics.Color.BLACK
                    lineWidth = 2f
                    circleRadius = 4f
                }
            )

            chart.data = LineData(dataSets)

            chart.legend.isEnabled = true
            chart.legend.isWordWrapEnabled = true

            chart.setTouchEnabled(true)
            chart.setPinchZoom(true)
            chart.setScaleEnabled(true)
            chart.setDrawGridBackground(false)

            val yAxisLeft = chart.axisLeft
            val maxValue = listOf(bookingsByMonth, checkoutsByMonth, cancellationsByMonth, paidRoomsByMonth)
                .flatMap { it.values }
                .maxOrNull()?.toFloat() ?: 10f
            yAxisLeft.axisMinimum = 0f
            yAxisLeft.axisMaximum = maxValue + 2f
            yAxisLeft.granularity = 1f
            yAxisLeft.setLabelCount((maxValue.toInt() + 2).coerceAtLeast(5), true)
            yAxisLeft.setDrawGridLines(true)
            yAxisLeft.setDrawLabels(true)

            chart.axisRight.isEnabled = false

            val xAxis = chart.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1f
            xAxis.setLabelCount(12, true)
            xAxis.axisMinimum = 1f
            xAxis.axisMaximum = 12f
            xAxis.valueFormatter = IndexAxisValueFormatter(months)

            chart.description.isEnabled = false
            chart.invalidate()
        }
    )
}
