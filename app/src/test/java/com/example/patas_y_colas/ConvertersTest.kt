package com.example.patas_y_colas

import com.example.patas_y_colas.data.local.Converters
import com.example.patas_y_colas.model.Pet
import com.example.patas_y_colas.model.VaccineRecord
import org.junit.Test

import org.junit.Assert.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Pruebas unitarias locales para la lógica de la aplicación Patas y Colas.
 */
class ConvertersTest {

    // Instancia de la clase a probar
    private val converters = Converters()

    /**
     * Prueba 1: Verifica que la serialización (de Lista a JSON) funciona.
     */
    @Test
    fun converters_fromVaccineRecordList_isCorrect() {
        val vaccineList = listOf(
            VaccineRecord(id = 1, vaccineName = "Rabia", date = "10/10/2025"),
            VaccineRecord(id = 2, vaccineName = "Parvo", date = "12/12/2025")
        )

        val jsonString = converters.fromVaccineRecordList(vaccineList)

        // Verificamos que el JSON no esté vacío y contenga los nombres
        assertNotNull(jsonString)
        assertTrue(jsonString.contains("Rabia"))
        assertTrue(jsonString.contains("Parvo"))
        assertTrue(jsonString.contains("10/10/2025"))
    }

    /**
     * Prueba 2: Verifica que la deserialización (de JSON a Lista) funciona.
     */
    @Test
    fun converters_toVaccineRecordList_isCorrect() {
        val jsonString = """
            [
                {"id":1, "vaccineName":"Rabia", "vaccinationDate":"10/10/2025"},
                {"id":2, "vaccineName":"Parvo", "vaccinationDate":"12/12/2025"}
            ]
        """
        val expectedList = listOf(
            VaccineRecord(id = 1, vaccineName = "Rabia", date = "10/10/2025"),
            VaccineRecord(id = 2, vaccineName = "Parvo", date = "12/12/2025")
        )

        val resultList = converters.toVaccineRecordList(jsonString)

        assertNotNull(resultList)
        assertEquals(2, resultList!!.size)
        assertEquals(expectedList, resultList)
    }

    /**
     * Prueba 3: Verifica la lógica de filtrado de recordatorios de vacunas.
     * Esta prueba simula la lógica encontrada en `MenuScreen.kt` para asegurar
     * que solo las vacunas futuras y válidas se muestren como recordatorios.
     */
    @Test
    fun reminderLogic_filtersUpcomingVaccines_isCorrect() {
        // 1. Preparar las fechas de prueba
        val dateFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
        val todayCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val today = todayCalendar.time

        // Fecha futura (Mañana)
        val futureCalendar = (todayCalendar.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 1) }
        val futureDateStr = dateFormat.format(futureCalendar.time)

        // Fecha pasada (Ayer)
        val pastCalendar = (todayCalendar.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -1) }
        val pastDateStr = dateFormat.format(pastCalendar.time)

        // 2. Crear datos de prueba (Mascotas y Vacunas)
        val pets = listOf(
            Pet(
                id = 1, name = "Firulais", species = "Perro", breed = "Kiltro", age = "2", weight = "10 Kg",
                vaccines = listOf(
                    VaccineRecord(id = 10, vaccineName = "Vacuna Futura", date = futureDateStr),
                    VaccineRecord(id = 11, vaccineName = "Vacuna Pasada", date = pastDateStr),
                    VaccineRecord(id = 12, vaccineName = "Vacuna Hoy", date = dateFormat.format(today)),
                    VaccineRecord(id = 13, vaccineName = "", date = futureDateStr), // Vacuna inválida (nombre vacío)
                    VaccineRecord(id = 14, vaccineName = "Vacuna Sin Fecha", date = "") // Vacuna inválida (fecha vacía)
                )
            )
        )

        // 3. Aplicar la lógica de filtrado (copiada de MenuScreen.kt)
        val reminders = pets.flatMap { pet ->
            val vaccineList: List<VaccineRecord> = pet.vaccines

            vaccineList.filter { vaccine ->
                if (vaccine.vaccineName.isNotBlank() && vaccine.date.isNotBlank()) {
                    try {
                        val vaccineDate = dateFormat.parse(vaccine.date)
                        vaccineDate != null && !vaccineDate.before(today) // No debe ser anterior a hoy
                    } catch (e: Exception) {
                        false
                    }
                } else {
                    false
                }
            }.map { vaccine -> pet.name to vaccine }
        }

        // 4. Verificar (Asserts)
        // Solo debe haber 2 recordatorios: "Vacuna Futura" y "Vacuna Hoy".
        assertEquals(2, reminders.size)

        // Verificamos que la vacuna pasada no esté
        assertFalse(reminders.any { it.second.vaccineName == "Vacuna Pasada" })

        // Verificamos que las vacunas inválidas no estén
        assertFalse(reminders.any { it.second.vaccineName == "" })
        assertFalse(reminders.any { it.second.vaccineName == "Vacuna Sin Fecha" })

        // Verificamos que las correctas sí estén
        assertTrue(reminders.any { it.second.vaccineName == "Vacuna Futura" })
        assertTrue(reminders.any { it.second.vaccineName == "Vacuna Hoy" })
    }
}