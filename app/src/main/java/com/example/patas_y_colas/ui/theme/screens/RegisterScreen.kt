package com.example.patas_y_colas.ui.theme.screens

import android.util.Patterns // <-- AÑADIDO PARA VALIDAR EMAIL
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState // <-- Importado para Scroll
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll // <-- Importado para Scroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.patas_y_colas.PetApplication
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(navController: NavController) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = (context.applicationContext as PetApplication).repository
    var isLoading by remember { mutableStateOf(false) }

    // --- 1. AÑADIR ESTADOS DE ERROR ---
    var nombreError by remember { mutableStateOf<String?>(null) }
    var apellidoError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    // --- 2. FUNCIÓN DE VALIDACIÓN ---
    fun validate(): Boolean {
        // Validar Nombre
        if (nombre.isBlank()) {
            nombreError = "El nombre no puede estar vacío"
        } else if (nombre.any { it.isDigit() }) {
            nombreError = "El nombre no debe contener números"
        } else {
            nombreError = null
        }

        // Validar Apellido
        if (apellido.isBlank()) {
            apellidoError = "El apellido no puede estar vacío"
        } else if (apellido.any { it.isDigit() }) {
            apellidoError = "El apellido no debe contener números"
        } else {
            apellidoError = null
        }

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
        } else if (password.length < 6) {
            passwordError = "La contraseña debe tener al menos 6 caracteres"
        } else {
            passwordError = null
        }

        return nombreError == null && apellidoError == null && emailError == null && passwordError == null
    }


    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(24.dp)
                .imePadding() // <-- Padding para el teclado
                .verticalScroll(rememberScrollState()), // <-- Scroll
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Crear Cuenta",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // --- 3. MODIFICAR TEXTFIELDS CON VALIDACIÓN ---
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it; nombreError = null },
                label = { Text("Nombre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = nombreError != null,
                supportingText = {
                    if (nombreError != null) {
                        Text(nombreError!!, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = apellido,
                onValueChange = { apellido = it; apellidoError = null },
                label = { Text("Apellido") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = apellidoError != null,
                supportingText = {
                    if (apellidoError != null) {
                        Text(apellidoError!!, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it; emailError = null },
                label = { Text("Correo electrónico") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = emailError != null,
                supportingText = {
                    if (emailError != null) {
                        Text(emailError!!, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it; passwordError = null },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                isError = passwordError != null,
                supportingText = {
                    if (passwordError != null) {
                        Text(passwordError!!, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    // --- 4. AÑADIR LLAMADA A VALIDATE() ---
                    if (validate()) {
                        scope.launch {
                            isLoading = true
                            // Llamamos a la nueva función register del repositorio
                            val success = repository.register(nombre, apellido, email, password)
                            isLoading = false
                            if (success) {
                                Toast.makeText(context, "¡Registro exitoso!", Toast.LENGTH_SHORT).show()
                                // Navegamos al menú directamente
                                navController.navigate("menu") {
                                    popUpTo("register") { inclusive = true }
                                    popUpTo("login") { inclusive = true }
                                }
                            } else {
                                Toast.makeText(context, "Error al registrar. Intenta con otro correo.", Toast.LENGTH_LONG).show()
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
                    Text("Registrarse", fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para volver al Login si ya tiene cuenta
            TextButton(onClick = { navController.popBackStack() }) {
                Text("¿Ya tienes cuenta? Inicia sesión")
            }
        }
    }
}