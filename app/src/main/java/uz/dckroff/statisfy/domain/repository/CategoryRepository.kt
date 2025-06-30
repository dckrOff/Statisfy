package uz.dckroff.statisfy.domain.repository

import kotlinx.coroutines.flow.Flow
import uz.dckroff.statisfy.domain.model.Category
import uz.dckroff.statisfy.utils.Result

/**
 * Интерфейс репозитория для категорий
 */
interface CategoryRepository {
    
    /**
     * Получение списка категорий
     */
    suspend fun getCategories(): Result<List<Category>>
    
    /**
     * Получение кэшированных категорий
     */
    fun getCachedCategoriesFlow(): Flow<List<Category>>
} 