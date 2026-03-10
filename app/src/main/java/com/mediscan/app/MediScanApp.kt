package com.mediscan.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * MediScan Application class.
 * @HiltAndroidApp triggers Hilt's code generation for dependency injection.
 * This serves as the application-level dependency container.
 */
@HiltAndroidApp
class MediScanApp : Application()
