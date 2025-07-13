package uz.dckroff.statisfy.data.remote.api

import retrofit2.Response
import retrofit2.http.*
import uz.dckroff.statisfy.data.remote.dto.NewsDto
import uz.dckroff.statisfy.data.remote.dto.NewsListResponse

/**
 * API для работы с новостями
 */
interface NewsApi {
    
    /**
     * Получить список новостей с пагинацией
     */
    @GET("api/news")
    suspend fun getNews(
//        @Query("page") page: Int = 1,
//        @Query("limit") limit: Int = 10,
//        @Query("category") category: String? = null,
//        @Query("search") search: String? = null,
//        @Query("sort") sort: String = "date_desc"
    ): Response<NewsListResponse>
    
    /**
     * Получить конкретную новость по ID
     */
    @GET("api/news/{id}")
    suspend fun getNewsById(
        @Path("id") id: String
    ): Response<NewsDto>
    
    /**
     * Получить релевантные новости
     */
    @GET("api/news/relevant")
    suspend fun getRelevantNews(
        @Query("limit") limit: Int = 10
    ): Response<NewsListResponse>
    
    /**
     * Получить новости по категории
     */
    @GET("api/news/category/{categoryId}")
    suspend fun getNewsByCategory(
        @Path("categoryId") categoryId: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): Response<NewsListResponse>
    
    /**
     * Поиск новостей
     */
    @GET("api/news/search")
    suspend fun searchNews(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10,
        @Query("category") category: String? = null
    ): Response<NewsListResponse>
    
    /**
     * Получить популярные новости
     */
    @GET("api/news/popular")
    suspend fun getPopularNews(
        @Query("limit") limit: Int = 10,
        @Query("period") period: String = "week" // day, week, month
    ): Response<NewsListResponse>
    
    /**
     * Отметить новость как прочитанную
     */
    @POST("api/news/{id}/read")
    suspend fun markAsRead(
        @Path("id") id: String
    ): Response<Unit>
}