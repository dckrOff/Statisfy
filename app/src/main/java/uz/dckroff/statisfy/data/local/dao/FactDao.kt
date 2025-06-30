package uz.dckroff.statisfy.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import uz.dckroff.statisfy.data.local.entities.FactEntity

/**
 * DAO для работы с фактами
 */
@Dao
interface FactDao {
    
    /**
     * Вставка факта
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFact(fact: FactEntity)
    
    /**
     * Вставка списка фактов
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFacts(facts: List<FactEntity>)
    
    /**
     * Получение всех фактов
     */
    @Query("SELECT * FROM facts ORDER BY createdAt DESC")
    fun getAllFactsFlow(): Flow<List<FactEntity>>
    
    /**
     * Получение факта по ID
     */
    @Query("SELECT * FROM facts WHERE id = :id")
    suspend fun getFactById(id: String): FactEntity?
    
    /**
     * Получение факта дня
     */
    @Query("SELECT * FROM facts WHERE isDaily = 1 ORDER BY createdAt DESC LIMIT 1")
    suspend fun getDailyFact(): FactEntity?
    
    /**
     * Получение недавних фактов
     */
    @Query("SELECT * FROM facts ORDER BY createdAt DESC LIMIT :limit")
    suspend fun getRecentFacts(limit: Int = 10): List<FactEntity>
    
    /**
     * Получение фактов по категории
     */
    @Query("SELECT * FROM facts WHERE categoryId = :categoryId ORDER BY createdAt DESC")
    fun getFactsByCategoryFlow(categoryId: String): Flow<List<FactEntity>>
    
    /**
     * Установка факта дня
     */
    @Transaction
    suspend fun setDailyFact(fact: FactEntity) {
        // Сначала сбрасываем все предыдущие факты дня
        resetDailyFacts()
        // Затем вставляем новый факт дня
        insertFact(fact.copy(isDaily = true))
    }
    
    /**
     * Сброс всех фактов дня
     */
    @Query("UPDATE facts SET isDaily = 0 WHERE isDaily = 1")
    suspend fun resetDailyFacts()
    
    /**
     * Удаление всех фактов
     */
    @Query("DELETE FROM facts")
    suspend fun deleteAllFacts()
} 