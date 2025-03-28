package com.example.noteapp.core.utils

import androidx.room.TypeConverter
import com.example.noteapp.damain.model.DateTimeReminder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromDateTimeReminder(dateTimeReminder: DateTimeReminder?): String? {
        return Gson().toJson(dateTimeReminder)
    }

    @TypeConverter
    fun toDateTimeReminder(value: String?): DateTimeReminder? {
        return if (value == null) null else Gson().fromJson(value, object : TypeToken<DateTimeReminder>() {}.type)
    }

    @TypeConverter
    fun fromStringList(value: String?): List<String> {
        if (value.isNullOrEmpty()) return emptyList()
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun toStringList(list: List<String>?): String {
        return Gson().toJson(list ?: emptyList<String>())
    }
}
