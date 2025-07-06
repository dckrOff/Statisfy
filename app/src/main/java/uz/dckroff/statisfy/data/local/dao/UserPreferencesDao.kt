package uz.dckroff.statisfy.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import uz.dckroff.statisfy.data.local.entities.UserPreferencesEntity

/**
 * DAO для работы с пользовательскими предпочтениями
 */
@Dao
interface UserPreferencesDao {
    
    /**
     * Получить предпочтения пользователя
     */
    @Query("SELECT * FROM user_preferences WHERE userId = :userId")
    suspend fun getUserPreferences(userId: String): UserPreferencesEntity?
    
    /**
     * Получить предпочтения пользователя как Flow
     */
    @Query("SELECT * FROM user_preferences WHERE userId = :userId")
    fun getUserPreferencesFlow(userId: String): Flow<UserPreferencesEntity?>
    
    /**
     * Вставить или обновить предпочтения
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePreferences(preferences: UserPreferencesEntity)
    
    /**
     * Обновить предпочтения
     */
    @Update
    suspend fun updatePreferences(preferences: UserPreferencesEntity)
    
    /**
     * Удалить предпочтения пользователя
     */
    @Query("DELETE FROM user_preferences WHERE userId = :userId")
    suspend fun deleteUserPreferences(userId: String)
    
    /**
     * Обновить время последнего изменения
     */
    @Query("UPDATE user_preferences SET lastUpdated = :timestamp WHERE userId = :userId")
    suspend fun updateLastModified(userId: String, timestamp: String)
    
    /**
     * Обновить тему приложения
     */
    @Query("UPDATE user_preferences SET theme = :theme WHERE userId = :userId")
    suspend fun updateTheme(userId: String, theme: String)
    
    /**
     * Обновить размер шрифта
     */
    @Query("UPDATE user_preferences SET fontSize = :fontSize WHERE userId = :userId")
    suspend fun updateFontSize(userId: String, fontSize: String)
    
    /**
     * Обновить настройки уведомлений
     */
    @Query("""
        UPDATE user_preferences 
        SET enablePushNotifications = :enablePush, 
            dailyFactTime = :dailyTime,
            enableNewsNotifications = :enableNews,
            lastUpdated = :timestamp
        WHERE userId = :userId
    """)
    suspend fun updateNotificationSettings(
        userId: String,
        enablePush: Boolean,
        dailyTime: String,
        enableNews: Boolean,
        timestamp: String
    )
    
    /**
     * Обновить настройки приватности
     */
    @Query("""
        UPDATE user_preferences 
        SET shareAnalytics = :shareAnalytics,
            shareReadingHistory = :shareHistory,
            enablePersonalization = :enablePersonalization,
            lastUpdated = :timestamp
        WHERE userId = :userId
    """)
    suspend fun updatePrivacySettings(
        userId: String,
        shareAnalytics: Boolean,
        shareHistory: Boolean,
        enablePersonalization: Boolean,
        timestamp: String
    )
    
    /**
     * Обновить интересующие категории
     */
    @Query("UPDATE user_preferences SET interestCategories = :categories, lastUpdated = :timestamp WHERE userId = :userId")
    suspend fun updateInterestCategories(userId: String, categories: List<String>, timestamp: String)
    
    /**
     * Обновить цели чтения
     */
    @Query("UPDATE user_preferences SET readingGoalsJson = :goalsJson, lastUpdated = :timestamp WHERE userId = :userId")
    suspend fun updateReadingGoals(userId: String, goalsJson: String?, timestamp: String)
    
    /**
     * Включить/выключить анимации
     */
    @Query("UPDATE user_preferences SET enableAnimations = :enable WHERE userId = :userId")
    suspend fun setAnimationsEnabled(userId: String, enable: Boolean)
    
    /**
     * Включить/выключить персонализированные рекомендации
     */
    @Query("UPDATE user_preferences SET enablePersonalizedRecommendations = :enable WHERE userId = :userId")
    suspend fun setPersonalizedRecommendationsEnabled(userId: String, enable: Boolean)
    
    /**
     * Обновить предпочтения по контенту
     */
    @Query("""
        UPDATE user_preferences 
        SET contentComplexity = :complexity,
            preferredLength = :length,
            showRelevantOnly = :relevantOnly,
            lastUpdated = :timestamp
        WHERE userId = :userId
    """)
    suspend fun updateContentPreferences(
        userId: String,
        complexity: String,
        length: String,
        relevantOnly: Boolean,
        timestamp: String
    )
    
    /**
     * Получить настройки отображения
     */
    @Query("SELECT theme, fontSize, enableAnimations, compactMode, showImages FROM user_preferences WHERE userId = :userId")
    suspend fun getDisplaySettings(userId: String): DisplaySettingsResult?
    
    /**
     * Получить настройки уведомлений
     */
    @Query("SELECT enablePushNotifications, dailyFactTime, enableNewsNotifications, enableRecommendations FROM user_preferences WHERE userId = :userId")
    suspend fun getNotificationSettings(userId: String): NotificationSettingsResult?
    
    /**
     * Проверить существование предпочтений
     */
    @Query("SELECT COUNT(*) > 0 FROM user_preferences WHERE userId = :userId")
    suspend fun preferencesExist(userId: String): Boolean
    
    /**
     * Результат для настроек отображения
     */
    data class DisplaySettingsResult(
        val theme: String,
        val fontSize: String,
        val enableAnimations: Boolean,
        val compactMode: Boolean,
        val showImages: Boolean
    )
    
    /**
     * Результат для настроек уведомлений
     */
    data class NotificationSettingsResult(
        val enablePushNotifications: Boolean,
        val dailyFactTime: String,
        val enableNewsNotifications: Boolean,
        val enableRecommendations: Boolean
    )
}