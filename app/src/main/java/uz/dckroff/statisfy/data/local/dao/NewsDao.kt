package uz.dckroff.statisfy.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import uz.dckroff.statisfy.data.local.entities.NewsEntity

/**
 * DAO для работы с новостями в базе данных
 */
@Dao
interface NewsDao {
    
    /**
     * Получить все новости
     */
    @Query("SELECT * FROM news ORDER BY publishedAt DESC")
    fun getAllNews(): Flow<List<NewsEntity>>
    
    /**
     * Получить новости с пагинацией
     */
    @Query("SELECT * FROM news ORDER BY publishedAt DESC LIMIT :limit OFFSET :offset")
    suspend fun getNewsWithPagination(limit: Int, offset: Int): List<NewsEntity>
    
    /**
     * Получить новость по ID
     */
    @Query("SELECT * FROM news WHERE id = :id")
    suspend fun getNewsById(id: String): NewsEntity?
    
    /**
     * Получить релевантные новости
     */
    @Query("SELECT * FROM news WHERE isRelevant = 1 ORDER BY publishedAt DESC")
    fun getRelevantNews(): Flow<List<NewsEntity>>
    
    /**
     * Получить новости по категории
     */
    @Query("SELECT * FROM news WHERE categoryId = :categoryId ORDER BY publishedAt DESC")
    fun getNewsByCategory(categoryId: String): Flow<List<NewsEntity>>
    
    /**
     * Поиск новостей по заголовку или содержимому
     */
    @Query("""
        SELECT * FROM news 
        WHERE title LIKE '%' || :query || '%' 
        OR summary LIKE '%' || :query || '%' 
        OR content LIKE '%' || :query || '%' 
        ORDER BY publishedAt DESC
    """)
    fun searchNews(query: String): Flow<List<NewsEntity>>
    
    /**
     * Получить избранные новости
     */
    @Query("SELECT * FROM news WHERE isFavorite = 1 ORDER BY publishedAt DESC")
    fun getFavoriteNews(): Flow<List<NewsEntity>>
    
    /**
     * Вставить новость
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(news: NewsEntity)
    
    /**
     * Вставить список новостей
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewsList(newsList: List<NewsEntity>)
    
    /**
     * Обновить новость
     */
    @Update
    suspend fun updateNews(news: NewsEntity)
    
    /**
     * Удалить новость
     */
    @Delete
    suspend fun deleteNews(news: NewsEntity)
    
    /**
     * Удалить все новости
     */
    @Query("DELETE FROM news")
    suspend fun deleteAllNews()
    
    /**
     * Пометить новость как избранную
     */
    @Query("UPDATE news SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavoriteStatus(id: String, isFavorite: Boolean)
    
    /**
     * Получить количество новостей
     */
    @Query("SELECT COUNT(*) FROM news")
    suspend fun getNewsCount(): Int
    
    /**
     * Удалить старые новости (оставить только последние N)
     */
    @Query("""
        DELETE FROM news 
        WHERE id NOT IN (
            SELECT id FROM news 
            ORDER BY publishedAt DESC 
            LIMIT :limit
        )
    """)
    suspend fun deleteOldNews(limit: Int)
}