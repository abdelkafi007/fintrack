package com.fintrack

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * FinTrack Application class.
 * Annotated with @HiltAndroidApp to trigger Hilt code generation
 * and serve as the application-level dependency container.
 */
@HiltAndroidApp
class FinTrackApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Application-level initialization
        // - Notification channels are created in NotificationHelper
        // - WorkManager tasks are scheduled from SettingsViewModel
    }
}
