package uz.dckroff.statisfy.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.Gson
import uz.dckroff.statisfy.data.local.converters.StringListConverter
import uz.dckroff.statisfy.domain.model.*

/**
 * Сущность для хранения пользовательских предпочтений
 */
@Entity(tableName = "user_preferences")
@TypeConverters(StringListConverter::class)
data class UserPreferencesEntity(
    @PrimaryKey
    val userId: String,
    
    // Content Preferences
    val preferredLanguages: List<String> = listOf("ru"),
    val preferredSources: List<String> = emptyList(),
    val excludedSources: List<String> = emptyList(),
    val contentComplexity: String = ContentComplexity.MEDIUM.name,
    val preferredLength: String = ContentLength.MEDIUM.name,
    val topicsOfInterest: List<String> = emptyList(),
    val excludedTopics: List<String> = emptyList(),
    val showRelevantOnly: Boolean = false,
    val enablePersonalizedRecommendations: Boolean = true,
    
    // Notification Settings
    val notificationsEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val lightsEnabled: Boolean = true,
    val dailyFactEnabled: Boolean = true,
    val dailyFactTime: String = "09:00",
    val newsNotificationsEnabled: Boolean = true,
    val newsCategories: List<String> = emptyList(),
    val newsMaxPerDay: Int = 3,
    val recommendationsEnabled: Boolean = true,
    val recommendationsFrequency: String = RecommendationFrequency.WEEKLY.name,
    val inactivityRemindersEnabled: Boolean = true,
    val inactivityThresholdDays: Int = 3,
    val quietHoursEnabled: Boolean = false,
    val quietHoursStart: String = "22:00",
    val quietHoursEnd: String = "08:00",
    val enabledDaysOfWeek: List<String> = DayOfWeek.values().map { it.name },
    
    // Display Settings
    val theme: String = AppTheme.SYSTEM.name,
    val fontSize: String = FontSize.MEDIUM.name,
    val enableAnimations: Boolean = true,
    val enableHapticFeedback: Boolean = true,
    val compactMode: Boolean = false,
    val showImages: Boolean = true,
    val autoPlayVideo: Boolean = false,
    val saveDataMode: Boolean = false,
    
    // Privacy Settings
    val shareAnalytics: Boolean = true,
    val shareReadingHistory: Boolean = false,
    val enablePersonalization: Boolean = true,
    val allowLocationBasedContent: Boolean = false,
    val shareUsageStatistics: Boolean = true,
    
    // Interest Categories
    val interestCategories: List<String> = emptyList(),
    
    // Reading Goals (JSON)
    val readingGoalsJson: String? = null,
    
    val lastUpdated: String
) {
    /**
     * Преобразование в доменную модель
     */
    fun toDomainModel(): UserPreferences {
        val readingGoals = readingGoalsJson?.let { json ->
            try {
                Gson().fromJson(json, ReadingGoals::class.java)
            } catch (e: Exception) {
                null
            }
        }
        
        return UserPreferences(
            userId = userId,
            contentPreferences = ContentPreferences(
                preferredLanguages = preferredLanguages,
                preferredSources = preferredSources,
                excludedSources = excludedSources,
                contentComplexity = ContentComplexity.valueOf(contentComplexity),
                preferredLength = ContentLength.valueOf(preferredLength),
                topicsOfInterest = topicsOfInterest,
                excludedTopics = excludedTopics,
                showRelevantOnly = showRelevantOnly,
                enablePersonalizedRecommendations = enablePersonalizedRecommendations
            ),
            notificationSettings = NotificationSettings(
                notificationsEnabled = notificationsEnabled,
                soundEnabled = soundEnabled,
                vibrationEnabled = vibrationEnabled,
                lightsEnabled = lightsEnabled,
                dailyFactEnabled = dailyFactEnabled,
                dailyFactTime = NotificationTime.fromString(dailyFactTime) ?: NotificationTime(9, 0),
                newsNotificationsEnabled = newsNotificationsEnabled,
                newsCategories = newsCategories.toSet(),
                newsMaxPerDay = newsMaxPerDay,
                recommendationsEnabled = recommendationsEnabled,
                recommendationsFrequency = try {
                    RecommendationFrequency.valueOf(recommendationsFrequency)
                } catch (e: Exception) {
                    RecommendationFrequency.WEEKLY
                },
                inactivityRemindersEnabled = inactivityRemindersEnabled,
                inactivityThresholdDays = inactivityThresholdDays,
                quietHoursEnabled = quietHoursEnabled,
                quietHoursStart = NotificationTime.fromString(quietHoursStart) ?: NotificationTime(22, 0),
                quietHoursEnd = NotificationTime.fromString(quietHoursEnd) ?: NotificationTime(8, 0),
                enabledDaysOfWeek = enabledDaysOfWeek.mapNotNull {
                    try {
                        DayOfWeek.valueOf(it)
                    } catch (e: Exception) {
                        null
                    }
                }.toSet()
            ),
            displaySettings = DisplaySettings(
                theme = AppTheme.valueOf(theme),
                fontSize = FontSize.valueOf(fontSize),
                enableAnimations = enableAnimations,
                enableHapticFeedback = enableHapticFeedback,
                compactMode = compactMode,
                showImages = showImages,
                autoPlayVideo = autoPlayVideo,
                saveDataMode = saveDataMode
            ),
            privacySettings = PrivacySettings(
                shareAnalytics = shareAnalytics,
                shareReadingHistory = shareReadingHistory,
                enablePersonalization = enablePersonalization,
                allowLocationBasedContent = allowLocationBasedContent,
                shareUsageStatistics = shareUsageStatistics
            ),
            interestCategories = interestCategories,
            readingGoals = readingGoals,
            lastUpdated = lastUpdated
        )
    }
    
    companion object {
        /**
         * Создание из доменной модели
         */
        fun fromDomainModel(preferences: UserPreferences): UserPreferencesEntity {
            return UserPreferencesEntity(
                userId = preferences.userId,
                
                // Content Preferences
                preferredLanguages = preferences.contentPreferences.preferredLanguages,
                preferredSources = preferences.contentPreferences.preferredSources,
                excludedSources = preferences.contentPreferences.excludedSources,
                contentComplexity = preferences.contentPreferences.contentComplexity.name,
                preferredLength = preferences.contentPreferences.preferredLength.name,
                topicsOfInterest = preferences.contentPreferences.topicsOfInterest,
                excludedTopics = preferences.contentPreferences.excludedTopics,
                showRelevantOnly = preferences.contentPreferences.showRelevantOnly,
                enablePersonalizedRecommendations = preferences.contentPreferences.enablePersonalizedRecommendations,
                
                // Notification Settings
                notificationsEnabled = preferences.notificationSettings.notificationsEnabled,
                soundEnabled = preferences.notificationSettings.soundEnabled,
                vibrationEnabled = preferences.notificationSettings.vibrationEnabled,
                lightsEnabled = preferences.notificationSettings.lightsEnabled,
                dailyFactEnabled = preferences.notificationSettings.dailyFactEnabled,
                dailyFactTime = preferences.notificationSettings.dailyFactTime.toString(),
                newsNotificationsEnabled = preferences.notificationSettings.newsNotificationsEnabled,
                newsCategories = preferences.notificationSettings.newsCategories.toList(),
                newsMaxPerDay = preferences.notificationSettings.newsMaxPerDay,
                recommendationsEnabled = preferences.notificationSettings.recommendationsEnabled,
                recommendationsFrequency = preferences.notificationSettings.recommendationsFrequency.name,
                inactivityRemindersEnabled = preferences.notificationSettings.inactivityRemindersEnabled,
                inactivityThresholdDays = preferences.notificationSettings.inactivityThresholdDays,
                quietHoursEnabled = preferences.notificationSettings.quietHoursEnabled,
                quietHoursStart = preferences.notificationSettings.quietHoursStart.toString(),
                quietHoursEnd = preferences.notificationSettings.quietHoursEnd.toString(),
                enabledDaysOfWeek = preferences.notificationSettings.enabledDaysOfWeek.map { it.name },
                
                // Display Settings
                theme = preferences.displaySettings.theme.name,
                fontSize = preferences.displaySettings.fontSize.name,
                enableAnimations = preferences.displaySettings.enableAnimations,
                enableHapticFeedback = preferences.displaySettings.enableHapticFeedback,
                compactMode = preferences.displaySettings.compactMode,
                showImages = preferences.displaySettings.showImages,
                autoPlayVideo = preferences.displaySettings.autoPlayVideo,
                saveDataMode = preferences.displaySettings.saveDataMode,
                
                // Privacy Settings
                shareAnalytics = preferences.privacySettings.shareAnalytics,
                shareReadingHistory = preferences.privacySettings.shareReadingHistory,
                enablePersonalization = preferences.privacySettings.enablePersonalization,
                allowLocationBasedContent = preferences.privacySettings.allowLocationBasedContent,
                shareUsageStatistics = preferences.privacySettings.shareUsageStatistics,
                
                // Interest Categories
                interestCategories = preferences.interestCategories,
                
                // Reading Goals
                readingGoalsJson = preferences.readingGoals?.let { Gson().toJson(it) },
                
                lastUpdated = preferences.lastUpdated
            )
        }
        
        /**
         * Создание настроек по умолчанию
         */
        fun createDefault(userId: String): UserPreferencesEntity {
            return UserPreferencesEntity(
                userId = userId,
                lastUpdated = System.currentTimeMillis().toString()
            )
        }
    }
}