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
    @Query("SELECT * FROM favorites WHERE itemId = :itemId")
    suspend fun getFavoriteById(itemId: String): FavoriteEntity?

    /**
     * Проверить наличие элемента в избранном
     */
    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE itemId = :itemId)")
    suspend fun isFavorite(itemId: String): Boolean

    /**
     * Получить все избранные элементы
     */
    @Query("SELECT * FROM favorites")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    /**
     * Получить избранные элементы по типу
     */
    @Query("SELECT * FROM favorites WHERE itemType = :itemType")
    fun getFavoritesByType(itemType: String): Flow<List<FavoriteEntity>>
} 