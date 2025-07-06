package uz.dckroff.statisfy.data.remote.dto

import com.google.gson.annotations.SerializedName

// ПРОФИЛЬ ПОЛЬЗОВАТЕЛЯ

/**
 * DTO профиля пользователя
 */
data class UserProfileDto(
    @SerializedName("id") val id: String,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("displayName") val displayName: String?,
    @SerializedName("avatarUrl") val avatarUrl: String?,
    @SerializedName("bio") val bio: String?,
    @SerializedName("location") val location: String?,
    @SerializedName("website") val website: String?,
    @SerializedName("birthDate") val birthDate: String?,
    @SerializedName("joinDate") val joinDate: String,
    @SerializedName("lastActiveDate") val lastActiveDate: String?,
    @SerializedName("isVerified") val isVerified: Boolean = false,
    @SerializedName("isPremium") val isPremium: Boolean = false
)

/**
 * DTO для обновления профиля
 */
data class UserProfileUpdateDto(
    @SerializedName("displayName") val displayName: String?,
    @SerializedName("bio") val bio: String?,
    @SerializedName("location") val location: String?,
    @SerializedName("website") val website: String?,
    @SerializedName("birthDate") val birthDate: String?
)

/**
 * Ответ на загрузку аватара
 */
data class AvatarUploadResponseDto(
    @SerializedName("avatarUrl") val avatarUrl: String
)

// ПРЕДПОЧТЕНИЯ ПОЛЬЗОВАТЕЛЯ

/**
 * DTO пользовательских предпочтений
 */
data class UserPreferencesDto(
    @SerializedName("userId") val userId: String,
    @SerializedName("contentPreferences") val contentPreferences: ContentPreferencesDto,
    @SerializedName("notificationSettings") val notificationSettings: NotificationSettingsDto,
    @SerializedName("displaySettings") val displaySettings: DisplaySettingsDto,
    @SerializedName("privacySettings") val privacySettings: PrivacySettingsDto,
    @SerializedName("interestCategories") val interestCategories: List<String>,
    @SerializedName("readingGoals") val readingGoals: ReadingGoalsDto?,
    @SerializedName("lastUpdated") val lastUpdated: String
)

/**
 * DTO предпочтений контента
 */
data class ContentPreferencesDto(
    @SerializedName("preferredLanguages") val preferredLanguages: List<String>,
    @SerializedName("preferredSources") val preferredSources: List<String>,
    @SerializedName("excludedSources") val excludedSources: List<String>,
    @SerializedName("contentComplexity") val contentComplexity: String,
    @SerializedName("preferredLength") val preferredLength: String,
    @SerializedName("topicsOfInterest") val topicsOfInterest: List<String>,
    @SerializedName("excludedTopics") val excludedTopics: List<String>,
    @SerializedName("showRelevantOnly") val showRelevantOnly: Boolean,
    @SerializedName("enablePersonalizedRecommendations") val enablePersonalizedRecommendations: Boolean
)

/**
 * DTO настроек уведомлений
 */
data class NotificationSettingsDto(
    @SerializedName("enablePushNotifications") val enablePushNotifications: Boolean,
    @SerializedName("dailyFactTime") val dailyFactTime: String,
    @SerializedName("enableNewsNotifications") val enableNewsNotifications: Boolean,
    @SerializedName("newsNotificationFrequency") val newsNotificationFrequency: String,
    @SerializedName("enableRecommendations") val enableRecommendations: Boolean,
    @SerializedName("enableActivityReminders") val enableActivityReminders: Boolean,
    @SerializedName("quietHoursStart") val quietHoursStart: String,
    @SerializedName("quietHoursEnd") val quietHoursEnd: String,
    @SerializedName("notificationCategories") val notificationCategories: List<String>
)

/**
 * DTO настроек отображения
 */
