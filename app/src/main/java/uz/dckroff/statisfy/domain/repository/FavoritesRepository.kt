package uz.dckroff.statisfy.domain.repository

import kotlinx.coroutines.flow.Flow
import uz.dckroff.statisfy.domain.model.*
import uz.dckroff.statisfy.utils.Result

/**
 * Расширенный репозиторий для управления избранным контентом
 */
interface FavoritesRepository {
    
    // ОСНОВНЫЕ CRUD ОПЕРАЦИИ
    
    /**
     * Добавить факт в избранное
     */
    suspend fun addToFavorites(fact: Fact, folderId: String? = null): Result<Unit>
    
    /**
     * Добавить новость в избранное
     */
    suspend fun addToFavorites(news: News, folderId: String? = null): Result<Unit>
    
    /**
     * Добавить статистику в избранное
     */
    suspend fun addToFavorites(statistic: Statistic, folderId: String? = null): Result<Unit>
    
    /**
     * Добавить элемент в избранное (универсальный метод)
     */
    suspend fun addFavoriteItem(item: FavoriteItem): Result<Unit>
    
    /**
     * Удалить элемент из избранного
     */
    suspend fun removeFromFavorites(itemId: String): Result<Unit>
    
    /**
     * Удалить по contentId
     */
    suspend fun removeFromFavoritesByContentId(contentId: String, contentType: ContentType): Result<Unit>
    
    /**
     * Проверить, находится ли элемент в избранном
     */
    suspend fun isFavorite(contentId: String, contentType: ContentType): Boolean
    
    // ПОЛУЧЕНИЕ ДАННЫХ
    
    /**
     * Получить все избранные элементы
     */
    fun getAllFavorites(): Flow<List<FavoriteItem>>
    
    /**
     * Получить структурированные данные избранного
     */
    fun getFavoritesData(): Flow<FavoritesData>
    
    /**
     * Получить избранные факты
     */
    fun getFavoriteFacts(): Flow<List<Fact>>
    
    /**
     * Получить избранные новости
     */
    fun getFavoriteNews(): Flow<List<News>>
    
    /**
     * Получить всю избранную статистику
     */
    fun getFavoriteStatistics(): Flow<List<Statistic>>
    
    /**
     * Получить избранные по типу контента
     */
    fun getFavoritesByType(contentType: ContentType): Flow<List<FavoriteItem>>
    
    // УПРАВЛЕНИЕ ПАПКАМИ
    
    /**
     * Создать папку для избранного
     */
    suspend fun createFolder(folder: FavoriteFolder): Result<Unit>
    
    /**
     * Обновить папку
     */
    suspend fun updateFolder(folder: FavoriteFolder): Result<Unit>
    
    /**
     * Удалить папку
     */
    suspend fun deleteFolder(folderId: String): Result<Unit>
    
    /**
     * Получить все папки
     */
    fun getAllFolders(): Flow<List<FavoriteFolder>>
    
    /**
     * Получить папку по ID
     */
    suspend fun getFolderById(folderId: String): Result<FavoriteFolder>
    
    /**
     * Получить избранные из папки
     */
    fun getFavoritesInFolder(folderId: String): Flow<List<FavoriteItem>>
    
    /**
     * Переместить элемент в папку
     */
    suspend fun moveToFolder(favoriteId: String, folderId: String?): Result<Unit>
    
    /**
     * Переместить несколько элементов в папку
     */
    suspend fun moveItemsToFolder(favoriteIds: List<String>, folderId: String?): Result<Unit>
    
    /**
     * Получить папки для типа контента
     */
    suspend fun getFoldersForContentType(contentType: ContentType): Result<List<FavoriteFolder>>
    
    // ПОИСК И ФИЛЬТРАЦИЯ
    
    /**
     * Поиск в избранном
     */
    suspend fun searchFavorites(query: String): Result<List<FavoriteItem>>
    
    /**
     * Фильтрация по категории
     */
    suspend fun getFavoritesByCategory(categoryId: String): Result<List<FavoriteItem>>
    
    /**
     * Фильтрация по тегам
     */
    suspend fun getFavoritesByTag(tag: String): Result<List<FavoriteItem>>
    
    /**
     * Получить избранные за период
     */
    suspend fun getFavoritesInDateRange(startDate: String, endDate: String): Result<List<FavoriteItem>>
    
