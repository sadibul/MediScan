package com.mediscan.app

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * MediScan Application class.
 * @HiltAndroidApp triggers Hilt's code generation for dependency injection.
 * This serves as the application-level dependency container.
 *
 * Implements ImageLoaderFactory to provide a custom Coil ImageLoader
 * with a proper timeout for Firebase Storage image downloads.
 */
@HiltAndroidApp
class MediScanApp : Application(), ImageLoaderFactory {
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .okHttpClient {
                OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .build()
            }
            .crossfade(true)
            .build()
    }
}