data class DisplaySettingsDto(
    @SerializedName("theme") val theme: String,
    @SerializedName("fontSize") val fontSize: String,
    @SerializedName("enableAnimations") val enableAnimations: Boolean,
    @SerializedName("enableHapticFeedback") val enableHapticFeedback: Boolean,
    @SerializedName("compactMode") val compactMode: Boolean,
    @SerializedName("showImages") val showImages: Boolean,
    @SerializedName("autoPlayVideo") val autoPlayVideo: Boolean,
    @SerializedName("saveDataMode") val saveDataMode: Boolean
)

/**
 * DTO настроек приватности
 */
data class PrivacySettingsDto(
    @SerializedName("shareAnalytics") val shareAnalytics: Boolean,
    @SerializedName("shareReadingHistory") val shareReadingHistory: Boolean,
    @SerializedName("enablePersonalization") val enablePersonalization: Boolean,
    @SerializedName("allowLocationBasedContent") val allowLocationBasedContent: Boolean,
    @SerializedName("shareUsageStatistics") val shareUsageStatistics: Boolean
)

/**
 * DTO целей чтения
 */
data class ReadingGoalsDto(
    @SerializedName("dailyFactsGoal") val dailyFactsGoal: Int,
    @SerializedName("weeklyNewsGoal") val weeklyNewsGoal: Int,
    @SerializedName("monthlyLearningGoal") val monthlyLearningGoal: Int,
    @SerializedName("enableGoalNotifications") val enableGoalNotifications: Boolean,
    @SerializedName("currentStreak") val currentStreak: Int,
    @SerializedName("longestStreak") val longestStreak: Int
)

// СТАТИСТИКА ПОЛЬЗОВАТЕЛЯ

/**
 * DTO статистики пользователя
 */
data class UserStatsDto(
    @SerializedName("userId") val userId: String,
    @SerializedName("overallStats") val overallStats: OverallStatsDto,
    @SerializedName("readingStats") val readingStats: ReadingStatsDto,
    @SerializedName("favoriteStats") val favoriteStats: FavoriteStatsDto,
    @SerializedName("streakStats") val streakStats: StreakStatsDto,
    @SerializedName("categoryStats") val categoryStats: List<CategoryStatsDto>,
    @SerializedName("monthlyActivity") val monthlyActivity: List<MonthlyActivityDto>,
    @SerializedName("achievements") val achievements: List<AchievementDto>,
    @SerializedName("lastUpdated") val lastUpdated: String
)

/**
 * DTO общей статистики
 */
data class OverallStatsDto(
    @SerializedName("totalItemsRead") val totalItemsRead: Int,
    @SerializedName("totalTimeSpent") val totalTimeSpent: Long,
    @SerializedName("joinDate") val joinDate: String,
    @SerializedName("daysActive") val daysActive: Int,
    @SerializedName("currentLevel") val currentLevel: Int,
    @SerializedName("experiencePoints") val experiencePoints: Int,
    @SerializedName("nextLevelXp") val nextLevelXp: Int
)

/**
 * DTO статистики чтения
 */
data class ReadingStatsDto(
    @SerializedName("factsRead") val factsRead: Int,
    @SerializedName("newsRead") val newsRead: Int,
    @SerializedName("statisticsViewed") val statisticsViewed: Int,
    @SerializedName("averageReadingTime") val averageReadingTime: Double,
    @SerializedName("favoriteReadingTime") val favoriteReadingTime: String,
    @SerializedName("readingStreak") val readingStreak: Int,
    @SerializedName("totalReadingDays") val totalReadingDays: Int,
    @SerializedName("averageItemsPerDay") val averageItemsPerDay: Double
)

/**
 * DTO статистики избранного
 */
data class FavoriteStatsDto(
    @SerializedName("totalFavorites") val totalFavorites: Int,
    @SerializedName("favoritesByType") val favoritesByType: Map<String, Int>,
    @SerializedName("averageFavoritesPerMonth") val averageFavoritesPerMonth: Double,
    @SerializedName("mostFavoritedCategory") val mostFavoritedCategory: String?,
    @SerializedName("oldestFavorite") val oldestFavorite: String?,
    @SerializedName("newestFavorite") val newestFavorite: String?
)

