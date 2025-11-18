package com.example.patas_y_colas.data.local

import androidx.room.TypeConverter
import com.example.patas_y_colas.model.VaccineRecord
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromVaccineRecordList(value: List<VaccineRecord>?): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toVaccineRecordList(value: String): List<VaccineRecord>? {
        val listType = object : TypeToken<List<VaccineRecord>>() {}.type
        return Gson().fromJson(value, listType)
    }
}
