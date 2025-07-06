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
    val enablePushNotifications: Boolean = true,
    val dailyFactTime: String = "09:00",
    val enableNewsNotifications: Boolean = true,
    val newsNotificationFrequency: String = NotificationFrequency.DAILY.name,
    val enableRecommendations: Boolean = true,
    val enableActivityReminders: Boolean = true,
    val quietHoursStart: String = "22:00",
    val quietHoursEnd: String = "08:00",
    val notificationCategories: List<String> = emptyList(),
    
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
                enablePushNotifications = enablePushNotifications,
                dailyFactTime = dailyFactTime,
                enableNewsNotifications = enableNewsNotifications,
                newsNotificationFrequency = NotificationFrequency.valueOf(newsNotificationFrequency),
                enableRecommendations = enableRecommendations,
                enableActivityReminders = enableActivityReminders,
                quietHoursStart = quietHoursStart,
                quietHoursEnd = quietHoursEnd,
                notificationCategories = notificationCategories
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
                enablePushNotifications = preferences.notificationSettings.enablePushNotifications,
                dailyFactTime = preferences.notificationSettings.dailyFactTime,
                enableNewsNotifications = preferences.notificationSettings.enableNewsNotifications,
                newsNotificationFrequency = preferences.notificationSettings.newsNotificationFrequency.name,
                enableRecommendations = preferences.notificationSettings.enableRecommendations,
                enableActivityReminders = preferences.notificationSettings.enableActivityReminders,
                quietHoursStart = preferences.notificationSettings.quietHoursStart,
                quietHoursEnd = preferences.notificationSettings.quietHoursEnd,
                notificationCategories = preferences.notificationSettings.notificationCategories,
                
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