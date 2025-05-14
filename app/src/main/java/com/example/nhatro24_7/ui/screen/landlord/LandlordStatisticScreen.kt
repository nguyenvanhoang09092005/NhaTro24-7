//package com.example.nhatro24_7.ui.screen.landlord
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowDropDown
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.example.nhatro24_7.viewmodel.StatisticViewModel
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun LandlordStatisticScreen(
//    landlordId: String,
//    viewModel: StatisticViewModel = viewModel()
//) {
//    // collectAsState cho các StateFlow
//    val statistic by viewModel.statistic.collectAsState(initial = null)
//    val isLoading by viewModel.isLoading.collectAsState(initial = false)
//    val revenueData by viewModel.revenueChart.collectAsState(initial = emptyList())
//    val bookingData by viewModel.bookingChart.collectAsState(initial = emptyList())
//    val cancellationData by viewModel.cancellationChart.collectAsState(initial = emptyList())
//
//    var timeRange by remember { mutableStateOf("Tuần") }
//    LaunchedEffect(timeRange) {
//        viewModel.fetchStatistic(landlordId, timeRange.lowercase())
//    }
//
//    Scaffold(
//        topBar = {
//            SmallTopAppBar(
//                title = { Text("Thống kê chủ trọ") }
//            )
//        }
//    ) { padding ->
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(padding)
//        ) {
//            if (isLoading) {
//                CircularProgressIndicator(Modifier.align(Alignment.Center))
//            } else {
//                statistic?.let { stat ->
//                    Column(
//                        Modifier
//                            .fillMaxSize()
//                            .verticalScroll(rememberScrollState())
//                            .padding(16.dp),
//                        verticalArrangement = Arrangement.spacedBy(24.dp)
//                    ) {
//                        // Dropdown
//                        TimeRangeDropdown(timeRange) { timeRange = it }
//
//                        // Biểu đồ doanh thu
//                        ChartSection("Doanh thu", revenueData)
//
//                        // Biểu đồ lượt đặt
//                        ChartSection("Lượt đặt", bookingData)
//
//                        // Biểu đồ huỷ phòng
//                        ChartSection("Huỷ phòng", cancellationData)
//
//                        Spacer(Modifier.height(16.dp))
//                        Text("Chi tiết thống kê", style = MaterialTheme.typography.titleLarge)
//                        StatisticCard("Tổng doanh thu", "${stat.revenue} VNĐ")
//                        StatisticCard("Tổng lượt đặt", "${stat.totalBookings}")
//                        StatisticCard("Lượt huỷ phòng", "${stat.totalCancellations}")
//                        StatisticCard("Lượt xem phòng", "${stat.totalViews}")
//                        StatisticCard("Đánh giá trung bình", "${stat.averageRating}/5")
//                        StatisticCard("Đặt phòng thành công", "${stat.successfulBookings}")
//                        StatisticCard("Doanh thu thành công", "${stat.successfulBookingRevenue} VNĐ")
//                        StatisticCard("Phòng đã trả", "${stat.totalCheckouts}")
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun SmallTopAppBar(title: @Composable () -> Unit) {
//    TODO("Not yet implemented")
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun TimeRangeDropdown(
//    selected: String,
//    onSelect: (String) -> Unit
//) {
//    val options = listOf("Tuần", "Tháng", "Năm")
//    var expanded by remember { mutableStateOf(false) }
//    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
//        TextField(
//            value = selected,
//            onValueChange = {},
//            readOnly = true,
//            label = { Text("Khoảng thời gian") },
//            trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = null) },
//            modifier = Modifier.fillMaxWidth().menuAnchor()
//        )
//        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
//            options.forEach { option ->
//                DropdownMenuItem(text = { Text(option) }, onClick = {
//                    onSelect(option)
//                    expanded = false
//                })
//            }
//        }
//    }
//}
//
//@Composable
//fun ChartSection(title: String, data: List<FloatEntry>) {
//    Column {
//        Text(title, style = MaterialTheme.typography.titleMedium)
//        Spacer(Modifier.height(8.dp))
//        CartesianChartHost(
//            chart = rememberCartesianChart(
//                layer = rememberLineCartesianLayer(),
//                startAxis = VerticalAxis.rememberStart(),
//                bottomAxis = HorizontalAxis.rememberBottom()
//            ),
//            chartModelProducer = remember {
//                com.patrykandpatrick.vico.core.chart.model.CartesianChartModelProducer().also { producer ->
//                    producer.runTransaction {
//                        lineSeries { series(data.map { it.y.toDouble() }) }
//                    }
//                }
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(200.dp)
//        )
//    }
//}
//
//@Composable
//fun StatisticCard(title: String, value: String) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 4.dp),
//        elevation = CardDefaults.cardElevation(2.dp)
//    ) {
//        Row(
//            Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text(title, style = MaterialTheme.typography.bodyLarge)
//            Text(value, style = MaterialTheme.typography.titleMedium)
//        }
//    }
//}
