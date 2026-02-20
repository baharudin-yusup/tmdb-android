package dev.baharudin.tmdb_android.core.data.models

import androidx.annotation.Keep
import androidx.room.TypeConverter
import com.google.gson.Gson

@Keep
class ArrayListOfIntConverter {
    @TypeConverter
    fun fromArrayListToString(value: ArrayList<Int>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun fromStringToArrayList(value: String): ArrayList<Int> {
        return try {
            Gson().fromJson(value, IntArray::class.java)?.toCollection(arrayListOf()) ?: arrayListOf()
        } catch (e: Exception) {
            arrayListOf()
        }
    }
}