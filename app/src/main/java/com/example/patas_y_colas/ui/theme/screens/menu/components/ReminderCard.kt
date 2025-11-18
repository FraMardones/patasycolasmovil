package com.example.patas_y_colas.ui.screens.menu.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.patas_y_colas.model.Pet
import com.example.patas_y_colas.ui.theme.PetTextDark
import com.example.patas_y_colas.ui.theme.PetTextLight
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReminderSection(pets: List<Pet>) {
    val reminders = remember(pets) {
        val dateFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        pets.flatMap { pet ->
            // CAMBIO: Usamos la lista directa del modelo, sin Gson
            val vaccineList = pet.vaccines

            vaccineList.filter { vaccine ->
                // Mantenemos la lógica de validación de fechas
                if (vaccine.vaccineName != null && vaccine.vaccineName.isNotBlank() &&
                    vaccine.date != null && vaccine.date.isNotBlank()) {
                    try {
                        val vaccineDate = dateFormat.parse(vaccine.date)
                        vaccineDate != null && !vaccineDate.before(today)
                    } catch (e: Exception) {
                        false
                    }
                } else {
                    false
                }
            }.map { vaccine -> pet.name to vaccine }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Recordatorios de Vacunas", style = MaterialTheme.typography.titleLarge, color = PetTextDark)

        if (reminders.isEmpty()) {
            Text("No hay vacunas programadas.", color = PetTextLight)
        } else {
            reminders.forEach { (petName, vaccine) ->
                // Aseguramos que vaccineName y date no sean nulos al pasarlos
                ReminderCard(
                    petName = petName,
                    vaccineName = vaccine.vaccineName ?: "Vacuna",
                    date = vaccine.date ?: ""
                )
            }
        }
    }
}

@Composable
fun ReminderCard(petName: String, vaccineName: String, date: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(imageVector = Icons.Default.Info, contentDescription = "Recordatorio", tint = PetTextLight)
            Column {
                Text(text = "$petName - $vaccineName", color = PetTextDark, fontWeight = FontWeight.Bold)
                Text(text = "Fecha: $date", color = PetTextLight)
            }
        }
    }
}