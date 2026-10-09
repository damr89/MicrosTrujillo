package com.example.microstrujillo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.microstrujillo.presentation.TransportApp
import com.example.microstrujillo.ui.theme.MicrosTrujilloTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MicrosTrujilloTheme {
                TransportApp()
            }
        }
    }
}
