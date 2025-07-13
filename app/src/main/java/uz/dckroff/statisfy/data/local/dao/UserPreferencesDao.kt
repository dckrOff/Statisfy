package uz.dckroff.statisfy.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import uz.dckroff.statisfy.data.local.entities.UserPreferencesEntity

/**
 * DAO для работы с пользовательскими настройками
 */
@Dao
interface UserPreferencesDao {
    
    /**
     * Получить настройки по ID пользователя
     */
    @Query("SELECT * FROM user_preferences WHERE userId = :userId LIMIT 1")
    suspend fun getUserPreferences(userId: String): UserPreferencesEntity?
    
    /**
     * Получить настройки по ID пользователя как Flow
     */
    @Query("SELECT * FROM user_preferences WHERE userId = :userId LIMIT 1")
    fun getUserPreferencesFlow(userId: String): Flow<UserPreferencesEntity?>
    
    /**
     * Вставить или обновить настройки
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUserPreferences(preferences: UserPreferencesEntity)
    
    /**
     * Удалить настройки пользователя
     */
    @Query("DELETE FROM user_preferences WHERE userId = :userId")
    suspend fun deleteUserPreferences(userId: String)
    
    /**
     * Получить все настройки (для административных целей)
     */
    @Query("SELECT * FROM user_preferences")
    suspend fun getAllUserPreferences(): List<UserPreferencesEntity>
    
    /**
     * Обновить тему приложения
     */
    @Query("UPDATE user_preferences SET theme = :theme WHERE userId = :userId")
    suspend fun updateAppTheme(userId: String, theme: String)
    
    /**
     * Обновить размер шрифта
     */
    @Query("UPDATE user_preferences SET fontSize = :fontSize WHERE userId = :userId")
    suspend fun updateFontSize(userId: String, fontSize: String)
    
    /**
     * Обновить настройку уведомлений
     */
    @Query("UPDATE user_preferences SET notificationsEnabled = :enabled WHERE userId = :userId")
    suspend fun updateNotificationsEnabled(userId: String, enabled: Boolean)
}