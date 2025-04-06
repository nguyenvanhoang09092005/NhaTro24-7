package com.example.nhatro24_7.util

import android.content.ContentResolver
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okio.IOException
import org.json.JSONObject
import java.io.InputStream
import java.security.MessageDigest
import java.util.Base64

suspend fun uploadImageToCloudinary(
    imageUri: Uri,
    contentResolver: ContentResolver,
    cloudName: String,
    uploadPreset: String,
    onSuccess: (imageUrl: String, publicId: String) -> Unit,
    onError: (String) -> Unit
) {
    withContext(Dispatchers.IO) {
        try {
            val inputStream: InputStream? = contentResolver.openInputStream(imageUri)
            val imageBytes = inputStream?.readBytes() ?: throw IOException("Không thể đọc ảnh")

            val client = OkHttpClient()

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file",
                    "upload.jpg",
                    RequestBody.create("image/*".toMediaTypeOrNull(), imageBytes)
                )
                .addFormDataPart("upload_preset", uploadPreset)
                .build()

            val request = Request.Builder()
                .url("https://api.cloudinary.com/v1_1/$cloudName/image/upload")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && responseBody != null) {
                val jsonObject = JSONObject(responseBody)
                val imageUrl = jsonObject.getString("secure_url")
                val publicId = jsonObject.getString("public_id")

                withContext(Dispatchers.Main) {
                    onSuccess(imageUrl, publicId)
                }
            } else {
                withContext(Dispatchers.Main) {
                    onError("Upload thất bại: ${response.code}")
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onError("Lỗi: ${e.localizedMessage}")
            }
        }
    }
}

suspend fun deleteImageFromCloudinary(
    publicId: String,
    cloudName: String,
    apiKey: String,
    apiSecret: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    withContext(Dispatchers.IO) {
        try {
            val timestamp = (System.currentTimeMillis() / 1000).toString()
            val signatureRaw = "public_id=$publicId&timestamp=$timestamp$apiSecret"
            val signature = MessageDigest.getInstance("SHA-1")
                .digest(signatureRaw.toByteArray())
                .joinToString("") { "%02x".format(it) }

            val requestBody = FormBody.Builder()
                .add("public_id", publicId)
                .add("791292363868727", apiKey)
                .add("timestamp", timestamp)
                .add("signature", signature)
                .build()

            val request = Request.Builder()
                .url("https://api.cloudinary.com/v1_1/$cloudName/image/destroy")
                .post(requestBody)
                .build()

            val response = OkHttpClient().newCall(request).execute()
            if (response.isSuccessful) {
                withContext(Dispatchers.Main) { onSuccess() }
            } else {
                withContext(Dispatchers.Main) { onError("Xoá thất bại: ${response.code}") }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onError("Lỗi khi xóa ảnh: ${e.localizedMessage}")
            }
        }
    }
}