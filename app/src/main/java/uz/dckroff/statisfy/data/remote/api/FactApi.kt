package uz.dckroff.statisfy.data.remote.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import uz.dckroff.statisfy.data.remote.dto.FactDto
import uz.dckroff.statisfy.data.remote.dto.FactsResponse

/**
 * API интерфейс для работы с фактами
 */
interface FactApi {
    
    /**
     * Получение списка фактов с пагинацией
     */
    @GET("api/facts")
    suspend fun getFacts(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("category") categoryId: String? = null
    ): Response<FactsResponse>
    
    /**
     * Получение факта по ID
     */
    @GET("api/facts/{id}")
    suspend fun getFactById(
        @Path("id") id: String
    ): Response<FactDto>
    
    /**
     * Получение недавних фактов
     */
    @GET("api/facts/recent")
    suspend fun getRecentFacts(): Response<List<FactDto>>
    
    /**
     * Получение факта дня
     */
    @GET("api/ai/daily-fact")
    suspend fun getDailyFact(): Response<FactDto>
} 