package uz.dckroff.statisfy.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uz.dckroff.statisfy.data.local.dao.CategoryDao
import uz.dckroff.statisfy.data.local.dao.FactDao
import uz.dckroff.statisfy.data.local.dao.FavoriteDao
import uz.dckroff.statisfy.data.local.dao.FavoriteFolderDao
import uz.dckroff.statisfy.data.local.dao.NewsDao
import uz.dckroff.statisfy.data.local.dao.UserDao
import uz.dckroff.statisfy.data.local.dao.UserPreferencesDao
import uz.dckroff.statisfy.data.local.database.AppDatabase
import javax.inject.Singleton

/**
 * Модуль для внедрения зависимостей для базы данных
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Предоставляет базу данных Room
     */
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "statisfy_database"
        ).fallbackToDestructiveMigration().build()
    }

    /**
     * Предоставляет DAO для работы с пользователями
     */
    @Provides
    @Singleton
    fun provideUserDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao()
    }

    /**
     * Предоставляет DAO для работы с фактами
     */
    @Provides
    @Singleton
    fun provideFactDao(appDatabase: AppDatabase): FactDao {
        return appDatabase.factDao()
    }

    /**
     * Предоставляет DAO для работы с категориями
     */
    @Provides
    @Singleton
    fun provideCategoryDao(appDatabase: AppDatabase): CategoryDao {
        return appDatabase.categoryDao()
    }

    /**
     * Предоставляет DAO для работы с избранным
     */
    @Provides
    @Singleton
    fun provideFavoriteDao(appDatabase: AppDatabase): FavoriteDao {
        return appDatabase.favoriteDao()
    }
    
    /**
     * Предоставляет DAO для работы с папками избранного
     */
    @Provides
    @Singleton
    fun provideFavoriteFolderDao(appDatabase: AppDatabase): FavoriteFolderDao {
        return appDatabase.favoriteFolderDao()
    }

    /**
     * Предоставляет DAO для работы с новостями
     */
    @Provides
    @Singleton
    fun provideNewsDao(appDatabase: AppDatabase): NewsDao {
        return appDatabase.newsDao()
    }
    
    /**
     * Предоставляет DAO для работы с пользовательскими настройками
     */
    @Provides
    @Singleton
    fun provideUserPreferencesDao(appDatabase: AppDatabase): UserPreferencesDao {
        return appDatabase.userPreferencesDao()
    }
} 