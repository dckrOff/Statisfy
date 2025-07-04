package uz.dckroff.statisfy.data.remote.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import uz.dckroff.statisfy.data.remote.dto.FavoriteResponse

/**
 * Интерфейс для работы с API избранного
 */
interface FavoriteApi {

    /**
     * Получить все избранные элементы пользователя
     */
    @GET("api/favorites")
    suspend fun getFavorites(): Response<List<FavoriteResponse>>

    /**
     * Добавить элемент в избранное
     */
    @POST("api/favorites")
    suspend fun addToFavorites(@Body request: Map<String, Any>): Response<Void>

    /**
     * Удалить элемент из избранного
     */
    @DELETE("api/favorites/{itemId}")
    suspend fun removeFromFavorites(@Path("itemId") itemId: String): Response<Void>

    /**
     * Проверить, находится ли элемент в избранном
     */
    @GET("api/favorites/check/{itemId}")
    suspend fun isFavorite(@Path("itemId") itemId: String): Response<Boolean>

    /**
     * Синхронизировать избранное
     */
    @POST("api/favorites/sync")
    suspend fun syncFavorites(@Body favoriteIds: List<String>): Response<Void>
} 