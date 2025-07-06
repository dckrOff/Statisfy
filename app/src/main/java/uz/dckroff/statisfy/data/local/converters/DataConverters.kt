package uz.dckroff.statisfy.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Конвертер для List<String>
 */
class StringListConverter {
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType) ?: emptyList()
    }
}

/**
 * Конвертер для Set<String>
 */
class StringSetConverter {
    @TypeConverter
    fun fromStringSet(value: Set<String>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toStringSet(value: String): Set<String> {
        val setType = object : TypeToken<Set<String>>() {}.type
        return Gson().fromJson(value, setType) ?: emptySet()
    }
}

/**
 * Конвертер для Map<String, String>
 */
class MapConverter {
    @TypeConverter
    fun fromStringMap(value: Map<String, String>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toStringMap(value: String): Map<String, String> {
        val mapType = object : TypeToken<Map<String, String>>() {}.type
        return Gson().fromJson(value, mapType) ?: emptyMap()
    }
}

/**
 * Конвертер для Map<String, Int>
 */
class StringIntMapConverter {
    @TypeConverter
    fun fromStringIntMap(value: Map<String, Int>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toStringIntMap(value: String): Map<String, Int> {
        val mapType = object : TypeToken<Map<String, Int>>() {}.type
        return Gson().fromJson(value, mapType) ?: emptyMap()
    }
}

/**
 * Конвертер для Map<String, Double>
 */
class StringDoubleMapConverter {
    @TypeConverter
    fun fromStringDoubleMap(value: Map<String, Double>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toStringDoubleMap(value: String): Map<String, Double> {
        val mapType = object : TypeToken<Map<String, Double>>() {}.type
        return Gson().fromJson(value, mapType) ?: emptyMap()
    }
}

/**
 * Конвертер для Map<String, Any>
 */
class GenericMapConverter {
    @TypeConverter
    fun fromGenericMap(value: Map<String, Any>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toGenericMap(value: String): Map<String, Any> {
        val mapType = object : TypeToken<Map<String, Any>>() {}.type
        return Gson().fromJson(value, mapType) ?: emptyMap()
    }
}