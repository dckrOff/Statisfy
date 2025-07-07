package uz.dckroff.statisfy.domain.model

/**
 * Модель пользовательских предпочтений
 */
data class UserPreferences(
    val userId: String,
    val contentPreferences: ContentPreferences = ContentPreferences(),
    val notificationSettings: NotificationSettings = NotificationSettings(),
    val displaySettings: DisplaySettings = DisplaySettings(),
    val privacySettings: PrivacySettings = PrivacySettings(),
    val interestCategories: List<String> = emptyList(),
    val readingGoals: ReadingGoals? = null,
    val lastUpdated: String
)

/**
 * Предпочтения по контенту
 */
data class ContentPreferences(
    val preferredLanguages: List<String> = listOf("ru"),
    val preferredSources: List<String> = emptyList(),
    val excludedSources: List<String> = emptyList(),
    val contentComplexity: ContentComplexity = ContentComplexity.MEDIUM,
    val preferredLength: ContentLength = ContentLength.MEDIUM,
    val topicsOfInterest: List<String> = emptyList(),
    val excludedTopics: List<String> = emptyList(),
    val showRelevantOnly: Boolean = false,
    val enablePersonalizedRecommendations: Boolean = true
)



/**
 * Настройки отображения
 */
data class DisplaySettings(
    val theme: AppTheme = AppTheme.SYSTEM,
    val fontSize: FontSize = FontSize.MEDIUM,
    val enableAnimations: Boolean = true,
    val enableHapticFeedback: Boolean = true,
    val compactMode: Boolean = false,
    val showImages: Boolean = true,
    val autoPlayVideo: Boolean = false,
    val saveDataMode: Boolean = false
)

/**
 * Настройки приватности
 */
data class PrivacySettings(
    val shareAnalytics: Boolean = true,
    val shareReadingHistory: Boolean = false,
    val enablePersonalization: Boolean = true,
    val allowLocationBasedContent: Boolean = false,
    val shareUsageStatistics: Boolean = true
)

/**
 * Цели чтения
 */
data class ReadingGoals(
    val dailyFactsGoal: Int = 3,
    val weeklyNewsGoal: Int = 10,
    val monthlyLearningGoal: Int = 50,
    val enableGoalNotifications: Boolean = true,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0
)

/**
 * Сложность контента
 */
enum class ContentComplexity(val displayName: String) {
    SIMPLE("Простой"),
    MEDIUM("Средний"),
    COMPLEX("Сложный"),
    ACADEMIC("Академический")
}

/**
 * Длина контента
 */
enum class ContentLength(val displayName: String) {
    SHORT("Короткий"),
    MEDIUM("Средний"),
    LONG("Длинный"),
    ANY("Любой")
}

/**
 * Частота уведомлений
 */
enum class NotificationFrequency(val displayName: String) {
    DISABLED("Отключено"),
    DAILY("Ежедневно"),
    WEEKLY("Еженедельно"),
    CUSTOM("Настраиваемо")
}

/**
 * Темы приложения
 */
enum class AppTheme(val displayName: String) {
    LIGHT("Светлая"),
    DARK("Темная"),
    SYSTEM("Системная")
}

/**
 * Размеры шрифта
 */
enum class FontSize(val displayName: String, val scale: Float) {
    SMALL("Маленький", 0.85f),
    MEDIUM("Средний", 1.0f),
    LARGE("Большой", 1.15f),
    EXTRA_LARGE("Очень большой", 1.3f)
}