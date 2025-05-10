//package com.example.nhatro24_7.di
//
//import android.content.Context
//import com.example.nhatro24_7.data.repository.NotificationRepository
//import com.example.nhatro24_7.util.NotificationHelper
//import com.example.nhatro24_7.viewmodel.NotificationViewModel
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.qualifiers.ApplicationContext
//import dagger.hilt.components.SingletonComponent
//import javax.inject.Singleton
//
//@Module
//@InstallIn(SingletonComponent::class)
//object AppModule {
//
//    @Provides
//    @Singleton
//    fun provideNotificationHelper(@ApplicationContext context: Context): NotificationHelper {
//        return NotificationHelper(context)
//    }
//
//    @Provides
//    @Singleton
//    fun provideNotificationViewModel(
//        repository: NotificationRepository,
//        helper: NotificationHelper
//    ): NotificationViewModel {
//        return NotificationViewModel(repository, helper)
//    }
//}
