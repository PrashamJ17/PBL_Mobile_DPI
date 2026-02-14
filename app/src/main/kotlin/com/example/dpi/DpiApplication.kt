package com.example.dpi

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Main application class for Mobile DPI Hub
 * Initializes Hilt dependency injection
 */
@HiltAndroidApp
class DpiApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // App-wide initialization here
    }
}
