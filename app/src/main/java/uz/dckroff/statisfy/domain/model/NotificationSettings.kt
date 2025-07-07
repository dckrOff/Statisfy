package uz.dckroff.statisfy.domain.model

/**
 * Настройки уведомлений пользователя
 */
data class NotificationSettings(
    // Общие настройки
    val notificationsEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val lightsEnabled: Boolean = true,
    
    // Факт дня
    val dailyFactEnabled: Boolean = true,
    val dailyFactTime: NotificationTime = NotificationTime(9, 0), // 09:00
    
    // Новости
    val newsNotificationsEnabled: Boolean = true,
    val newsCategories: Set<String> = emptySet(), // Пустое множество = все категории
    val newsMaxPerDay: Int = 3,
    
    // Рекомендации
    val recommendationsEnabled: Boolean = true,
    val recommendationsFrequency: RecommendationFrequency = RecommendationFrequency.WEEKLY,
    
    // Напоминания
    val inactivityRemindersEnabled: Boolean = true,
    val inactivityThresholdDays: Int = 3,
    
    // Quiet Hours (Не беспокоить)
    val quietHoursEnabled: Boolean = false,
    val quietHoursStart: NotificationTime = NotificationTime(22, 0), // 22:00
    val quietHoursEnd: NotificationTime = NotificationTime(8, 0), // 08:00
    
    // Дни недели для уведомлений
    val enabledDaysOfWeek: Set<DayOfWeek> = DayOfWeek.values().toSet()
)

/**
 * Время для уведомлений
 */
data class NotificationTime(
    val hour: Int, // 0-23
    val minute: Int // 0-59
) {
    override fun toString(): String {
        return String.format("%02d:%02d", hour, minute)
    }
    
    /**
     * Преобразование в минуты с начала дня
     */
    fun toMinutes(): Int = hour * 60 + minute
    
    companion object {
        /**
         * Создание из минут с начала дня
         */
        fun fromMinutes(minutes: Int): NotificationTime {
            return NotificationTime(
                hour = minutes / 60,
                minute = minutes % 60
            )
        }
        
        /**
         * Создание из строки "HH:mm"
         */
        fun fromString(timeString: String): NotificationTime? {
            return try {
                val parts = timeString.split(":")
                if (parts.size == 2) {
                    NotificationTime(
                        hour = parts[0].toInt(),
                        minute = parts[1].toInt()
                    )
                } else null
            } catch (e: Exception) {
                null
            }
        }
    }
}

/**
 * Частота рекомендаций
 */
enum class RecommendationFrequency(val displayName: String, val intervalDays: Int) {
    DAILY("Ежедневно", 1),
    WEEKLY("Еженедельно", 7),
    MONTHLY("Ежемесячно", 30),
    NEVER("Никогда", -1);
    
    companion object {
        fun fromString(value: String): RecommendationFrequency {
            return values().find { it.name.equals(value, ignoreCase = true) } ?: WEEKLY
        }
    }
}

/**
 * Дни недели
 */
enum class DayOfWeek(val displayName: String, val calendarValue: Int) {
    MONDAY("Понедельник", 2),
    TUESDAY("Вторник", 3),
    WEDNESDAY("Среда", 4),
    THURSDAY("Четверг", 5),
    FRIDAY("Пятница", 6),
    SATURDAY("Суббота", 7),
    SUNDAY("Воскресенье", 1);
    
    companion object {
        fun fromCalendarValue(value: Int): DayOfWeek? {
            return values().find { it.calendarValue == value }
        }
        
        fun fromString(value: String): DayOfWeek? {
            return values().find { it.name.equals(value, ignoreCase = true) }
        }
    }
}

/**
 * Типы уведомлений с настройками
 */
enum class NotificationChannel(
    val channelId: String,
    val displayName: String,
    val description: String,
    val importance: Int
) {
    DAILY_FACT(
        "daily_facts",
        "Факт дня",
        "Ежедневные интересные факты",
        3 // IMPORTANCE_DEFAULT
    ),
    
    NEWS_UPDATE(
        "news_updates",
        "Новости",
        "Новые интересные новости",
        3 // IMPORTANCE_DEFAULT
    ),
    
    RECOMMENDATIONS(
        "recommendations",
        "Рекомендации",
        "Персонализированные рекомендации",
        2 // IMPORTANCE_LOW
    ),
    
    REMINDER(
        "reminders",
        "Напоминания",
        "Напоминания о чтении фактов",
        4 // IMPORTANCE_HIGH
    ),
    
    GENERAL(
        "general",
        "Общие уведомления",
        "Общие уведомления приложения",
        3 // IMPORTANCE_DEFAULT
    );
    
    companion object {
        fun fromChannelId(channelId: String): NotificationChannel? {
            return values().find { it.channelId == channelId }
        }
    }
}