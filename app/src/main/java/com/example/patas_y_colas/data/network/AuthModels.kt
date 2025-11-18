package com.example.patas_y_colas.data.network

// No es necesario modificar LoginRequest ni RegisterRequest
data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val firstname: String,
    val lastname: String,
    val email: String,
    val password: String
)

data class AuthResponse(
    val token: String,
    val refreshToken: String,
    val firstname: String? // <-- AÑADIDO
)