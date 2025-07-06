package uz.dckroff.statisfy.domain.model

/**
 * Статистика активности пользователя
 */
data class UserStats(
    val userId: String,
    val overallStats: OverallStats,
    val readingStats: ReadingStats,
    val favoriteStats: FavoriteStats,
    val streakStats: StreakStats,
    val categoryStats: List<CategoryStats>,
    val monthlyActivity: List<MonthlyActivity>,
    val achievements: List<Achievement>,
    val lastUpdated: String
)

/**
 * Общая статистика
 */
data class OverallStats(
    val totalItemsRead: Int = 0,
    val totalTimeSpent: Long = 0, // в минутах
    val joinDate: String,
    val daysActive: Int = 0,
    val currentLevel: Int = 1,
    val experiencePoints: Int = 0,
    val nextLevelXp: Int = 100
)

/**
 * Статистика чтения
 */
data class ReadingStats(
    val factsRead: Int = 0,
    val newsRead: Int = 0,
    val statisticsViewed: Int = 0,
    val averageReadingTime: Double = 0.0, // в минутах
    val favoriteReadingTime: String = "morning", // утро, день, вечер
    val readingStreak: Int = 0,
    val totalReadingDays: Int = 0,
    val averageItemsPerDay: Double = 0.0
)

/**
 * Статистика избранного
 */
data class FavoriteStats(
    val totalFavorites: Int = 0,
    val favoritesByType: Map<ContentType, Int> = emptyMap(),
    val averageFavoritesPerMonth: Double = 0.0,
    val mostFavoritedCategory: String? = null,
    val oldestFavorite: String? = null,
    val newestFavorite: String? = null
)

/**
 * Статистика серий (streak)
 */
data class StreakStats(
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val streakStartDate: String? = null,
    val totalStreaks: Int = 0,
    val averageStreakLength: Double = 0.0,
    val streakHistory: List<StreakPeriod> = emptyList()
)

/**
 * Период серии активности
 */
data class StreakPeriod(
    val startDate: String,
    val endDate: String,
    val length: Int
)

/**
 * Статистика по категориям
 */
data class CategoryStats(
    val categoryId: String,
    val categoryName: String,
    val itemsRead: Int = 0,
    val timeSpent: Long = 0, // в минутах
    val favoriteCount: Int = 0,
    val lastAccessDate: String? = null,
    val progressPercentage: Double = 0.0
)

/**
 * Месячная активность
 */
data class MonthlyActivity(
    val month: String, // YYYY-MM формат
    val itemsRead: Int = 0,
    val timeSpent: Long = 0,
    val favoritesAdded: Int = 0,
    val daysActive: Int = 0,
    val averageItemsPerDay: Double = 0.0
)

/**
 * Достижения пользователя
 */
data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val iconUrl: String? = null,
    val unlockedAt: String,
    val category: AchievementCategory,
    val rarity: AchievementRarity,
    val progress: Int = 100, // процент выполнения
    val maxProgress: Int = 100
)

/**
 * Категории достижений
 */
enum class AchievementCategory(val displayName: String) {
    READING("Чтение"),
    FAVORITES("Избранное"),
    STREAK("Серии"),
    EXPLORATION("Исследование"),
    SOCIAL("Социальные"),
    TIME("Время"),
    KNOWLEDGE("Знания")
}

/**
 * Редкость достижений
 */
enum class AchievementRarity(val displayName: String, val color: String) {
    COMMON("Обычное", "#8BC34A"),
    UNCOMMON("Необычное", "#2196F3"),
    RARE("Редкое", "#9C27B0"),
    EPIC("Эпическое", "#FF9800"),
    LEGENDARY("Легендарное", "#F44336")
}

/**
 * Рекомендации для пользователя
 */
data class PersonalizedRecommendations(
    val recommendedCategories: List<String> = emptyList(),
    val suggestedContent: List<RecommendedContent> = emptyList(),
    val optimizedReadingTime: String? = null,
    val recommendedGoals: ReadingGoals? = null,
    val basedOnAnalysis: RecommendationAnalysis
)

/**
 * Рекомендуемый контент
 */
data class RecommendedContent(
    val contentId: String,
    val contentType: ContentType,
    val title: String,
    val reason: String, // почему рекомендуется
    val confidence: Double, // уверенность в рекомендации (0.0 - 1.0)
    val category: Category? = null
)

/**
 * Анализ для рекомендаций
 */
data class RecommendationAnalysis(
    val readingPatterns: Map<String, Any> = emptyMap(),
    val preferredTopics: List<String> = emptyList(),
    val optimalReadingTimes: List<String> = emptyList(),
    val engagementFactors: Map<String, Double> = emptyMap(),
    val lastAnalysisDate: String
)