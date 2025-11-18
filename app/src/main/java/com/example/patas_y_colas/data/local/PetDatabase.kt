package com.example.patas_y_colas.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.patas_y_colas.model.Pet
// import com.example.patas_y_colas.model.VaccineRecord // <--- BORRAMOS ESTE IMPORT

// --- CORRECCIÓN AQUÍ: Eliminamos VaccineRecord::class de la lista de entidades ---
@Database(entities = [Pet::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class PetDatabase : RoomDatabase() {

    abstract fun petDao(): PetDao

    companion object {
        @Volatile
        private var INSTANCE: PetDatabase? = null

        fun getDatabase(context: Context): PetDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PetDatabase::class.java,
                    "pet_database"
                )
                    .fallbackToDestructiveMigration() // Asegúrate de tener esto si cambias la estructura
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}