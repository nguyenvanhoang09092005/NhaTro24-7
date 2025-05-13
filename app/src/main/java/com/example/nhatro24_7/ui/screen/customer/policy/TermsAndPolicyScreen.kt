package com.example.nhatro24_7.ui.screen.customer.policy

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlin.text.trimIndent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsAndPolicyScreen(
    navController: NavController,
    onBackClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Điều khoản và Chính sách") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        TermsAndPolicyContent(Modifier.padding(paddingValues))
    }
}

@Composable
fun TermsAndPolicyContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        SectionTitle("1. Giới thiệu")
        SectionText(
            """
            Chào mừng bạn đến với ứng dụng Nhà Trọ 24_7. Việc bạn truy cập và sử dụng ứng dụng đồng nghĩa với việc bạn đồng ý với các điều khoản và chính sách được nêu trong tài liệu này. Vui lòng đọc kỹ trước khi tiếp tục sử dụng dịch vụ.
            """.trimIndent()
        )

        Spacer(modifier = Modifier.height(24.dp))

        SectionTitle("2. Điều khoản sử dụng")
        SectionText(
            """
            Khi sử dụng Nhà Trọ 24_7, bạn cần cam kết:
            - Cung cấp thông tin cá nhân trung thực và chính xác khi đăng ký tài khoản.
            - Không sử dụng ứng dụng để đăng tải hoặc chia sẻ nội dung vi phạm pháp luật, thuần phong mỹ tục, hoặc gây ảnh hưởng đến người dùng khác.
            - Không thu thập, sử dụng trái phép thông tin cá nhân của người khác.
            - Không thực hiện các hành vi can thiệp, phá hoại hệ thống hoặc làm gián đoạn dịch vụ.
            - Chịu trách nhiệm hoàn toàn với nội dung bạn đăng tải, bao gồm thông tin nhà trọ, tin nhắn, hình ảnh...
            """.trimIndent()
        )

        Spacer(modifier = Modifier.height(24.dp))

        SectionTitle("3. Quyền và trách nhiệm của người dùng")
        SectionText(
            """
            - Người dùng có quyền truy cập, chỉnh sửa hoặc yêu cầu xóa dữ liệu cá nhân trong tài khoản của mình.
            - Người dùng có trách nhiệm đảm bảo tính bảo mật của tài khoản và mật khẩu, cũng như các hành động thực hiện từ tài khoản của mình.
            - Trong trường hợp phát hiện hành vi vi phạm hoặc tài khoản bị xâm nhập, người dùng cần thông báo cho chúng tôi sớm nhất để được hỗ trợ.
            """.trimIndent()
        )

        Spacer(modifier = Modifier.height(24.dp))

        SectionTitle("4. Chính sách bảo mật")
        SectionText(
            """
            Nhà Trọ 24_7 cam kết bảo vệ thông tin cá nhân của bạn:
            - Mọi thông tin được thu thập chỉ nhằm mục đích cung cấp dịch vụ và cải thiện trải nghiệm người dùng.
            - Chúng tôi không chia sẻ, mua bán hay tiết lộ thông tin của bạn cho bên thứ ba mà không có sự đồng ý trước đó.
            - Các biện pháp bảo mật kỹ thuật và quản lý được áp dụng để đảm bảo an toàn cho dữ liệu người dùng.
            - Bạn có thể liên hệ với chúng tôi để yêu cầu truy cập, chỉnh sửa hoặc xóa dữ liệu cá nhân bất kỳ lúc nào.
            """.trimIndent()
        )

        Spacer(modifier = Modifier.height(24.dp))

        SectionTitle("5. Quyền và trách nhiệm của chúng tôi")
        SectionText(
            """
            - Chúng tôi có quyền kiểm tra, gỡ bỏ hoặc đình chỉ các nội dung vi phạm quy định trong ứng dụng.
            - Có quyền thay đổi hoặc tạm ngừng dịch vụ bất kỳ lúc nào để nâng cấp, bảo trì hoặc xử lý sự cố.
            - Cam kết minh bạch trong việc xử lý thông tin và cung cấp dịch vụ đúng như mô tả.
            - Không chịu trách nhiệm đối với các hành vi, tranh chấp giữa người dùng với nhau nếu không xuất phát từ lỗi của hệ thống.
            """.trimIndent()
        )

        Spacer(modifier = Modifier.height(24.dp))

        SectionTitle("6. Thay đổi điều khoản và chính sách")
        SectionText(
            """
            Chúng tôi có thể cập nhật nội dung điều khoản và chính sách bất kỳ lúc nào nhằm phù hợp với thay đổi pháp lý hoặc cải tiến dịch vụ. Các thay đổi sẽ được thông báo trên ứng dụng. Việc tiếp tục sử dụng dịch vụ sau khi điều khoản được cập nhật đồng nghĩa với việc bạn chấp nhận các thay đổi đó.
            """.trimIndent()
        )

        Spacer(modifier = Modifier.height(24.dp))

        SectionTitle("7. Liên hệ")
        SectionText(
            """
            Nếu bạn có bất kỳ câu hỏi, khiếu nại hoặc yêu cầu nào liên quan đến điều khoản và chính sách, vui lòng liên hệ với chúng tôi qua:
            - Email: hotro@nhatro247.vn
            - Hotline: 1900 1234 567
            - Địa chỉ: Đại học Việt Hàn - Đà Nẵng
            """.trimIndent()
        )
    }
}


@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun SectionText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        lineHeight = 22.sp
    )
}
