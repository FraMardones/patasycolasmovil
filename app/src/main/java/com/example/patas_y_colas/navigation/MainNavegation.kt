package com.example.patas_y_colas.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext // <-- 1. IMPORTAR CONTEXTO
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.patas_y_colas.data.network.TokenManager // <-- 2. IMPORTAR TOKEN MANAGER
import com.example.patas_y_colas.ui.theme.screens.MenuScreen
import com.example.patas_y_colas.ui.theme.screens.PortadaScreen
import com.example.patas_y_colas.ui.theme.screens.LoginScreen
import com.example.patas_y_colas.ui.theme.screens.RegisterScreen

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "portada") {

        composable("portada") {
            // --- ¡AQUÍ ESTÁ LA LÓGICA! ---
            val context = LocalContext.current

            // 3. Verificamos si hay un token guardado
            // Usamos getAccessToken, pero getRefreshToken también serviría
            val token = TokenManager.getAccessToken(context)

            // 4. Decidimos el destino: si hay token vamos a "menu", si no, a "login"
            val destination = if (token != null) "menu" else "login"

            // 5. Pasamos la lógica al botón "Continuar" de la PortadaScreen
            PortadaScreen(onContinueClick = {
                navController.navigate(destination) {
                    // Limpiamos la pila para que no puedan volver a la portada
                    popUpTo("portada") { inclusive = true }
                }
            })
        }

        composable("login") {
            LoginScreen(navController = navController)
        }

        composable("register") {
            RegisterScreen(navController = navController)
        }

        composable("menu") {
            MenuScreen(navController = navController)
        }
    }
}