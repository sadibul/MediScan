package com.mediscan.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.mediscan.app.core.navigation.NavGraph
import com.mediscan.app.core.theme.MediScanTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Single Activity for the entire MediScan app.
 * @AndroidEntryPoint enables Hilt dependency injection in this Activity.
 * Uses Jetpack Compose for the entire UI via setContent.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MediScanTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph()
                }
            }
        }
    }
}
