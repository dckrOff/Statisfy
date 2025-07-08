package uz.dckroff.statisfy.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import uz.dckroff.statisfy.data.local.entities.FavoriteEntity

/**
 * DAO для работы с избранным в базе данных
 */
@Dao
interface FavoriteDao {

    /**
     * Добавить элемент в избранное
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    /**
     * Удалить элемент из избранного
     */
    @Delete
    suspend fun deleteFavorite(favorite: FavoriteEntity)

    /**
     * Получить элемент избранного по ID
     */
    @Query("SELECT * FROM favorites WHERE contentId = :contentId")
    suspend fun getFavoriteById(contentId: String): FavoriteEntity?

    /**
     * Проверить наличие элемента в избранном
     */
    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE contentId = :contentId)")
    suspend fun isFavorite(contentId: String): Boolean

    /**
     * Получить все избранные элементы
     */
    @Query("SELECT * FROM favorites")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    /**
     * Получить избранные элементы по типу
     */
    @Query("SELECT * FROM favorites WHERE contentType = :contentType")
    fun getFavoritesByType(contentType: String): Flow<List<FavoriteEntity>>

    /**
     * Удалить элемент избранного по ID
     */
    @Query("DELETE FROM favorites WHERE id = :id")
    suspend fun deleteFavoriteById(id: String)

    /**
     * Удалить элемент избранного по content ID
     */
    @Query("DELETE FROM favorites WHERE contentId = :contentId")
    suspend fun deleteFavoriteByContentId(contentId: String)

    /**
     * Получить общее количество избранных элементов
     */
    @Query("SELECT COUNT(*) FROM favorites")
    suspend fun getTotalCount(): Int

    /**
     * Получить недавние избранные элементы
     */
    @Query("SELECT * FROM favorites ORDER BY addedAt DESC LIMIT :limit")
    suspend fun getRecentFavorites(limit: Int): List<FavoriteEntity>

    /**
     * Поиск в избранном
     */
    @Query("SELECT * FROM favorites WHERE title LIKE '%' || :query || '%' OR contentType LIKE '%' || :query || '%'")
    suspend fun searchFavorites(query: String): List<FavoriteEntity>

    /**
     * Обновить папку для элемента избранного
     */
    @Query("UPDATE favorites SET folderId = :folderId WHERE id = :favoriteId")
    suspend fun updateFolderId(favoriteId: String, folderId: String?)

    /**
     * Получить избранные элементы в папке
     */
    @Query("SELECT * FROM favorites WHERE folderId = :folderId")
    suspend fun getFavoritesInFolder(folderId: String): List<FavoriteEntity>

    /**
     * Удалить все избранные элементы
     */
    @Query("DELETE FROM favorites")
    suspend fun deleteAllFavorites()

    /**
     * Удалить избранные элементы по типу
     */
    @Query("DELETE FROM favorites WHERE contentType = :contentType")
    suspend fun deleteFavoritesByType(contentType: String)

    /**
     * Очистить папку (удалить ссылки на папку)
     */
    @Query("UPDATE favorites SET folderId = NULL WHERE folderId = :folderId")
    suspend fun clearFolder(folderId: String)
} 