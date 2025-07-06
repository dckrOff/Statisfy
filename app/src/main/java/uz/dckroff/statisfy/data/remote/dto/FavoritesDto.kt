package uz.dckroff.statisfy.data.remote.dto

import com.google.gson.annotations.SerializedName

// ИЗБРАННОЕ

/**
 * DTO избранного элемента
 */
data class FavoriteDto(
    @SerializedName("id") val id: String,
    @SerializedName("contentId") val contentId: String,
    @SerializedName("contentType") val contentType: String,
    @SerializedName("title") val title: String,
    @SerializedName("summary") val summary: String?,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("source") val source: String?,
    @SerializedName("categoryId") val categoryId: String?,
    @SerializedName("categoryName") val categoryName: String?,
    @SerializedName("folderId") val folderId: String?,
    @SerializedName("tags") val tags: List<String>,
    @SerializedName("addedAt") val addedAt: String,
    @SerializedName("lastViewedAt") val lastViewedAt: String?,
    @SerializedName("viewCount") val viewCount: Int,
    @SerializedName("metadata") val metadata: Map<String, String>
)

/**
 * DTO для добавления в избранное
 */
data class AddFavoriteDto(
    @SerializedName("contentId") val contentId: String,
    @SerializedName("contentType") val contentType: String,
    @SerializedName("folderId") val folderId: String?
)

/**
 * DTO списка избранного
 */
data class FavoritesListDto(
    @SerializedName("favorites") val favorites: List<FavoriteDto>,
    @SerializedName("totalCount") val totalCount: Int,
    @SerializedName("page") val page: Int,
    @SerializedName("pageSize") val pageSize: Int,
    @SerializedName("hasMore") val hasMore: Boolean
)

/**
 * DTO синхронизации избранного
 */
data class FavoritesSyncDto(
    @SerializedName("localFavorites") val localFavorites: List<FavoriteDto>,
    @SerializedName("lastSyncTimestamp") val lastSyncTimestamp: String?,
    @SerializedName("deviceId") val deviceId: String
)

/**
 * DTO ответа синхронизации избранного
 */
data class FavoritesSyncResponseDto(
    @SerializedName("serverFavorites") val serverFavorites: List<FavoriteDto>,
    @SerializedName("conflictedItems") val conflictedItems: List<FavoriteSyncConflictDto>,
    @SerializedName("deletedItems") val deletedItems: List<String>,
    @SerializedName("syncTimestamp") val syncTimestamp: String
)

/**
 * DTO конфликта синхронизации
 */
data class FavoriteSyncConflictDto(
    @SerializedName("favoriteId") val favoriteId: String,
    @SerializedName("localVersion") val localVersion: FavoriteDto,
    @SerializedName("serverVersion") val serverVersion: FavoriteDto,
    @SerializedName("conflictType") val conflictType: String // MODIFIED, DELETED, etc.
)

// ПАПКИ ИЗБРАННОГО

/**
 * DTO папки избранного
 */
data class FavoriteFolderDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("color") val color: String?,
    @SerializedName("icon") val icon: String?,
    @SerializedName("itemsCount") val itemsCount: Int,
    @SerializedName("contentTypes") val contentTypes: List<String>,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String?,
    @SerializedName("isDefault") val isDefault: Boolean
)

/**
 * DTO для создания папки
 */
data class CreateFolderDto(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("color") val color: String?,
    @SerializedName("icon") val icon: String?,
    @SerializedName("contentTypes") val contentTypes: List<String>
)

/**
 * DTO для обновления папки
 */
data class UpdateFolderDto(
    @SerializedName("name") val name: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("color") val color: String?,
    @SerializedName("icon") val icon: String?
)

// УВЕДОМЛЕНИЯ

/**
 * DTO регистрации устройства
 */
data class DeviceRegistrationDto(
    @SerializedName("deviceToken") val deviceToken: String,
    @SerializedName("platform") val platform: String, // ANDROID, IOS
    @SerializedName("deviceInfo") val deviceInfo: DeviceInfoDto
)

/**
 * DTO информации об устройстве
 */
data class DeviceInfoDto(
    @SerializedName("model") val model: String,
    @SerializedName("osVersion") val osVersion: String,
    @SerializedName("appVersion") val appVersion: String,
    @SerializedName("language") val language: String,
    @SerializedName("timezone") val timezone: String
)

// ЭКСПОРТ/ИМПОРТ

/**
 * DTO экспорта данных пользователя
 */
data class UserDataExportDto(
    @SerializedName("exportId") val exportId: String,
    @SerializedName("exportUrl") val exportUrl: String?,
    @SerializedName("exportData") val exportData: ExportDataDto?,
    @SerializedName("status") val status: String, // PROCESSING, COMPLETED, FAILED
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("expiresAt") val expiresAt: String?
)

/**
 * DTO экспортированных данных
 */
data class ExportDataDto(
    @SerializedName("profile") val profile: UserProfileDto?,
    @SerializedName("preferences") val preferences: UserPreferencesDto?,
    @SerializedName("favorites") val favorites: List<FavoriteDto>?,
    @SerializedName("folders") val folders: List<FavoriteFolderDto>?,
    @SerializedName("stats") val stats: UserStatsDto?,
    @SerializedName("metadata") val metadata: Map<String, Any>
)

/**
 * DTO импорта данных пользователя
 */
data class UserDataImportDto(
    @SerializedName("importData") val importData: ExportDataDto,
    @SerializedName("overwriteExisting") val overwriteExisting: Boolean = false,
    @SerializedName("selectedDataTypes") val selectedDataTypes: List<String>
)

/**
 * DTO результата импорта
 */
data class ImportResultDto(
    @SerializedName("importId") val importId: String,
    @SerializedName("status") val status: String, // SUCCESS, PARTIAL, FAILED
    @SerializedName("importedItems") val importedItems: Map<String, Int>,
    @SerializedName("errors") val errors: List<ImportErrorDto>?,
    @SerializedName("warnings") val warnings: List<String>?
)

/**
 * DTO ошибки импорта
 */
data class ImportErrorDto(
    @SerializedName("type") val type: String,
    @SerializedName("itemId") val itemId: String?,
    @SerializedName("message") val message: String,
    @SerializedName("details") val details: String?
)

// УДАЛЕНИЕ АККАУНТА

/**
 * DTO запроса на удаление аккаунта
 */
data class AccountDeletionRequestDto(
    @SerializedName("reason") val reason: String?,
    @SerializedName("feedback") val feedback: String?,
    @SerializedName("confirmationToken") val confirmationToken: String
)

// ОБЩИЕ DTO

/**
 * DTO пагинации
 */
data class PaginationDto(
    @SerializedName("page") val page: Int,
    @SerializedName("size") val size: Int,
    @SerializedName("totalElements") val totalElements: Long,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("first") val first: Boolean,
    @SerializedName("last") val last: Boolean,
    @SerializedName("hasNext") val hasNext: Boolean,
    @SerializedName("hasPrevious") val hasPrevious: Boolean
)

/**
 * DTO ответа API
 */
data class ApiResponseDto<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: T?,
    @SerializedName("message") val message: String?,
    @SerializedName("errorCode") val errorCode: String?,
    @SerializedName("timestamp") val timestamp: String
)

/**
 * DTO ошибки API
 */
data class ApiErrorDto(
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("details") val details: Map<String, Any>?,
    @SerializedName("timestamp") val timestamp: String
)