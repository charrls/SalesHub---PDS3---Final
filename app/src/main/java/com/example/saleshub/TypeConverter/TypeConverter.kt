package com.example.saleshub.TypeConverter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken



class Converters {

    @TypeConverter
    fun fromStringListToString(value: List<String>?): String? {
        return value?.joinToString(",")
    }

    @TypeConverter
    fun fromStringToStringList(value: String?): List<String>? {
        return value?.split(",")?.map { it.trim() }
    }

    @TypeConverter
    fun fromIntListToString(cantidades: List<Int>?): String? {
        return Gson().toJson(cantidades)
    }

    @TypeConverter
    fun fromStringToIntList(data: String?): List<Int>? {
        val listType = object : TypeToken<List<Int>>() {}.type
        return Gson().fromJson(data, listType)
    }
    @TypeConverter
    fun fromPairListToString(pairs: List<Pair<String, Int>>?): String? {
        return Gson().toJson(pairs)
    }

    @TypeConverter
    fun fromStringToPairList(data: String?): List<Pair<String, Int>>? {
        val listType = object : TypeToken<List<Pair<String, Int>>>() {}.type
        return Gson().fromJson(data, listType)
    }
}








