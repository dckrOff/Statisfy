package uz.dckroff.statisfy.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import uz.dckroff.statisfy.data.local.entities.FavoriteEntity

/**
 * DAO для работы с избранным контентом
 */
@Dao
interface FavoritesDao {
    
    // CRUD Operations
    
    /**
     * Получить все избранные элементы
     */
    @Query("SELECT * FROM favorites WHERE isArchived = 0 ORDER BY addedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>
    
    /**
     * Получить избранный элемент по ID
     */
    @Query("SELECT * FROM favorites WHERE id = :favoriteId")
    suspend fun getFavoriteById(favoriteId: String): FavoriteEntity?
    
    /**
     * Проверить, находится ли элемент в избранном
     */
    @Query("SELECT COUNT(*) > 0 FROM favorites WHERE contentId = :contentId AND contentType = :contentType")
    suspend fun isFavorite(contentId: String, contentType: String): Boolean
    
    /**
     * Вставить избранный элемент
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)
    
    /**
     * Вставить список избранных
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorites(favorites: List<FavoriteEntity>)
    
    /**
     * Обновить избранный элемент
     */
    @Update
    suspend fun updateFavorite(favorite: FavoriteEntity)
    
    /**
     * Удалить избранный элемент
     */
    @Delete
    suspend fun deleteFavorite(favorite: FavoriteEntity)
    
    /**
     * Удалить по content ID
     */
    @Query("DELETE FROM favorites WHERE contentId = :contentId AND contentType = :contentType")
    suspend fun deleteFavoriteByContentId(contentId: String, contentType: String)
    
    // Content Type Filtering
    
    /**
     * Получить избранные факты
     */
    @Query("SELECT * FROM favorites WHERE contentType = 'FACT' AND isArchived = 0 ORDER BY addedAt DESC")
    fun getFavoriteFacts(): Flow<List<FavoriteEntity>>
    
    /**
     * Получить избранные новости
     */
    @Query("SELECT * FROM favorites WHERE contentType = 'NEWS' AND isArchived = 0 ORDER BY addedAt DESC")
    fun getFavoriteNews(): Flow<List<FavoriteEntity>>
    
    /**
     * Получить избранную статистику
     */
    @Query("SELECT * FROM favorites WHERE contentType = 'STATISTIC' AND isArchived = 0 ORDER BY addedAt DESC")
    fun getFavoriteStatistics(): Flow<List<FavoriteEntity>>
    
    /**
     * Получить избранные по типу
     */
    @Query("SELECT * FROM favorites WHERE contentType = :contentType AND isArchived = 0 ORDER BY addedAt DESC")
    fun getFavoritesByType(contentType: String): Flow<List<FavoriteEntity>>
    
    // Folder Management
    
    /**
     * Получить избранные из папки
     */
    @Query("SELECT * FROM favorites WHERE folderId = :folderId AND isArchived = 0 ORDER BY sortOrder ASC, addedAt DESC")
    fun getFavoritesInFolder(folderId: String): Flow<List<FavoriteEntity>>
    
    /**
     * Получить избранные без папки
     */
    @Query("SELECT * FROM favorites WHERE folderId IS NULL AND isArchived = 0 ORDER BY addedAt DESC")
    fun getFavoritesWithoutFolder(): Flow<List<FavoriteEntity>>
    
    /**
     * Переместить в папку
     */
    @Query("UPDATE favorites SET folderId = :folderId WHERE id = :favoriteId")
    suspend fun moveToFolder(favoriteId: String, folderId: String?)
    
    /**
     * Переместить элементы в папку
     */
    @Query("UPDATE favorites SET folderId = :folderId WHERE id IN (:favoriteIds)")
    suspend fun moveItemsToFolder(favoriteIds: List<String>, folderId: String?)
    
    // Search and Filtering
    
    /**
     * Поиск в избранном
     */
    @Query("""
        SELECT * FROM favorites 
        WHERE (title LIKE '%' || :query || '%' OR summary LIKE '%' || :query || '%') 
        AND isArchived = 0
        ORDER BY addedAt DESC
    """)
    suspend fun searchFavorites(query: String): List<FavoriteEntity>
    
    /**
     * Фильтрация по категории
     */
    @Query("SELECT * FROM favorites WHERE categoryId = :categoryId AND isArchived = 0 ORDER BY addedAt DESC")
    suspend fun getFavoritesByCategory(categoryId: String): List<FavoriteEntity>
    
    /**
     * Фильтрация по тегам
     */
    @Query("SELECT * FROM favorites WHERE tags LIKE '%' || :tag || '%' AND isArchived = 0 ORDER BY addedAt DESC")
    suspend fun getFavoritesByTag(tag: String): List<FavoriteEntity>
    
    /**
     * Получить избранные за период
     */
    @Query("SELECT * FROM favorites WHERE addedAt BETWEEN :startDate AND :endDate AND isArchived = 0 ORDER BY addedAt DESC")
    suspend fun getFavoritesInDateRange(startDate: String, endDate: String): List<FavoriteEntity>
    
    // Statistics
    
    /**
     * Получить общее количество избранных
     */
    @Query("SELECT COUNT(*) FROM favorites WHERE isArchived = 0")
    suspend fun getTotalFavoritesCount(): Int
    
    /**
     * Получить количество по типам
     */
    @Query("SELECT contentType, COUNT(*) as count FROM favorites WHERE isArchived = 0 GROUP BY contentType")
    suspend fun getCountByContentType(): List<ContentTypeCount>
    
    /**
     * Получить количество в папке
     */
    @Query("SELECT COUNT(*) FROM favorites WHERE folderId = :folderId AND isArchived = 0")
    suspend fun getCountInFolder(folderId: String): Int
    
    /**
     * Получить самые недавние избранные
     */
    @Query("SELECT * FROM favorites WHERE isArchived = 0 ORDER BY addedAt DESC LIMIT :limit")
    suspend fun getRecentFavorites(limit: Int = 10): List<FavoriteEntity>
    
    /**
     * Получить самые просматриваемые
     */
    @Query("SELECT * FROM favorites WHERE isArchived = 0 ORDER BY viewCount DESC LIMIT :limit")
    suspend fun getMostViewedFavorites(limit: Int = 10): List<FavoriteEntity>
    
    // View Tracking
    
    /**
     * Увеличить счетчик просмотров
     */
    @Query("UPDATE favorites SET viewCount = viewCount + 1, lastViewedAt = :timestamp WHERE id = :favoriteId")
    suspend fun incrementViewCount(favoriteId: String, timestamp: String)
    
    /**
     * Обновить время последнего просмотра
     */
    @Query("UPDATE favorites SET lastViewedAt = :timestamp WHERE id = :favoriteId")
    suspend fun updateLastViewed(favoriteId: String, timestamp: String)
    
    // Archive Management
    
    /**
     * Архивировать элемент
     */
    @Query("UPDATE favorites SET isArchived = 1 WHERE id = :favoriteId")
    suspend fun archiveFavorite(favoriteId: String)
    
    /**
     * Восстановить из архива
     */
    @Query("UPDATE favorites SET isArchived = 0 WHERE id = :favoriteId")
    suspend fun unarchiveFavorite(favoriteId: String)
    
    /**
     * Получить архивированные элементы
     */
    @Query("SELECT * FROM favorites WHERE isArchived = 1 ORDER BY addedAt DESC")
    fun getArchivedFavorites(): Flow<List<FavoriteEntity>>
    
    // Bulk Operations
    
    /**
     * Удалить все избранные
     */
    @Query("DELETE FROM favorites")
    suspend fun deleteAllFavorites()
    
    /**
     * Удалить избранные по типу
     */
    @Query("DELETE FROM favorites WHERE contentType = :contentType")
    suspend fun deleteFavoritesByType(contentType: String)
    
    /**
     * Удалить старые избранные
     */
    @Query("DELETE FROM favorites WHERE addedAt < :cutoffDate")
    suspend fun deleteOldFavorites(cutoffDate: String)
    
    /**
     * Обновить теги для элемента
     */
    @Query("UPDATE favorites SET tags = :tags WHERE id = :favoriteId")
    suspend fun updateTags(favoriteId: String, tags: List<String>)
    
    /**
     * Результат подсчета по типам контента
     */
    data class ContentTypeCount(
        val contentType: String,
        val count: Int
    )
}