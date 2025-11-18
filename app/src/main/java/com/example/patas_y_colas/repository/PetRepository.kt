package com.example.patas_y_colas.repository

import android.content.Context
import com.example.patas_y_colas.data.network.LoginRequest
import com.example.patas_y_colas.data.network.RegisterRequest
import com.example.patas_y_colas.data.network.RetrofitClient
import com.example.patas_y_colas.data.network.TokenManager
import com.example.patas_y_colas.model.Pet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PetRepository(private val context: Context) {

    private val api = RetrofitClient.getClient(context)

    private val _pets = MutableStateFlow<List<Pet>>(emptyList())
    val allPets: StateFlow<List<Pet>> = _pets.asStateFlow()


    suspend fun getFunFact(): String? {
        return try {
            api.getCatFact().fact
        } catch (e: Exception) {
            e.printStackTrace()
            "No se pudo cargar el dato curioso. Revisa tu conexión."
        }
    }
    suspend fun refreshPets() {
        try {
            val petsFromApi = api.getAllPets()
            _pets.value = petsFromApi
        } catch (e: Exception) {
            e.printStackTrace()
            _pets.value = emptyList()
        }
    }

    // --- Login (MODIFICADO) ---
    suspend fun login(email: String, pass: String): Boolean {
        return try {
            val response = api.login(LoginRequest(email, pass))
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                // Guardamos los tokens Y el nombre
                TokenManager.saveTokens(
                    context,
                    authResponse.token,
                    authResponse.refreshToken,
                    authResponse.firstname // <-- AÑADIDO
                )
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // --- Registro (MODIFICADO) ---
    suspend fun register(nombre: String, apellido: String, email: String, pass: String): Boolean {
        return try {
            val request = RegisterRequest(
                firstname = nombre,
                lastname = apellido,
                email = email,
                password = pass
            )
            val response = api.register(request)
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                // Guardamos los tokens Y el nombre
                TokenManager.saveTokens(
                    context,
                    authResponse.token,
                    authResponse.refreshToken,
                    authResponse.firstname // <-- AÑADIDO
                )
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // --- Logout (Sin cambios) ---
    fun logout() {
        TokenManager.clearTokens(context)
        _pets.value = emptyList()
    }


    // --- Funciones de Pet (Sin cambios) ---
    suspend fun insert(pet: Pet) {
        try {
            api.createPet(pet)
            refreshPets()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun update(pet: Pet) {
        val idToUpdate = pet.id
        if (idToUpdate != null) {
            try {
                api.updatePet(idToUpdate, pet)
                refreshPets()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            println("Error: Intento de actualizar mascota con ID nulo.")
        }
    }

    suspend fun delete(pet: Pet) {
        val idToDelete = pet.id
        if (idToDelete != null) {
            try {
                api.deletePet(idToDelete)
                refreshPets()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            println("Error: Intento de borrar mascota con ID nulo.")
        }
    }
}