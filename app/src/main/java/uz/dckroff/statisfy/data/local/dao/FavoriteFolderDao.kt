package uz.dckroff.statisfy.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import uz.dckroff.statisfy.data.local.entities.FavoriteFolderEntity

/**
 * DAO для работы с папками избранного
 */
@Dao
interface FavoriteFolderDao {
    
    /**
     * Получить все папки
     */
    @Query("SELECT * FROM favorite_folders ORDER BY sortOrder ASC, name ASC")
    fun getAllFolders(): Flow<List<FavoriteFolderEntity>>
    
    /**
     * Получить все папки синхронно
     */
    @Query("SELECT * FROM favorite_folders ORDER BY sortOrder ASC, name ASC")
    suspend fun getAllFoldersSync(): List<FavoriteFolderEntity>
    
    /**
     * Получить папку по ID
     */
    @Query("SELECT * FROM favorite_folders WHERE id = :folderId")
    suspend fun getFolderById(folderId: String): FavoriteFolderEntity?
    
    /**
     * Получить папки по умолчанию
     */
    @Query("SELECT * FROM favorite_folders WHERE isDefault = 1 ORDER BY name ASC")
    suspend fun getDefaultFolders(): List<FavoriteFolderEntity>
    
    /**
     * Получить папки для определенного типа контента
     */
    @Query("SELECT * FROM favorite_folders WHERE contentTypes LIKE '%' || :contentType || '%' ORDER BY name ASC")
    suspend fun getFoldersForContentType(contentType: String): List<FavoriteFolderEntity>
    
    /**
     * Получить пустые папки
     */
    @Query("SELECT * FROM favorite_folders WHERE itemsCount = 0")
    suspend fun getEmptyFolders(): List<FavoriteFolderEntity>
    
    /**
     * Вставить папку
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: FavoriteFolderEntity)
    
    /**
     * Вставить список папок
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolders(folders: List<FavoriteFolderEntity>)
    
    /**
     * Обновить папку
     */
    @Update
    suspend fun updateFolder(folder: FavoriteFolderEntity)
    
    /**
     * Удалить папку
     */
    @Delete
    suspend fun deleteFolder(folder: FavoriteFolderEntity)
    
    /**
     * Удалить папку по ID
     */
    @Query("DELETE FROM favorite_folders WHERE id = :folderId")
    suspend fun deleteFolderById(folderId: String)
    
    /**
     * Обновить количество элементов в папке
     */
    @Query("UPDATE favorite_folders SET itemsCount = :count WHERE id = :folderId")
    suspend fun updateItemsCount(folderId: String, count: Int)
    
    /**
     * Обновить время изменения папки
     */
    @Query("UPDATE favorite_folders SET updatedAt = :timestamp WHERE id = :folderId")
    suspend fun updateTimestamp(folderId: String, timestamp: String)
    
    /**
     * Получить количество папок
     */
    @Query("SELECT COUNT(*) FROM favorite_folders")
    suspend fun getFoldersCount(): Int
    
    /**
     * Получить папки с элементами
     */
    @Query("SELECT * FROM favorite_folders WHERE itemsCount > 0 ORDER BY itemsCount DESC")
    suspend fun getFoldersWithItems(): List<FavoriteFolderEntity>
    
    /**
     * Поиск папок по имени
     */
    @Query("SELECT * FROM favorite_folders WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    suspend fun searchFolders(query: String): List<FavoriteFolderEntity>
    
    /**
     * Получить максимальный порядок сортировки
     */
    @Query("SELECT MAX(sortOrder) FROM favorite_folders")
    suspend fun getMaxSortOrder(): Int?
    
    /**
     * Обновить порядок сортировки папок
     */
    @Query("UPDATE favorite_folders SET sortOrder = :newOrder WHERE id = :folderId")
    suspend fun updateSortOrder(folderId: String, newOrder: Int)
}