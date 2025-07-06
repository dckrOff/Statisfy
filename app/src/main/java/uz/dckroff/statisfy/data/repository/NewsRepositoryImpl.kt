package uz.dckroff.statisfy.data.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import uz.dckroff.statisfy.data.local.dao.CategoryDao
import uz.dckroff.statisfy.data.local.dao.NewsDao
import uz.dckroff.statisfy.data.local.entities.NewsEntity
import uz.dckroff.statisfy.data.remote.api.NewsApi
import uz.dckroff.statisfy.domain.model.News
import uz.dckroff.statisfy.domain.model.NewsData
import uz.dckroff.statisfy.domain.repository.NewsRepository
import uz.dckroff.statisfy.utils.Resource
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Реализация репозитория для работы с новостями
 */
@Singleton
class NewsRepositoryImpl @Inject constructor(
    private val newsApi: NewsApi,
    private val newsDao: NewsDao,
    private val categoryDao: CategoryDao
) : NewsRepository {
    
    companion object {
        private const val TAG = "NewsRepositoryImpl"
        private const val DEFAULT_CACHE_SIZE = 100
    }
    
    /**
     * Получить список новостей с пагинацией
     */
    override suspend fun getNews(
        page: Int,
        limit: Int,
        category: String?,
        search: String?,
        sort: String
    ): Resource<NewsData> {
        return try {
            val response = newsApi.getNews(page, limit, category, search, sort)
            
            if (response.isSuccessful) {
                val newsListResponse = response.body()
                if (newsListResponse != null) {
                    val newsList = newsListResponse.data.map { it.toDomainModel() }
                    
                    // Сохранить в локальную БД для кэширования
                    saveNewsToLocal(newsList)
                    
                    val newsData = NewsData(
                        news = newsList,
                        totalPages = newsListResponse.pagination.totalPages,
                        currentPage = newsListResponse.pagination.currentPage
                    )
                    
                    Resource.Success(newsData)
                } else {
                    Resource.Error("Пустой ответ от сервера")
                }
            } else {
                Log.e(TAG, "Ошибка получения новостей: ${response.code()}")
                
                // Попытаться получить из локальной БД
                val localNews = getNewsFromLocal(page, limit)
                if (localNews.isNotEmpty()) {
                    Resource.Success(NewsData(news = localNews))
                } else {
                    Resource.Error("Ошибка загрузки новостей")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Исключение при получении новостей", e)
            
            // Попытаться получить из локальной БД
            val localNews = getNewsFromLocal(page, limit)
            if (localNews.isNotEmpty()) {
                Resource.Success(NewsData(news = localNews))
            } else {
                Resource.Error("Нет подключения к интернету")
            }
        }
    }
    
    /**
     * Получить новость по ID
     */
    override suspend fun getNewsById(id: String): Resource<News> {
        return try {
            val response = newsApi.getNewsById(id)
            
            if (response.isSuccessful) {
                val newsDto = response.body()
                if (newsDto != null) {
                    val news = newsDto.toDomainModel()
                    
                    // Сохранить в локальную БД
                    saveNewsToLocal(listOf(news))
                    
                    Resource.Success(news)
                } else {
                    Resource.Error("Новость не найдена")
                }
            } else {
                // Попытаться получить из локальной БД
                val localNews = getNewsFromLocalById(id)
                if (localNews != null) {
                    Resource.Success(localNews)
                } else {
                    Resource.Error("Новость не найдена")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Исключение при получении новости", e)
            
            // Попытаться получить из локальной БД
            val localNews = getNewsFromLocalById(id)
            if (localNews != null) {
                Resource.Success(localNews)
            } else {
                Resource.Error("Ошибка загрузки новости")
            }
        }
    }
    
    /**
     * Получить релевантные новости
     */
    override suspend fun getRelevantNews(limit: Int): Resource<List<News>> {
        return try {
            val response = newsApi.getRelevantNews(limit)
            
            if (response.isSuccessful) {
                val newsListResponse = response.body()
                if (newsListResponse != null) {
                    val newsList = newsListResponse.data.map { it.toDomainModel() }
                    
                    // Сохранить в локальную БД
                    saveNewsToLocal(newsList)
                    
                    Resource.Success(newsList)
                } else {
                    Resource.Error("Пустой ответ от сервера")
                }
            } else {
                Resource.Error("Ошибка загрузки релевантных новостей")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Исключение при получении релевантных новостей", e)
            Resource.Error("Ошибка загрузки новостей")
        }
    }
    
    /**
     * Получить новости по категории
     */
    override suspend fun getNewsByCategory(
        categoryId: String,
        page: Int,
        limit: Int
    ): Resource<NewsData> {
        return try {
            val response = newsApi.getNewsByCategory(categoryId, page, limit)
            
            if (response.isSuccessful) {
                val newsListResponse = response.body()
                if (newsListResponse != null) {
                    val newsList = newsListResponse.data.map { it.toDomainModel() }
                    
                    // Сохранить в локальную БД
                    saveNewsToLocal(newsList)
                    
                    val newsData = NewsData(
                        news = newsList,
                        totalPages = newsListResponse.pagination.totalPages,
                        currentPage = newsListResponse.pagination.currentPage
                    )
                    
                    Resource.Success(newsData)
                } else {
                    Resource.Error("Пустой ответ от сервера")
                }
            } else {
                Resource.Error("Ошибка загрузки новостей по категории")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Исключение при получении новостей по категории", e)
            Resource.Error("Ошибка загрузки новостей")
        }
    }
    
    /**
     * Поиск новостей
     */
    override suspend fun searchNews(
        query: String,
        page: Int,
        limit: Int,
        category: String?
    ): Resource<NewsData> {
        return try {
            val response = newsApi.searchNews(query, page, limit, category)
            
            if (response.isSuccessful) {
                val newsListResponse = response.body()
                if (newsListResponse != null) {
                    val newsList = newsListResponse.data.map { it.toDomainModel() }
                    
                    val newsData = NewsData(
                        news = newsList,
                        totalPages = newsListResponse.pagination.totalPages,
                        currentPage = newsListResponse.pagination.currentPage
                    )
                    
                    Resource.Success(newsData)
                } else {
                    Resource.Error("Пустой ответ от сервера")
                }
            } else {
                Resource.Error("Ошибка поиска новостей")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Исключение при поиске новостей", e)
            Resource.Error("Ошибка поиска новостей")
        }
    }
    
    /**
     * Получить популярные новости
     */
    override suspend fun getPopularNews(limit: Int, period: String): Resource<List<News>> {
        return try {
            val response = newsApi.getPopularNews(limit, period)
            
            if (response.isSuccessful) {
                val newsListResponse = response.body()
                if (newsListResponse != null) {
                    val newsList = newsListResponse.data.map { it.toDomainModel() }
                    
                    // Сохранить в локальную БД
                    saveNewsToLocal(newsList)
                    
                    Resource.Success(newsList)
                } else {
                    Resource.Error("Пустой ответ от сервера")
                }
            } else {
                Resource.Error("Ошибка загрузки популярных новостей")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Исключение при получении популярных новостей", e)
            Resource.Error("Ошибка загрузки новостей")
        }
    }
    
    /**
     * Получить все новости из локальной БД
     */
    override fun getAllNewsFromLocal(): Flow<List<News>> {
        return newsDao.getAllNews().map { entities ->
            entities.map { entity ->
                val category = categoryDao.getCategoryById(entity.categoryId)
                    ?.toDomainModel()
                    ?: uz.dckroff.statisfy.domain.model.Category("", "Неизвестно")
                entity.toDomainModel(category)
            }
        }
    }
    
    /**
     * Получить избранные новости
     */
    override fun getFavoriteNews(): Flow<List<News>> {
        return newsDao.getFavoriteNews().map { entities ->
            entities.map { entity ->
                val category = categoryDao.getCategoryById(entity.categoryId)
                    ?.toDomainModel()
                    ?: uz.dckroff.statisfy.domain.model.Category("", "Неизвестно")
                entity.toDomainModel(category)
            }
        }
    }
    
    /**
     * Поиск новостей локально
     */
    override fun searchNewsLocal(query: String): Flow<List<News>> {
        return newsDao.searchNews(query).map { entities ->
            entities.map { entity ->
                val category = categoryDao.getCategoryById(entity.categoryId)
                    ?.toDomainModel()
                    ?: uz.dckroff.statisfy.domain.model.Category("", "Неизвестно")
                entity.toDomainModel(category)
            }
        }
    }
    
    /**
     * Получить новости по категории локально
     */
    override fun getNewsByCategoryLocal(categoryId: String): Flow<List<News>> {
        return newsDao.getNewsByCategory(categoryId).map { entities ->
            entities.map { entity ->
                val category = categoryDao.getCategoryById(entity.categoryId)
                    ?.toDomainModel()
                    ?: uz.dckroff.statisfy.domain.model.Category("", "Неизвестно")
                entity.toDomainModel(category)
            }
        }
    }
    
    /**
     * Добавить/убрать из избранного
     */
    override suspend fun toggleFavorite(newsId: String): Resource<Boolean> {
        return try {
            val newsEntity = newsDao.getNewsById(newsId)
            if (newsEntity != null) {
                val newFavoriteStatus = !newsEntity.isFavorite
                newsDao.setFavoriteStatus(newsId, newFavoriteStatus)
                Resource.Success(newFavoriteStatus)
            } else {
                Resource.Error("Новость не найдена")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при изменении статуса избранного", e)
            Resource.Error("Ошибка изменения статуса")
        }
    }
    
    /**
     * Отметить новость как прочитанную
     */
    override suspend fun markAsRead(newsId: String): Resource<Unit> {
        return try {
            newsApi.markAsRead(newsId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при отметке новости как прочитанной", e)
            Resource.Error("Ошибка отметки новости")
        }
    }
    
    /**
     * Синхронизировать новости с сервером
     */
    override suspend fun syncNews(): Resource<Unit> {
        return try {
            val response = newsApi.getNews(page = 1, limit = DEFAULT_CACHE_SIZE)
            
            if (response.isSuccessful) {
                val newsListResponse = response.body()
                if (newsListResponse != null) {
                    val newsList = newsListResponse.data.map { it.toDomainModel() }
                    
                    // Очистить старые данные и сохранить новые
                    newsDao.deleteAllNews()
                    saveNewsToLocal(newsList)
                    
                    Resource.Success(Unit)
                } else {
                    Resource.Error("Пустой ответ от сервера")
                }
            } else {
                Resource.Error("Ошибка синхронизации")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при синхронизации новостей", e)
            Resource.Error("Ошибка синхронизации")
        }
    }
    
    /**
     * Очистить кэш новостей
     */
    override suspend fun clearCache(): Resource<Unit> {
        return try {
            newsDao.deleteAllNews()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при очистке кэша", e)
            Resource.Error("Ошибка очистки кэша")
        }
    }
    
    /**
     * Сохранить новости в локальную БД
     */
    private suspend fun saveNewsToLocal(newsList: List<News>) {
        try {
            val entities = newsList.map { news ->
                NewsEntity.fromDomainModel(news)
            }
            newsDao.insertNewsList(entities)
            
            // Удалить старые новости, оставив только последние
            newsDao.deleteOldNews(DEFAULT_CACHE_SIZE)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при сохранении новостей в БД", e)
        }
    }
    
    /**
     * Получить новости из локальной БД с пагинацией
     */
    private suspend fun getNewsFromLocal(page: Int, limit: Int): List<News> {
        return try {
            val offset = (page - 1) * limit
            val entities = newsDao.getNewsWithPagination(limit, offset)
            
            entities.map { entity ->
                val category = categoryDao.getCategoryById(entity.categoryId)
                    ?.toDomainModel()
                    ?: uz.dckroff.statisfy.domain.model.Category("", "Неизвестно")
                entity.toDomainModel(category)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при получении новостей из БД", e)
            emptyList()
        }
    }
    
    /**
     * Получить новость по ID из локальной БД
     */
    private suspend fun getNewsFromLocalById(id: String): News? {
        return try {
            val entity = newsDao.getNewsById(id)
            if (entity != null) {
                val category = categoryDao.getCategoryById(entity.categoryId)
                    ?.toDomainModel()
                    ?: uz.dckroff.statisfy.domain.model.Category("", "Неизвестно")
                entity.toDomainModel(category)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при получении новости из БД", e)
            null
        }
    }
}