    /**
     * Расширенный поиск с фильтрами
     */
    suspend fun searchWithFilters(
        query: String = "",
        contentTypes: List<ContentType> = emptyList(),
        categories: List<String> = emptyList(),
        tags: List<String> = emptyList(),
        folderId: String? = null,
        sortBy: FavoritesSortBy = FavoritesSortBy.RECENT
    ): Result<List<FavoriteItem>>
    
    // СТАТИСТИКА И АНАЛИТИКА
    
    /**
     * Получить общее количество избранных
     */
    suspend fun getTotalFavoritesCount(): Int
    
    /**
     * Получить количество по типам
     */
    suspend fun getCountByContentType(): Result<Map<ContentType, Int>>
    
    /**
     * Получить самые недавние избранные
     */
    suspend fun getRecentFavorites(limit: Int = 10): Result<List<FavoriteItem>>
    
    /**
     * Получить самые просматриваемые избранные
     */
    suspend fun getMostViewedFavorites(limit: Int = 10): Result<List<FavoriteItem>>
    
    /**
     * Записать просмотр избранного элемента
     */
    suspend fun recordView(favoriteId: String): Result<Unit>
    
    // УПРАВЛЕНИЕ ТЕГАМИ
    
    /**
     * Добавить теги к избранному элементу
     */
    suspend fun addTags(favoriteId: String, tags: List<String>): Result<Unit>
    
    /**
     * Удалить теги от избранного элемента
     */
    suspend fun removeTags(favoriteId: String, tags: List<String>): Result<Unit>
    
    /**
     * Обновить теги избранного элемента
     */
    suspend fun updateTags(favoriteId: String, tags: List<String>): Result<Unit>
    
    /**
     * Получить все используемые теги
     */
    suspend fun getAllTags(): Result<List<String>>
    
    /**
     * Получить популярные теги
     */
    suspend fun getPopularTags(limit: Int = 20): Result<List<Pair<String, Int>>>
    
    // АРХИВИРОВАНИЕ
    
    /**
     * Архивировать избранный элемент
     */
    suspend fun archiveFavorite(favoriteId: String): Result<Unit>
    
    /**
     * Восстановить из архива
     */
    suspend fun unarchiveFavorite(favoriteId: String): Result<Unit>
    
    /**
     * Получить архивированные элементы
     */
    fun getArchivedFavorites(): Flow<List<FavoriteItem>>
    
    // НАСТРОЙКИ ОТОБРАЖЕНИЯ
    
    /**
     * Получить настройки отображения избранного
     */
    suspend fun getDisplaySettings(): Result<FavoritesDisplaySettings>
    
    /**
     * Сохранить настройки отображения
     */
    suspend fun saveDisplaySettings(settings: FavoritesDisplaySettings): Result<Unit>
    
    // ЭКСПОРТ И ИМПОРТ
    
    /**
     * Экспортировать избранное
     */
    suspend fun exportFavorites(): Result<String>
    
    /**
     * Импортировать избранное
     */
    suspend fun importFavorites(data: String): Result<Unit>
    
    // СИНХРОНИЗАЦИЯ
    
    /**
     * Синхронизировать избранное с сервером
     */
    suspend fun syncFavorites(): Result<Unit>
    
    /**
     * Синхронизировать папки с сервером
     */
    suspend fun syncFolders(): Result<Unit>
    
    // МАССОВЫЕ ОПЕРАЦИИ
    
    /**
     * Удалить все избранные
     */
    suspend fun deleteAllFavorites(): Result<Unit>
    
    /**
     * Удалить избранные по типу
     */
    suspend fun deleteFavoritesByType(contentType: ContentType): Result<Unit>
    
    /**
     * Удалить старые избранные
     */
    suspend fun deleteOldFavorites(cutoffDate: String): Result<Unit>
    
    /**
     * Очистить папку
     */
    suspend fun clearFolder(folderId: String): Result<Unit>
    
    // РЕЗЕРВНОЕ КОПИРОВАНИЕ
    
    /**
     * Создать резервную копию избранного
     */
    suspend fun createBackup(): Result<String>
    
    /**
     * Восстановить из резервной копии
     */
    suspend fun restoreFromBackup(backupData: String): Result<Unit>
} 