package com.example.patas_y_colas.notifications

import android.app.AlarmManager
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.patas_y_colas.R
import com.example.patas_y_colas.model.Pet
import java.text.SimpleDateFormat
import java.util.*

const val NOTIFICATION_CHANNEL_ID = "vaccine_reminder_channel"

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val petName = intent.getStringExtra("PET_NAME") ?: "Tu mascota"
        val vaccineName = intent.getStringExtra("VACCINE_NAME") ?: "una vacuna"

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Recordatorio de Vacuna")
            .setContentText("¡La vacuna '$vaccineName' de $petName está próxima!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}

object NotificationScheduler {

    fun sendTestNotification(context: Context, petName: String, vaccineName: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("¡Notificación de Prueba!")
            .setContentText("Así se verá el recordatorio para la vacuna '$vaccineName' de $petName.")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Esta es una notificación de prueba. El recordatorio real se enviará un día antes de la fecha programada."))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    fun scheduleNotifications(context: Context, pet: Pet) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val vaccines = pet.vaccines

        cancelNotificationsForPet(context, pet)

        vaccines.forEach { vaccine ->
            if (!vaccine.date.isNullOrBlank()) {
                val sdf = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
                try {
                    val date = sdf.parse(vaccine.date)
                    if (date != null) {
                        val calendar = Calendar.getInstance()
                        calendar.time = date
                        calendar.add(Calendar.DAY_OF_YEAR, -1) // Avisar 1 día antes
                        calendar.set(Calendar.HOUR_OF_DAY, 10)
                        calendar.set(Calendar.MINUTE, 0)
                        calendar.set(Calendar.SECOND, 0)

                        if (calendar.timeInMillis > System.currentTimeMillis()) {
                            val intent = Intent(context, NotificationReceiver::class.java).apply {
                                putExtra("PET_NAME", pet.name)
                                putExtra("VACCINE_NAME", vaccine.vaccineName)
                            }

                            // --- CORRECCIÓN LÍNEA 85 ---
                            // Usamos (pet.id ?: 0) para manejar el ID nulo de la mascota
                            val requestCode = (pet.id ?: 0) + (vaccine.id ?: vaccine.hashCode())

                            val pendingIntent = PendingIntent.getBroadcast(
                                context,
                                requestCode,
                                intent,
                                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                            )
                            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun cancelNotificationsForPet(context: Context, pet: Pet) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val vaccines = pet.vaccines

        vaccines.forEach { vaccine ->
            val intent = Intent(context, NotificationReceiver::class.java)

            // --- CORRECCIÓN LÍNEA 111 (aprox) ---
            val requestCode = (pet.id ?: 0) + (vaccine.id ?: vaccine.hashCode())

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }
    }
}

fun createNotificationChannel(application: Application) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Recordatorios de Vacunas"
        val descriptionText = "Canal para notificar sobre próximas vacunas de mascotas."
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}