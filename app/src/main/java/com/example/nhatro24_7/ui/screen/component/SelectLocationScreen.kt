@file:Suppress("MissingPermission")

package com.example.nhatro24_7.ui.screen.component

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.mapbox.api.geocoding.v5.GeocodingCriteria
import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.mapbox.api.geocoding.v5.models.GeocodingResponse
import com.mapbox.geojson.Point
import com.mapbox.maps.*
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectLocationScreen(navController: NavController) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val mapStyles = mapOf(
        "Streets" to Style.MAPBOX_STREETS,
        "Satellite" to Style.SATELLITE,
        "Outdoors" to Style.OUTDOORS
    )
    var selectedStyle by remember { mutableStateOf("Streets") }

    val mapInitOptions = remember {
        MapInitOptions(
            context = context,
            resourceOptions = ResourceOptions.Builder()
                .accessToken("pk.eyJ1IjoiaG9hbmdrZTM0MDJmIiwiYSI6ImNtYTBkYzAyZzIyeWsyam13dTFjOWthMHUifQ.lXQX_tarAl9h2Vqhx5Gg5Q") // ❗️Nhớ thay bằng token thật
                .build()
        )
    }

    val mapView = remember { MapView(context, mapInitOptions) }
    val selectedPoint = remember { mutableStateOf<Point?>(null) }
    val selectedAddress = remember { mutableStateOf<String?>(null) }
    val pointAnnotationManager = remember { mutableStateOf<PointAnnotationManager?>(null) }

    fun moveToCurrentLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val point = Point.fromLngLat(location.longitude, location.latitude)
                    mapView.getMapboxMap().setCamera(
                        CameraOptions.Builder()
                            .center(point)
                            .zoom(14.0)
                            .build()
                    )
                } else {
                    Toast.makeText(context, "Không tìm được vị trí hiện tại", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Lỗi lấy vị trí: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }


    fun reverseGeocode(point: Point) {
        val geocoding = MapboxGeocoding.builder()
            .accessToken("pk.eyJ1IjoiaG9hbmdrZTM0MDJmIiwiYSI6ImNtYTBkYzAyZzIyeWsyam13dTFjOWthMHUifQ.lXQX_tarAl9h2Vqhx5Gg5Q")
            .query(point)
            .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS)
            .build()

        geocoding.enqueueCall(object : Callback<GeocodingResponse> {
            override fun onResponse(call: Call<GeocodingResponse>, response: Response<GeocodingResponse>) {
                if (response.isSuccessful) {
                    selectedAddress.value = response.body()?.features()?.firstOrNull()?.placeName()
                } else {
                    selectedAddress.value = null
                    Toast.makeText(context, "Không tìm thấy địa chỉ", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GeocodingResponse>, t: Throwable) {
                selectedAddress.value = null
                Toast.makeText(context, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
            }
        })
    }

    LaunchedEffect(Unit) {
        moveToCurrentLocation()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { mapView }, modifier = Modifier.fillMaxSize()) {
            mapView.getMapboxMap().loadStyleUri(mapStyles[selectedStyle]!!) {
                val annotationApi = mapView.annotations
                pointAnnotationManager.value = annotationApi.createPointAnnotationManager()

                mapView.getMapboxMap().addOnMapClickListener { clicked ->
                    val clickedPoint = Point.fromLngLat(clicked.longitude(), clicked.latitude())
                    pointAnnotationManager.value?.deleteAll()
                    pointAnnotationManager.value?.create(
                        PointAnnotationOptions()
                            .withPoint(clickedPoint)
                            .withIconImage("marker-15")
                    )

                    selectedPoint.value = clickedPoint
                    reverseGeocode(clickedPoint)
                    true
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp)
        ) {
            var expanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    value = selectedStyle,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Chọn kiểu bản đồ") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    mapStyles.forEach { (name, styleUri) ->
                        DropdownMenuItem(
                            text = { Text(name) },
                            onClick = {
                                selectedStyle = name
                                mapView.getMapboxMap().loadStyleUri(styleUri)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = { moveToCurrentLocation() }) {
                Icon(Icons.Default.LocationOn, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("Vị trí của tôi")
            }

            if (selectedAddress.value != null) {
                Text(
                    text = "📍 Địa chỉ: ${selectedAddress.value}",
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Text(
                    "Hãy nhấn vào bản đồ để chọn vị trí.",
                    modifier = Modifier.padding(top = 12.dp),
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Button(
            onClick = {
                val point = selectedPoint.value
                val address = selectedAddress.value
                if (point != null && address != null) {
                    navController.previousBackStackEntry?.savedStateHandle?.set("selected_address", address)
                    navController.previousBackStackEntry?.savedStateHandle?.set("selected_coordinates", listOf(point.latitude(), point.longitude()))
                    navController.popBackStack()
                } else {
                    Toast.makeText(context, "Vui lòng chọn vị trí trên bản đồ", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = null)
            Spacer(Modifier.width(6.dp))
            Text("Xác nhận vị trí này")
        }
    }
}