/**
 * DTO статистики серий
 */
data class StreakStatsDto(
    @SerializedName("currentStreak") val currentStreak: Int,
    @SerializedName("longestStreak") val longestStreak: Int,
    @SerializedName("streakStartDate") val streakStartDate: String?,
    @SerializedName("totalStreaks") val totalStreaks: Int,
    @SerializedName("averageStreakLength") val averageStreakLength: Double,
    @SerializedName("streakHistory") val streakHistory: List<StreakPeriodDto>
)

/**
 * DTO периода серии
 */
data class StreakPeriodDto(
    @SerializedName("startDate") val startDate: String,
    @SerializedName("endDate") val endDate: String,
    @SerializedName("length") val length: Int
)

/**
 * DTO статистики по категориям
 */
data class CategoryStatsDto(
    @SerializedName("categoryId") val categoryId: String,
    @SerializedName("categoryName") val categoryName: String,
    @SerializedName("itemsRead") val itemsRead: Int,
    @SerializedName("timeSpent") val timeSpent: Long,
    @SerializedName("favoriteCount") val favoriteCount: Int,
    @SerializedName("lastAccessDate") val lastAccessDate: String?,
    @SerializedName("progressPercentage") val progressPercentage: Double
)

/**
 * DTO месячной активности
 */
data class MonthlyActivityDto(
    @SerializedName("month") val month: String,
    @SerializedName("itemsRead") val itemsRead: Int,
    @SerializedName("timeSpent") val timeSpent: Long,
    @SerializedName("favoritesAdded") val favoritesAdded: Int,
    @SerializedName("daysActive") val daysActive: Int,
    @SerializedName("averageItemsPerDay") val averageItemsPerDay: Double
)

/**
 * DTO достижения
 */
data class AchievementDto(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("iconUrl") val iconUrl: String?,
    @SerializedName("unlockedAt") val unlockedAt: String,
    @SerializedName("category") val category: String,
    @SerializedName("rarity") val rarity: String,
    @SerializedName("progress") val progress: Int,
    @SerializedName("maxProgress") val maxProgress: Int
)

/**
 * DTO статистики за период
 */
data class PeriodStatsDto(
    @SerializedName("startDate") val startDate: String,
    @SerializedName("endDate") val endDate: String,
    @SerializedName("itemsRead") val itemsRead: Int,
    @SerializedName("timeSpent") val timeSpent: Long,
    @SerializedName("favoritesAdded") val favoritesAdded: Int,
    @SerializedName("categoriesExplored") val categoriesExplored: List<String>,
    @SerializedName("averageSessionTime") val averageSessionTime: Double,
    @SerializedName("mostActiveDay") val mostActiveDay: String?
)

/**
 * DTO события пользователя
 */
data class UserEventDto(
    @SerializedName("eventType") val eventType: String,
    @SerializedName("contentType") val contentType: String,
    @SerializedName("contentId") val contentId: String,
    @SerializedName("duration") val duration: Long?,
    @SerializedName("metadata") val metadata: Map<String, Any>?,
    @SerializedName("timestamp") val timestamp: String
)

/**
 * DTO рекомендаций
 */
data class RecommendationsDto(
    @SerializedName("recommendedCategories") val recommendedCategories: List<String>,
    @SerializedName("suggestedContent") val suggestedContent: List<RecommendedContentDto>,
    @SerializedName("optimizedReadingTime") val optimizedReadingTime: String?,
    @SerializedName("recommendedGoals") val recommendedGoals: ReadingGoalsDto?,
    @SerializedName("analysisMetadata") val analysisMetadata: Map<String, Any>
)

/**
 * DTO рекомендуемого контента
 */
data class RecommendedContentDto(
    @SerializedName("contentId") val contentId: String,
    @SerializedName("contentType") val contentType: String,
    @SerializedName("title") val title: String,
    @SerializedName("reason") val reason: String,
    @SerializedName("confidence") val confidence: Double,
    @SerializedName("category") val category: String?
)