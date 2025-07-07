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
} 