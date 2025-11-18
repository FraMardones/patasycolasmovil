package com.example.patas_y_colas.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "pets")
data class Pet(
    @PrimaryKey(autoGenerate = true)
    // --- CAMBIO: De Int a Int? ---
    // Permitimos que el ID sea nulo para los inserts
    val id: Int? = null,

    val name: String,
    val species: String,
    val breed: String,
    val age: String,
    val weight: String,
    val imageUri: String? = null,

    @SerializedName("vaccineRecords")
    val vaccines: List<VaccineRecord> = emptyList()
)