package com.example.patas_y_colas

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import com.example.patas_y_colas.navigation.MainNavigation
import com.example.patas_y_colas.ui.theme.Patas_y_ColasTheme

class MainActivity : ComponentActivity() {

    // Launcher para pedir el permiso de notificaciones
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Aquí podrías manejar si el usuario no concede el permiso,
        // por ahora no hacemos nada extra.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        askNotificationPermission() // Pedimos el permiso
        setContent {
            Patas_y_ColasTheme {
                MainNavigation()
            }
        }
    }

    private fun askNotificationPermission() {
        // Esta comprobación solo es necesaria para Android 13 (Tiramisu) o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

