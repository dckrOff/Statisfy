package uz.dckroff.statisfy.presentation.ui.statistics

import uz.dckroff.statisfy.domain.model.AchievementRarity

/**
 * Элементы для отображения в списке статистики
 */
sealed class StatisticsItem {
    
    /**
     * Заголовок секции
     */
    data class Header(
        val title: String
    ) : StatisticsItem()
    
    /**
     * Статистика по категории
     */
    data class Category(
        val id: String,
        val title: String,
        val itemsRead: Int,
        val timeSpent: Long,
        val progressPercentage: Double
    ) : StatisticsItem()
    
    /**
     * Месячная активность
     */
    data class Monthly(
        val month: String,
        val itemsRead: Int,
        val timeSpent: Long,
        val daysActive: Int
    ) : StatisticsItem()
    
    /**
     * Достижение
     */
    data class Achievement(
        val id: String,
        val title: String,
        val description: String,
        val iconUrl: String?,
        val unlockedAt: String,
        val rarity: AchievementRarity
    ) : StatisticsItem()
    
    /**
     * Общая статистика
     */
    data class Overall(
        val totalItemsRead: Int,
        val totalTimeSpent: Long,
        val currentLevel: Int,
        val daysActive: Int,
        val experiencePoints: Int,
        val nextLevelXp: Int
    ) : StatisticsItem()
    
    /**
     * Статистика чтения
     */
    data class Reading(
        val factsRead: Int,
        val newsRead: Int,
        val averageReadingTime: Double,
        val readingStreak: Int
    ) : StatisticsItem()
    
    /**
     * Статистика серий
     */
    data class Streak(
        val currentStreak: Int,
        val longestStreak: Int,
        val totalStreaks: Int,
        val averageStreakLength: Double
    ) : StatisticsItem()
}