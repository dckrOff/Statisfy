package uz.dckroff.statisfy.domain.repository

import kotlinx.coroutines.flow.Flow
import uz.dckroff.statisfy.domain.model.Fact
import uz.dckroff.statisfy.utils.Result

/**
 * Интерфейс репозитория для фактов
 */
interface FactRepository {
    
    /**
     * Получение списка фактов
     */
    suspend fun getFacts(page: Int, size: Int, categoryId: String? = null): Result<List<Fact>>
    
    /**
     * Получение факта по ID
     */
    suspend fun getFactById(id: String): Result<Fact>
    
    /**
     * Получение недавних фактов
     */
    suspend fun getRecentFacts(): Result<List<Fact>>
    
    /**
     * Получение факта дня
     */
    suspend fun getDailyFact(): Result<Fact>
    
    /**
     * Получение кэшированных фактов
     */
    fun getCachedFactsFlow(): Flow<List<Fact>>
} 