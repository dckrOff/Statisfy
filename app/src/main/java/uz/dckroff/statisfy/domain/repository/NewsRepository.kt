package uz.dckroff.statisfy.domain.repository

import kotlinx.coroutines.flow.Flow
import uz.dckroff.statisfy.domain.model.News
import uz.dckroff.statisfy.domain.model.NewsData
import uz.dckroff.statisfy.utils.Resource

/**
 * Репозиторий для работы с новостями
 */
interface NewsRepository : BaseRepository {
    
    /**
     * Получить список новостей с пагинацией
     */
    suspend fun getNews(
        page: Int = 1,
        limit: Int = 10,
        category: String? = null,
        search: String? = null,
        sort: String = "date_desc"
    ): Resource<NewsData>
    
    /**
     * Получить новость по ID
     */
    suspend fun getNewsById(id: String): Resource<News>
    
    /**
     * Получить релевантные новости
     */
    suspend fun getRelevantNews(limit: Int = 10): Resource<List<News>>
    
    /**
     * Получить новости по категории
     */
    suspend fun getNewsByCategory(
        categoryId: String,
        page: Int = 1,
        limit: Int = 10
    ): Resource<NewsData>
    
    /**
     * Поиск новостей
     */
    suspend fun searchNews(
        query: String,
        page: Int = 1,
        limit: Int = 10,
        category: String? = null
    ): Resource<NewsData>
    
    /**
     * Получить популярные новости
     */
    suspend fun getPopularNews(
        limit: Int = 10,
        period: String = "week"
    ): Resource<List<News>>
    
    /**
     * Получить все новости из локальной БД
     */
    fun getAllNewsFromLocal(): Flow<List<News>>
    
    /**
     * Получить избранные новости
     */
    fun getFavoriteNews(): Flow<List<News>>
    
    /**
     * Поиск новостей локально
     */
    fun searchNewsLocal(query: String): Flow<List<News>>
    
    /**
     * Получить новости по категории локально
     */
    fun getNewsByCategoryLocal(categoryId: String): Flow<List<News>>
    
    /**
     * Добавить/убрать из избранного
     */
    suspend fun toggleFavorite(newsId: String): Resource<Boolean>
    
    /**
     * Отметить новость как прочитанную
     */
    suspend fun markAsRead(newsId: String): Resource<Unit>
    
    /**
     * Синхронизировать новости с сервером
     */
    suspend fun syncNews(): Resource<Unit>
    
    /**
     * Очистить кэш новостей
     */
    suspend fun clearCache(): Resource<Unit>
}