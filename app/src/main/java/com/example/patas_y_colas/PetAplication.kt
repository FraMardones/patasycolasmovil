package com.example.patas_y_colas

import android.app.Application
import com.example.patas_y_colas.notifications.createNotificationChannel
import com.example.patas_y_colas.repository.PetRepository

class PetApplication : Application() {
    // Ya no necesitamos la base de datos local obligatoriamente para el repo
    // pasamos 'this' (el contexto)
    val repository by lazy { PetRepository(this) }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel(this)
    }
}