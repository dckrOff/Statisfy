package uz.dckroff.statisfy.data.remote.api

import retrofit2.Response
import retrofit2.http.*
import uz.dckroff.statisfy.data.remote.dto.*

/**
 * API для работы с профилем пользователя, предпочтениями и статистикой
 */
interface UserApi {
    
    // ПРОФИЛЬ ПОЛЬЗОВАТЕЛЯ
    
    /**
     * Получить профиль пользователя
     */
    @GET("api/user/profile")
    suspend fun getUserProfile(): Response<UserProfileDto>
    
    /**
     * Обновить профиль пользователя
     */
    @PUT("api/user/profile")
    suspend fun updateUserProfile(@Body profile: UserProfileUpdateDto): Response<UserProfileDto>
    
    /**
     * Загрузить аватар пользователя
     */
    @Multipart
    @POST("api/user/profile/avatar")
    suspend fun uploadAvatar(@Part avatar: okhttp3.MultipartBody.Part): Response<AvatarUploadResponseDto>
    
    // ПРЕДПОЧТЕНИЯ ПОЛЬЗОВАТЕЛЯ
    
    /**
     * Получить предпочтения пользователя
     */
    @GET("api/user/preferences")
    suspend fun getUserPreferences(): Response<UserPreferencesDto>
    
    /**
     * Сохранить предпочтения пользователя
     */
    @PUT("api/user/preferences")
    suspend fun updateUserPreferences(@Body preferences: UserPreferencesDto): Response<UserPreferencesDto>
    
    /**
     * Частичное обновление предпочтений
     */
    @PATCH("api/user/preferences")
    suspend fun patchUserPreferences(@Body preferences: Map<String, Any>): Response<UserPreferencesDto>
    
    /**
     * Сбросить предпочтения к значениям по умолчанию
     */
    @POST("api/user/preferences/reset")
    suspend fun resetPreferences(): Response<UserPreferencesDto>
    
    // СТАТИСТИКА ПОЛЬЗОВАТЕЛЯ
    
    /**
     * Получить статистику пользователя
     */
    @GET("api/analytics/user-stats")
    suspend fun getUserStats(): Response<UserStatsDto>
    
    /**
     * Получить статистику за период
     */
    @GET("api/analytics/user-stats/period")
    suspend fun getStatsForPeriod(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): Response<PeriodStatsDto>
    
    /**
     * Получить достижения пользователя
     */
    @GET("api/analytics/achievements")
    suspend fun getUserAchievements(): Response<List<AchievementDto>>
    
    /**
     * Записать событие активности
     */
    @POST("api/analytics/events")
    suspend fun recordEvent(@Body event: UserEventDto): Response<Unit>
    
    /**
     * Получить персонализированные рекомендации
     */
    @GET("api/analytics/recommendations")
    suspend fun getRecommendations(): Response<RecommendationsDto>
    
    // ИЗБРАННОЕ
    
    /**
     * Получить избранное пользователя
     */
    @GET("api/user/favorites")
    suspend fun getFavorites(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("type") type: String? = null
    ): Response<FavoritesListDto>
    
    /**
     * Синхронизировать избранное
     */
    @POST("api/user/favorites/sync")
    suspend fun syncFavorites(@Body favorites: FavoritesSyncDto): Response<FavoritesSyncResponseDto>
    
    /**
     * Добавить в избранное
     */
    @POST("api/user/favorites")
    suspend fun addToFavorites(@Body favorite: AddFavoriteDto): Response<FavoriteDto>
    
    /**
     * Удалить из избранного
     */
    @DELETE("api/user/favorites/{favoriteId}")
    suspend fun removeFromFavorites(@Path("favoriteId") favoriteId: String): Response<Unit>
    
    // ПАПКИ ИЗБРАННОГО
    
    /**
     * Получить папки избранного
     */
    @GET("api/user/favorites/folders")
    suspend fun getFavoriteFolders(): Response<List<FavoriteFolderDto>>
    
    /**
     * Создать папку избранного
     */
    @POST("api/user/favorites/folders")
    suspend fun createFolder(@Body folder: CreateFolderDto): Response<FavoriteFolderDto>
    
    /**
     * Обновить папку избранного
     */
    @PUT("api/user/favorites/folders/{folderId}")
    suspend fun updateFolder(
        @Path("folderId") folderId: String,
        @Body folder: UpdateFolderDto
    ): Response<FavoriteFolderDto>
    
    /**
     * Удалить папку избранного
     */
    @DELETE("api/user/favorites/folders/{folderId}")
    suspend fun deleteFolder(@Path("folderId") folderId: String): Response<Unit>
    
    // НАСТРОЙКИ УВЕДОМЛЕНИЙ
    
    /**
     * Получить настройки уведомлений
     */
    @GET("api/notifications/settings")
    suspend fun getNotificationSettings(): Response<NotificationSettingsDto>
    
    /**
     * Обновить настройки уведомлений
     */
    @PUT("api/notifications/settings")
    suspend fun updateNotificationSettings(@Body settings: NotificationSettingsDto): Response<NotificationSettingsDto>
    
    /**
     * Зарегистрировать устройство для уведомлений
     */
    @POST("api/notifications/register-device")
    suspend fun registerDevice(@Body device: DeviceRegistrationDto): Response<Unit>
    
    /**
     * Отписать устройство от уведомлений
     */
    @DELETE("api/notifications/unregister-device")
    suspend fun unregisterDevice(@Body deviceToken: String): Response<Unit>
    
    // ЭКСПОРТ/ИМПОРТ ДАННЫХ
    
    /**
     * Экспортировать данные пользователя
     */
    @GET("api/user/export")
    suspend fun exportUserData(@Query("types") types: List<String>): Response<UserDataExportDto>
    
    /**
     * Импортировать данные пользователя
     */
    @POST("api/user/import")
    suspend fun importUserData(@Body data: UserDataImportDto): Response<ImportResultDto>
    
    // УДАЛЕНИЕ АККАУНТА
    
    /**
     * Запросить удаление аккаунта
     */
    @POST("api/user/request-deletion")
    suspend fun requestAccountDeletion(@Body reason: AccountDeletionRequestDto): Response<Unit>
    
    /**
     * Отменить запрос на удаление аккаунта
     */
    @POST("api/user/cancel-deletion")
    suspend fun cancelAccountDeletion(): Response<Unit>
}