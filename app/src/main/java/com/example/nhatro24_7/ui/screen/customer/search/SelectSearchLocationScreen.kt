@file:Suppress("MissingPermission")

package com.example.nhatro24_7.ui.screen.customer.search

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import com.example.nhatro24_7.R
import com.example.nhatro24_7.data.model.Room
import com.mapbox.api.geocoding.v5.GeocodingCriteria
import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.mapbox.api.geocoding.v5.models.GeocodingResponse
import com.mapbox.geojson.Point
import com.mapbox.maps.*
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.*
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectSearchLocationScreen(navController: NavController) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val selectedPoint = remember { mutableStateOf<Point?>(null) }
    val selectedAddress = remember { mutableStateOf<String?>(null) }
    val pointAnnotationManager = remember { mutableStateOf<PointAnnotationManager?>(null) }

    val mapView = remember {
        MapView(
            context, MapInitOptions(
                context = context,
                resourceOptions = ResourceOptions.Builder()
                    .accessToken("pk.eyJ1IjoiaG9hbmdrZTM0MDJmIiwiYSI6ImNtYTBkYzAyZzIyeWsyam13dTFjOWthMHUifQ.lXQX_tarAl9h2Vqhx5Gg5Q") // Thay bằng token thật
                    .build()
            )
        )
    }

    fun moveToCurrentLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                location?.let {
                    val point = Point.fromLngLat(it.longitude, it.latitude)
                    mapView.getMapboxMap().setCamera(
                        CameraOptions.Builder()
                            .center(point)
                            .zoom(14.0)
                            .build()
                    )
                    pointAnnotationManager.value?.deleteAll()
                    pointAnnotationManager.value?.create(
                        PointAnnotationOptions().withPoint(point).withIconImage("marker-icon")
                    )
                } ?: Toast.makeText(context, "Không tìm được vị trí hiện tại", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Lỗi lấy vị trí: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    fun reverseGeocode(point: Point) {
        MapboxGeocoding.builder()
            .accessToken("pk.eyJ1IjoiaG9hbmdrZTM0MDJmIiwiYSI6ImNtYTBkYzAyZzIyeWsyam13dTFjOWthMHUifQ.lXQX_tarAl9h2Vqhx5Gg5Q") // Thay bằng token thật
            .query(point)
            .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS)
            .build()
            .enqueueCall(object : Callback<GeocodingResponse> {
                override fun onResponse(call: Call<GeocodingResponse>, response: Response<GeocodingResponse>) {
                    selectedAddress.value = response.body()?.features()?.firstOrNull()?.placeName()
                        ?: "Không rõ địa chỉ"
                }

                override fun onFailure(call: Call<GeocodingResponse>, t: Throwable) {
                    selectedAddress.value = null
                    Toast.makeText(context, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
                }
            })
    }

    LaunchedEffect(Unit) {
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) { style ->
            pointAnnotationManager.value = mapView.annotations.createPointAnnotationManager()
            mapView.bitmapFromDrawableRes(context, R.drawable.map)?.let {
                val resized = resizeBitmap(it, 0.05f)
                style.addImage("marker-icon", resized)
            }

            mapView.getMapboxMap().addOnMapClickListener { latLng ->
                val point = Point.fromLngLat(latLng.longitude(), latLng.latitude())
                pointAnnotationManager.value?.deleteAll()
                pointAnnotationManager.value?.create(
                    PointAnnotationOptions().withPoint(point).withIconImage("marker-icon")
                )
                selectedPoint.value = point
                reverseGeocode(point)
                true
            }
        }
        moveToCurrentLocation()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { mapView }, modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp)
        ) {
            Button(onClick = { moveToCurrentLocation() }) {
                Icon(Icons.Default.LocationOn, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("Vị trí của tôi")
            }

            selectedAddress.value?.let {
                Text(
                    text = "📍 $it",
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Button(
            onClick = {
                val point = selectedPoint.value
                val address = selectedAddress.value
                if (point != null && address != null) {
                    fetchRoomsNearby(
                        latitude = point.latitude(),
                        longitude = point.longitude(),
                        onResult = { nearbyRooms ->
                            navController.previousBackStackEntry?.savedStateHandle?.apply {
                                set("selected_coordinates", listOf(point.latitude(), point.longitude()))
                                set("selected_address", address)
                                set("filtered_rooms", nearbyRooms)
                            }
                            navController.popBackStack()
                        },
                        onError = {
                            Toast.makeText(context, "Lỗi lấy danh sách phòng: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                    )
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
            Text("Tìm phòng quanh đây")
        }

    }
}

// Tiện ích
fun MapView.bitmapFromDrawableRes(context: android.content.Context, resId: Int): Bitmap? {
    return BitmapFactory.decodeResource(context.resources, resId)
}

fun resizeBitmap(bitmap: Bitmap, scale: Float): Bitmap {
    val width = (bitmap.width * scale).toInt()
    val height = (bitmap.height * scale).toInt()
    return Bitmap.createScaledBitmap(bitmap, width, height, true)
}

fun fetchRoomsNearby(
    latitude: Double,
    longitude: Double,
    onResult: (List<Room>) -> Unit,
    onError: (Exception) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    db.collection("rooms")
        .get()
        .addOnSuccessListener { result ->
            val nearbyRooms = result.documents.mapNotNull { it.toObject(Room::class.java) }
                .filter {
                    // Nếu toạ độ phòng hợp lệ và cách vị trí chọn <= 5km
                    it.latitude != 0.0 && it.longitude != 0.0 &&
                            calculateDistance(latitude, longitude, it.latitude, it.longitude) <= 5.0
                }
            onResult(nearbyRooms)
        }
        .addOnFailureListener { exception ->
            onError(exception)
        }
}