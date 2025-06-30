package uz.dckroff.statisfy.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import uz.dckroff.statisfy.data.local.entities.CategoryEntity

/**
 * DAO для работы с категориями
 */
@Dao
interface CategoryDao {
    
    /**
     * Вставка категории
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)
    
    /**
     * Вставка списка категорий
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)
    
    /**
     * Получение всех категорий
     */
    @Query("SELECT * FROM categories ORDER BY name")
    fun getAllCategoriesFlow(): Flow<List<CategoryEntity>>
    
    /**
     * Получение категории по ID
     */
    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: String): CategoryEntity?
    
    /**
     * Удаление всех категорий
     */
    @Query("DELETE FROM categories")
    suspend fun deleteAllCategories()
} 