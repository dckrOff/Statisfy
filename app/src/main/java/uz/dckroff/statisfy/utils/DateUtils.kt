package uz.dckroff.statisfy.utils

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Утилиты для работы с датами
 */

/**
 * Форматирование даты в удобочитаемый формат
 */
fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        dateString
    }
}

/**
 * Форматирование даты с временем
 */
fun formatDateTime(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        dateString
    }
}

/**
 * Получить относительное время (например, "2 часа назад")
 */
fun getRelativeTimeString(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = inputFormat.parse(dateString) ?: Date()
        
        val now = Date()
        val diffInMillis = now.time - date.time
        
        when {
            diffInMillis < TimeUnit.MINUTES.toMillis(1) -> "только что"
            diffInMillis < TimeUnit.HOURS.toMillis(1) -> {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis)
                "$minutes мин назад"
            }
            diffInMillis < TimeUnit.DAYS.toMillis(1) -> {
                val hours = TimeUnit.MILLISECONDS.toHours(diffInMillis)
                "$hours ч назад"
            }
            diffInMillis < TimeUnit.DAYS.toMillis(7) -> {
                val days = TimeUnit.MILLISECONDS.toDays(diffInMillis)
                "$days дн назад"
            }
            else -> formatDate(dateString)
        }
    } catch (e: Exception) {
        dateString
    }
}

/**
 * Проверить, является ли дата сегодняшней
 */
fun isToday(dateString: String): Boolean {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = inputFormat.parse(dateString) ?: Date()
        
        val today = Calendar.getInstance()
        val dateCalendar = Calendar.getInstance()
        dateCalendar.time = date
        
        today.get(Calendar.YEAR) == dateCalendar.get(Calendar.YEAR) &&
        today.get(Calendar.DAY_OF_YEAR) == dateCalendar.get(Calendar.DAY_OF_YEAR)
    } catch (e: Exception) {
        false
    }
}

/**
 * Проверить, является ли дата вчерашней
 */
fun isYesterday(dateString: String): Boolean {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = inputFormat.parse(dateString) ?: Date()
        
        val yesterday = Calendar.getInstance()
        yesterday.add(Calendar.DAY_OF_YEAR, -1)
        
        val dateCalendar = Calendar.getInstance()
        dateCalendar.time = date
        
        yesterday.get(Calendar.YEAR) == dateCalendar.get(Calendar.YEAR) &&
        yesterday.get(Calendar.DAY_OF_YEAR) == dateCalendar.get(Calendar.DAY_OF_YEAR)
    } catch (e: Exception) {
        false
    }
}

/**
 * Получить удобочитаемый формат даты
 */
fun getReadableDate(dateString: String): String {
    return when {
        isToday(dateString) -> "Сегодня"
        isYesterday(dateString) -> "Вчера"
        else -> formatDate(dateString)
    }
}