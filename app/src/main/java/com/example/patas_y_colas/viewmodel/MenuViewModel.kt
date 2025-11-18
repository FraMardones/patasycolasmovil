package com.example.patas_y_colas.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.patas_y_colas.model.Pet
import com.example.patas_y_colas.notifications.NotificationScheduler
import com.example.patas_y_colas.repository.PetRepository
import kotlinx.coroutines.flow.MutableStateFlow // <-- AÑADIDO
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow // <-- AÑADIDO
import kotlinx.coroutines.launch

class MenuViewModel(
    private val repository: PetRepository,
    private val application: Application
) : ViewModel() {

    // --- CAMBIO: Ahora solo nos suscribimos al StateFlow del repositorio ---
    val allPets: StateFlow<List<Pet>> = repository.allPets

    // --- AÑADIDO: Estado para el dato curioso ---
    private val _catFact = MutableStateFlow<String?>(null)
    val catFact = _catFact.asStateFlow()
    // ------------------------------------------

    // --- NUEVO: Bloque 'init' para la carga inicial ---
    init {
        // Le pedimos al repositorio que cargue la lista de mascotas
        // tan pronto como el ViewModel se cree.
        viewModelScope.launch {
            repository.refreshPets()
        }
    }

    fun insert(pet: Pet) = viewModelScope.launch {
        // Ya no necesitamos 'refreshPets()' aquí, el repo lo hace solo
        repository.insert(pet)
        NotificationScheduler.scheduleNotifications(application, pet)

        pet.vaccines.lastOrNull()?.vaccineName?.let { vaccineName ->
            if(vaccineName.isNotBlank()) {
                NotificationScheduler.sendTestNotification(application, pet.name, vaccineName)
            }
        }
    }

    fun update(pet: Pet) = viewModelScope.launch {
        repository.update(pet)
        NotificationScheduler.scheduleNotifications(application, pet)

        pet.vaccines.lastOrNull()?.vaccineName?.let { vaccineName ->
            if(vaccineName.isNotBlank()) {
                NotificationScheduler.sendTestNotification(application, pet.name, vaccineName)
            }
        }
    }

    fun delete(pet: Pet) = viewModelScope.launch {
        NotificationScheduler.cancelNotificationsForPet(application, pet)
        repository.delete(pet)
    }

    // --- AÑADIDO: Funciones para el dato curioso ---
    fun loadFunFact() {
        viewModelScope.launch {
            _catFact.value = "Cargando..." // Mensaje de carga
            _catFact.value = repository.getFunFact()
        }
    }

    fun clearFunFact() {
        _catFact.value = null
    }
    // -----------------------------------------
}

class MenuViewModelFactory(
    private val repository: PetRepository,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MenuViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MenuViewModel(repository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}