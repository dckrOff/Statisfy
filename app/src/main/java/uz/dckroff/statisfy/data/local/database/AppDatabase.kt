package uz.dckroff.statisfy.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import uz.dckroff.statisfy.data.local.dao.CategoryDao
import uz.dckroff.statisfy.data.local.dao.FactDao
import uz.dckroff.statisfy.data.local.dao.FavoriteDao
import uz.dckroff.statisfy.data.local.dao.FavoriteFolderDao
import uz.dckroff.statisfy.data.local.dao.NewsDao
import uz.dckroff.statisfy.data.local.dao.UserDao
import uz.dckroff.statisfy.data.local.dao.UserPreferencesDao
import uz.dckroff.statisfy.data.local.entities.CategoryEntity
import uz.dckroff.statisfy.data.local.entities.FactEntity
import uz.dckroff.statisfy.data.local.entities.FavoriteEntity
import uz.dckroff.statisfy.data.local.entities.FavoriteFolderEntity
import uz.dckroff.statisfy.data.local.entities.NewsEntity
import uz.dckroff.statisfy.data.local.entities.UserEntity
import uz.dckroff.statisfy.data.local.entities.UserPreferencesEntity

/**
 * База данных Room для приложения
 */
@Database(
    entities = [
        UserEntity::class,
        FactEntity::class,
        CategoryEntity::class,
        FavoriteEntity::class,
        FavoriteFolderEntity::class,
        NewsEntity::class,
        UserPreferencesEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    /**
     * Получить DAO для работы с пользователями
     */
    abstract fun userDao(): UserDao
    
    /**
     * Получить DAO для работы с фактами
     */
    abstract fun factDao(): FactDao
    
    /**
     * Получить DAO для работы с категориями
     */
    abstract fun categoryDao(): CategoryDao
    
    /**
     * Получить DAO для работы с избранным
     */
    abstract fun favoriteDao(): FavoriteDao
    
    /**
     * Получить DAO для работы с папками избранного
     */
    abstract fun favoriteFolderDao(): FavoriteFolderDao
    
    /**
     * Получить DAO для работы с новостями
     */
    abstract fun newsDao(): NewsDao
    
    /**
     * Получить DAO для работы с пользовательскими настройками
     */
    abstract fun userPreferencesDao(): UserPreferencesDao
} 