package com.example.patas_y_colas.model

import com.google.gson.annotations.SerializedName

data class VaccineRecord(
    val id: Int? = null,

    val vaccineName: String = "",
    @SerializedName("vaccinationDate")
    val date: String = ""
)