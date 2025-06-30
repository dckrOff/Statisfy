package uz.dckroff.statisfy.data.remote.api

import retrofit2.Response
import retrofit2.http.GET
import uz.dckroff.statisfy.data.remote.dto.CategoryDto

/**
 * API интерфейс для работы с категориями
 */
interface CategoryApi {
    
    /**
     * Получение списка категорий
     */
    @GET("api/categories")
    suspend fun getCategories(): Response<List<CategoryDto>>
} 