package uz.dckroff.statisfy.domain.repository

import kotlinx.coroutines.flow.Flow
import uz.dckroff.statisfy.domain.model.*
import uz.dckroff.statisfy.utils.Result

/**
 * Репозиторий для управления пользовательскими предпочтениями
 */
interface UserPreferencesRepository {
    
    /**
     * Получить предпочтения пользователя
     */
    suspend fun getUserPreferences(userId: String): Result<UserPreferences>
    
    /**
     * Получить предпочтения как Flow
     */
    fun getUserPreferencesFlow(userId: String): Flow<UserPreferences?>
    
    /**
     * Сохранить предпочтения пользователя
     */
    suspend fun saveUserPreferences(preferences: UserPreferences): Result<Unit>
    
    /**
     * Обновить настройки отображения
     */
    suspend fun updateDisplaySettings(userId: String, settings: DisplaySettings): Result<Unit>
    
    /**
     * Обновить настройки уведомлений
     */
    suspend fun updateNotificationSettings(userId: String, settings: NotificationSettings): Result<Unit>
    
    /**
     * Обновить настройки контента
     */
    suspend fun updateContentPreferences(userId: String, preferences: ContentPreferences): Result<Unit>
    
    /**
     * Обновить настройки приватности
     */
    suspend fun updatePrivacySettings(userId: String, settings: PrivacySettings): Result<Unit>
    
    /**
     * Обновить интересующие категории
     */
    suspend fun updateInterestCategories(userId: String, categories: List<String>): Result<Unit>
    
    /**
     * Обновить цели чтения
     */
    suspend fun updateReadingGoals(userId: String, goals: ReadingGoals?): Result<Unit>
    
    /**
     * Получить тему приложения
     */
    suspend fun getAppTheme(userId: String): AppTheme
    
    /**
     * Установить тему приложения
     */
    suspend fun setAppTheme(userId: String, theme: AppTheme): Result<Unit>
    
    /**
     * Получить размер шрифта
     */
    suspend fun getFontSize(userId: String): FontSize
    
    /**
     * Установить размер шрифта
     */
    suspend fun setFontSize(userId: String, size: FontSize): Result<Unit>
    
    /**
     * Проверить, включены ли уведомления
     */
    suspend fun areNotificationsEnabled(userId: String): Boolean
    
    /**
     * Включить/выключить уведомления
     */
    suspend fun setNotificationsEnabled(userId: String, enabled: Boolean): Result<Unit>
    
    /**
     * Получить время ежедневного факта
     */
    suspend fun getDailyFactTime(userId: String): String
    
    /**
     * Установить время ежедневного факта
     */
    suspend fun setDailyFactTime(userId: String, time: String): Result<Unit>
    
    /**
     * Проверить, включена ли персонализация
     */
    suspend fun isPersonalizationEnabled(userId: String): Boolean
    
    /**
     * Включить/выключить персонализацию
     */
    suspend fun setPersonalizationEnabled(userId: String, enabled: Boolean): Result<Unit>
    
    /**
     * Получить предпочитаемые источники
     */
    suspend fun getPreferredSources(userId: String): List<String>
    
    /**
     * Обновить предпочитаемые источники
     */
    suspend fun updatePreferredSources(userId: String, sources: List<String>): Result<Unit>
    
    /**
     * Получить исключенные источники
     */
    suspend fun getExcludedSources(userId: String): List<String>
    
    /**
     * Обновить исключенные источники
     */
    suspend fun updateExcludedSources(userId: String, sources: List<String>): Result<Unit>
    
    /**
     * Синхронизировать предпочтения с сервером
     */
    suspend fun syncPreferences(userId: String): Result<Unit>
    
    /**
     * Сбросить предпочтения к значениям по умолчанию
     */
    suspend fun resetToDefaults(userId: String): Result<Unit>
    
    /**
     * Экспортировать предпочтения
     */
    suspend fun exportPreferences(userId: String): Result<String>
    
    /**
     * Импортировать предпочтения
     */
    suspend fun importPreferences(userId: String, preferencesData: String): Result<Unit>
    
    /**
     * Удалить все предпочтения пользователя
     */
    suspend fun deleteUserPreferences(userId: String): Result<Unit>
}