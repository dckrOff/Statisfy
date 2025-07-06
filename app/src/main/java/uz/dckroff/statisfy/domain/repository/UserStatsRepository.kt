package uz.dckroff.statisfy.domain.repository

import kotlinx.coroutines.flow.Flow
import uz.dckroff.statisfy.domain.model.*
import uz.dckroff.statisfy.utils.Result

/**
 * Репозиторий для работы со статистикой пользователя
 */
interface UserStatsRepository {
    
    /**
     * Получить статистику пользователя
     */
    suspend fun getUserStats(userId: String): Result<UserStats>
    
    /**
     * Получить статистику как Flow
     */
    fun getUserStatsFlow(userId: String): Flow<UserStats?>
    
    /**
     * Обновить статистику пользователя
     */
    suspend fun updateUserStats(stats: UserStats): Result<Unit>
    
    /**
     * Записать событие чтения
     */
    suspend fun recordReadingEvent(
        userId: String,
        contentType: ContentType,
        contentId: String,
        readingTime: Long // в миллисекундах
    ): Result<Unit>
    
    /**
     * Записать добавление в избранное
     */
    suspend fun recordFavoriteEvent(
        userId: String,
        contentType: ContentType,
        contentId: String
    ): Result<Unit>
    
    /**
     * Записать удаление из избранного
     */
    suspend fun recordUnfavoriteEvent(
        userId: String,
        contentType: ContentType,
        contentId: String
    ): Result<Unit>
    
    /**
     * Получить общую статистику
     */
    suspend fun getOverallStats(userId: String): Result<OverallStats>
    
    /**
     * Получить статистику чтения
     */
    suspend fun getReadingStats(userId: String): Result<ReadingStats>
    
    /**
     * Получить статистику избранного
     */
    suspend fun getFavoriteStats(userId: String): Result<FavoriteStats>
    
    /**
     * Получить статистику серий
     */
    suspend fun getStreakStats(userId: String): Result<StreakStats>
    
    /**
     * Обновить серию активности
     */
    suspend fun updateStreak(userId: String): Result<Unit>
    
    /**
     * Прервать серию
     */
    suspend fun breakStreak(userId: String): Result<Unit>
    
    /**
     * Получить статистику по категориям
     */
    suspend fun getCategoryStats(userId: String): Result<List<CategoryStats>>
    
    /**
     * Получить месячную активность
     */
    suspend fun getMonthlyActivity(userId: String, monthsBack: Int = 12): Result<List<MonthlyActivity>>
    
    /**
     * Получить достижения пользователя
     */
    suspend fun getUserAchievements(userId: String): Result<List<Achievement>>
    
    /**
     * Разблокировать достижение
     */
    suspend fun unlockAchievement(userId: String, achievementId: String): Result<Unit>
    
    /**
     * Проверить новые достижения
     */
    suspend fun checkNewAchievements(userId: String): Result<List<Achievement>>
    
    /**
     * Получить персонализированные рекомендации
     */
    suspend fun getPersonalizedRecommendations(userId: String): Result<PersonalizedRecommendations>
    
    /**
     * Записать взаимодействие с контентом
     */
    suspend fun recordInteraction(
        userId: String,
        contentType: ContentType,
        contentId: String,
        interactionType: InteractionType,
        duration: Long? = null
    ): Result<Unit>
    
    /**
     * Получить время оптимального чтения
     */
    suspend fun getOptimalReadingTime(userId: String): Result<String>
    
    /**
     * Получить предпочитаемые категории на основе активности
     */
    suspend fun getPreferredCategories(userId: String): Result<List<String>>
    
    /**
     * Получить статистику за определенный период
     */
    suspend fun getStatsForPeriod(
        userId: String,
        startDate: String,
        endDate: String
    ): Result<PeriodStats>
    
    /**
     * Сравнить статистику с другими пользователями
     */
    suspend fun compareWithAverage(userId: String): Result<ComparisonStats>
    
    /**
     * Экспортировать статистику
     */
    suspend fun exportStats(userId: String): Result<String>
    
    /**
     * Синхронизировать статистику с сервером
     */
    suspend fun syncStats(userId: String): Result<Unit>
    
    /**
     * Сбросить статистику
     */
    suspend fun resetStats(userId: String): Result<Unit>
    
    /**
     * Удалить статистику пользователя
     */
    suspend fun deleteUserStats(userId: String): Result<Unit>
}

/**
 * Типы взаимодействия с контентом
 */
enum class InteractionType {
    VIEW,           // Просмотр
    READ,           // Чтение
    SHARE,          // Поделиться
    FAVORITE,       // Добавить в избранное
    UNFAVORITE,     // Удалить из избранного
    SEARCH,         // Поиск
    FILTER,         // Фильтрация
    COMMENT,        // Комментарий
    RATE            // Оценка
}

/**
 * Статистика за период
 */
data class PeriodStats(
    val startDate: String,
    val endDate: String,
    val itemsRead: Int,
    val timeSpent: Long,
    val favoritesAdded: Int,
    val categoriesExplored: Set<String>,
    val averageSessionTime: Double,
    val mostActiveDay: String?
)

/**
 * Сравнительная статистика
 */
data class ComparisonStats(
    val userValue: Double,
    val averageValue: Double,
    val percentile: Double, // В каком процентиле находится пользователь
    val comparison: ComparisonResult
)

/**
 * Результат сравнения
 */
enum class ComparisonResult {
    ABOVE_AVERAGE,
    AVERAGE,
    BELOW_AVERAGE
}