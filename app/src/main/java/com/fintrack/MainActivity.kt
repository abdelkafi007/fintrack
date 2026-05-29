package com.fintrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.fintrack.presentation.ui.FinTrackApp
import com.fintrack.presentation.ui.theme.FinTrackTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main entry point for the FinTrack app.
 * Single-activity architecture — all screens are Compose destinations.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            FinTrackTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    FinTrackApp()
                }
            }
        }
    }
}
