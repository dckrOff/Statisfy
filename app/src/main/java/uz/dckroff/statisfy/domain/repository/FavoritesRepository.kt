package uz.dckroff.statisfy.domain.repository

import kotlinx.coroutines.flow.Flow
import uz.dckroff.statisfy.domain.model.Fact
import uz.dckroff.statisfy.domain.model.News
import uz.dckroff.statisfy.domain.model.Statistic
import uz.dckroff.statisfy.utils.Result

/**
 * Репозиторий для управления избранным контентом
 */
interface FavoritesRepository {
    
    /**
     * Добавить факт в избранное
     */
    suspend fun addToFavorites(fact: Fact): Result<Unit>
    
    /**
     * Добавить новость в избранное
     */
    suspend fun addToFavorites(news: News): Result<Unit>
    
    /**
     * Добавить статистику в избранное
     */
    suspend fun addToFavorites(statistic: Statistic): Result<Unit>
    
    /**
     * Удалить элемент из избранного
     */
    suspend fun removeFromFavorites(itemId: String): Result<Unit>
    
    /**
     * Проверить, находится ли элемент в избранном
     */
    suspend fun isFavorite(itemId: String): Boolean
    
    /**
     * Получить все избранные факты
     */
    fun getFavoriteFacts(): Flow<List<Fact>>
    
    /**
     * Получить все избранные новости
     */
    fun getFavoriteNews(): Flow<List<News>>
    
    /**
     * Получить всю избранную статистику
     */
    fun getFavoriteStatistics(): Flow<List<Statistic>>
    
    /**
     * Получить весь избранный контент
     */
    fun getAllFavorites(): Flow<Map<String, List<Any>>>
    
    /**
     * Синхронизировать избранное с сервером
     */
    suspend fun syncFavorites(): Result<Unit>
} 