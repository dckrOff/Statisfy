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
import uz.dckroff.statisfy.data.local.dao.UserDao
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
} 