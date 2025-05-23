package com.example.nhatro24_7.ui.screen.landlord.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.nhatro24_7.navigation.Routes
import com.example.nhatro24_7.ui.screen.customer.profile.SettingToggleItem
import com.example.nhatro24_7.ui.screen.landlord.component.BottomNavBar
import com.example.nhatro24_7.ui.screen.landlord.profile.ExpandableSection
import com.example.nhatro24_7.ui.screen.landlord.profile.ProfileOption
import com.example.nhatro24_7.viewmodel.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import okhttp3.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileLandlordScreen(
    navController: NavController,
    viewModel: AuthViewModel,
    isDarkMode: Boolean,
    selectedLanguage: String,
    onThemeToggle: (Boolean) -> Unit,
    onLanguageChange: (String) -> Unit
) {
    val context = LocalContext.current
    var avatarUri by remember { mutableStateOf<Uri?>(null) }

    var isRoomExpanded by remember { mutableStateOf(false) }
    var isAccountExpanded by remember { mutableStateOf(false) }
    var isPaymentExpanded by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("Người dùng") }
    var avatarUrl by remember { mutableStateOf("") }
    var isLanguageDropdownExpanded by remember { mutableStateOf(false) }
    var isNotificationEnabled by remember { mutableStateOf(true) }
    val userRole by viewModel.userRole.collectAsState()

    LaunchedEffect(Unit) {
        val userId = viewModel.getCurrentUserId()
        if (userId != null) {
            val doc = FirebaseFirestore.getInstance().collection("users").document(userId).get().await()
            username = doc.getString("username") ?: "Người dùng"
            avatarUrl = doc.getString("avatarUrl") ?: ""
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadUserRole()
    }



    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        avatarUri = uri
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController = navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            )
            {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val imageToDisplay = avatarUri?.toString() ?: if (avatarUrl.isNotBlank()) avatarUrl else null

                    if (imageToDisplay != null) {
                        Image(
                            painter = rememberAsyncImagePainter(imageToDisplay),
                            contentDescription = "Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(110.dp)
                                .background(Color.White, CircleShape),
                            tint = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(username, fontSize = 18.sp, color = Color.White)
                }
            }

            Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
                Spacer(modifier = Modifier.height(22.dp))

                ProfileOption(Icons.Default.Person, "Trang cá nhân") {
                    navController.navigate("landlord_profile_detail")
                }

                ExpandableSection("Quản lý phòng trọ", Icons.Default.Home, isRoomExpanded, { isRoomExpanded = !isRoomExpanded },
                    listOf(
                        Triple("Danh sách phòng", Icons.Default.MeetingRoom, "room_list"),
                        Triple("Yêu cầu thuê phòng", Icons.Default.Inbox, "landlord_booking_requests"),
//                        Triple("Đánh giá của khách", Icons.Default.ThumbUp, "guest_reviews")
                    ), navController)


                ExpandableSection("Tài khoản", Icons.Default.Settings, isAccountExpanded, { isAccountExpanded = !isAccountExpanded },
                    listOf(
                        Triple("Đổi mật khẩu", Icons.Default.Lock, "change_password"),
                        Triple("Xác minh Email/SĐT", Icons.Default.VerifiedUser, "verify_account"),
                        Triple("Liên kết tài khoản", Icons.Default.Link, "link_accounts"),
                        Triple("Xóa tài khoản", Icons.Default.Delete, "delete_account")
                    ), navController)

                ExpandableSection("Quản lý thanh toán", Icons.Default.AccountBalanceWallet, isPaymentExpanded, { isPaymentExpanded = !isPaymentExpanded },
                    listOf(
                        Triple("Thêm tài khoản ngân hàng", Icons.Default.AddCard, "add_bank_account"),
                        Triple("Danh sách tài khoản ngân hàng", Icons.Default.AccountBalance, "list_bank_accounts")
                    ), navController)

                com.example.nhatro24_7.ui.screen.customer.profile.ProfileOption(
                    Icons.Default.Info,
                    "Điều khoản và chính sách"
                ) {        navController.navigate("termAndPolicy") }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                SettingToggleItem(Icons.Default.DarkMode, "Chế độ tối", isDarkMode) {
                    onThemeToggle(it)
                }

                SettingRoleSwitchItem(
                    icon = Icons.Default.SwitchAccount,
                    title = "Chuyển vai trò",
                    currentRole = userRole,
                    onRoleToggle = {
                        viewModel.toggleUserRole() // Gọi hàm toggle vai trò trong ViewModel
                    }
                )
                SettingToggleItem(Icons.Default.Notifications, "Nhận thông báo", isNotificationEnabled) {
                    isNotificationEnabled = it
                    // TODO: Save to DataStore if needed
                }

                com.example.nhatro24_7.ui.screen.customer.profile.ProfileOption(
                    Icons.Default.Logout,
                    "Đăng xuất"
                ) {
                    viewModel.signOut {
                        navController.navigate("splash") {
                            popUpTo(0) { inclusive = true }
                        }
                    }

                }

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .background(MaterialTheme.colorScheme.surface)
                )
            }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }


@Composable
fun ProfileOption(
    icon: ImageVector,
    title: String,
    trailingIcon: ImageVector? = Icons.Default.KeyboardArrowRight,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, fontSize = 16.sp, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurface)
        trailingIcon?.let {
            Icon(it, contentDescription = "Next", tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}


@Composable
fun ExpandableSection(
    title: String,
    icon: ImageVector,
    expanded: Boolean,
    onToggle: () -> Unit,
    items: List<Triple<String, ImageVector, String>>,
    navController: NavController
) {
    val trailingIcon = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore

    ProfileOption(icon = icon, title = title, trailingIcon = trailingIcon, onClick = onToggle)

    AnimatedVisibility(visible = expanded) {
        Column(modifier = Modifier.padding(start = 32.dp)) {
            items.forEach { (label, itemIcon, route) ->
                ProfileOption(icon = itemIcon, title = label) {
                    navController.navigate(route)
                }
            }
        }
    }
}

@Composable
fun SettingToggleItem(
    icon: ImageVector,
    title: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            title,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurface
        )
        Switch(checked = checked, onCheckedChange = onToggle, modifier = Modifier.scale(0.7f))
    }
}


@Composable
fun SettingRoleSwitchItem(
    icon: ImageVector,
    title: String,
    currentRole: String,
    onRoleToggle: () -> Unit
) {
    val roleTitle = if (currentRole == "customer") "Người thuê" else "Chủ phòng"
    val roleIcon = if (currentRole == "customer") Icons.Default.Person else Icons.Default.Home

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = roleIcon,
            contentDescription = "Vai trò",
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "$title: $roleTitle",
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
            fontSize = 16.sp
        )
        Switch(
            checked = currentRole == "landlord",
            onCheckedChange = { onRoleToggle() },
            modifier = Modifier.scale(0.7f)
        )
    }
}