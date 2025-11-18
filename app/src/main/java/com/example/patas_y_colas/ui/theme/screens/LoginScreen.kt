package com.example.patas_y_colas.ui.theme.screens

import android.util.Patterns // <-- AÑADIDO PARA VALIDAR EMAIL
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState // <-- AÑADIDO PARA SCROLL
import androidx.compose.foundation.verticalScroll // <-- AÑADIDO PARA SCROLL
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// --- ¡CORRECCIÓN! ---
// Eliminamos el 'import androidx.navigation.NavController' que causaba el conflicto
import androidx.navigation.NavHostController // <- Esta es la única que necesitamos
import com.example.patas_y_colas.PetApplication
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavHostController) { // <- Usando NavHostController
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = (context.applicationContext as PetApplication).repository
    var isLoading by remember { mutableStateOf(false) }

    // --- 1. AÑADIR ESTADOS DE ERROR ---
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    // --- 2. FUNCIÓN DE VALIDACIÓN (similar a PetForm) ---
    fun validate(): Boolean {
        // Validar Email
        if (email.isBlank()) {
            emailError = "El correo no puede estar vacío"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "El formato del correo no es válido"
        } else {
            emailError = null
        }

        // Validar Contraseña
        if (password.isBlank()) {
            passwordError = "La contraseña no puede estar vacía"
        } else if (password.length < 6) { // Regla de negocio: mínimo 6 caracteres
            passwordError = "La contraseña debe tener al menos 6 caracteres"
        } else {
            passwordError = null
        }

        return emailError == null && passwordError == null
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(24.dp)
                .imePadding() // <-- AÑADIDO PARA EL TECLADO
                .verticalScroll(rememberScrollState()), // <-- AÑADIDO PARA SCROLL
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Iniciar Sesión",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // --- 3. MODIFICAR TEXTFIELD DE EMAIL ---
            OutlinedTextField(
                value = email,
                onValueChange = { email = it; emailError = null }, // Limpiar error al escribir
                label = { Text("Correo electrónico") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = emailError != null, // <-- Añadido
                supportingText = { // <-- Añadido
                    if (emailError != null) {
                        Text(emailError!!, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- 4. MODIFICAR TEXTFIELD DE CONTRASEÑA ---
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; passwordError = null }, // Limpiar error al escribir
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                isError = passwordError != null, // <-- Añadido
                supportingText = { // <-- Añadido
                    if (passwordError != null) {
                        Text(passwordError!!, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    // --- 5. AÑADIR LLAMADA A VALIDATE() ---
                    if (validate()) {
                        scope.launch {
                            isLoading = true
                            val success = repository.login(email, password)
                            isLoading = false
                            if (success) {
                                navController.navigate("menu") {
                                    popUpTo(0) { inclusive = true }
                                }
                            } else {
                                Toast.makeText(context, "Error de credenciales", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Ingresar", fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para ir a la pantalla de Registro
            TextButton(onClick = { navController.navigate("register") }) {
                Text("¿No tienes cuenta? Regístrate aquí")
            }
        }
    }
}