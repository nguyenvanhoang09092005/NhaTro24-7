plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.example.nhatro24_7"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.nhatro24_7"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }

//    packagingOptions {
//        resources.excludes.add("META-INF/*")
//    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation("androidx.navigation:navigation-compose:2.7.5")
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    // Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:32.7.4"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.android.gms:play-services-auth:21.3.0")
    implementation("com.github.scribejava:scribejava-core:8.3.0")
    implementation("com.github.scribejava:scribejava-apis:8.3.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")

    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("androidx.compose.foundation:foundation-layout:1.6.6")
    implementation("com.google.accompanist:accompanist-permissions:0.34.0")
    implementation("com.google.accompanist:accompanist-flowlayout:0.35.0-alpha")
    implementation ("com.google.accompanist:accompanist-flowlayout:0.34.0")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.48")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
    kapt("com.google.dagger:hilt-compiler:2.48")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    //qr]
    implementation("com.google.zxing:core:3.5.1")
    implementation("androidx.compose.ui:ui-graphics:1.5.0")

    // Material Icons (đã kiểm tra version)
    implementation("androidx.compose.material:material-icons-extended:1.6.4")

    implementation("com.squareup.okhttp3:okhttp:4.10.0")


    implementation("androidx.datastore:datastore-preferences:1.0.0")

    implementation ("androidx.compose.ui:ui:1.5.4")
    implementation ("androidx.compose.material:material:1.5.4")
    implementation ("androidx.navigation:navigation-compose:2.7.5")
    implementation ("com.google.firebase:firebase-messaging-ktx:23.4.0")
    // Compose
    implementation("androidx.compose.runtime:runtime-livedata:1.6.4")
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Coil for image loading
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation(libs.play.services.cast.tv)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    // Thư viện vẽ biểu đồ cho Jetpack Compose
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation ("org.apache.poi:poi:5.2.3")
    // Chart support for Jetpack Compose
//    implementation("com.patrykandpatrick.vico:compose:1.13.0")
//    implementation("com.patrykandpatrick.vico:core:1.13.0")
//    implementation ("com.patrykandpatrick:vico-compose:1.0.0-alpha04")

    // map
    implementation ("com.mapbox.maps:android:10.15.1")
    implementation ("com.mapbox.mapboxsdk:mapbox-sdk-services:5.8.0")

    implementation("com.cloudinary:cloudinary-android:2.3.1")

    //thông báo
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // Xử lý lỗi Duplicate class - Exclude firebase-common nếu cần
    implementation("com.google.firebase:firebase-firestore-ktx") {
        exclude(group = "com.google.firebase", module = "firebase-common")
    }
}

configurations.all {
    resolutionStrategy {
        force("com.google.code.findbugs:jsr305:3.0.2")
        force("androidx.lifecycle:lifecycle-runtime-ktx:2.8.3")
        force("androidx.lifecycle:lifecycle-runtime:2.8.3")
        force("androidx.lifecycle:lifecycle-runtime-android:2.8.3")
        force("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
        force("com.squareup.okhttp3:okhttp:4.9.3")
    }
}
