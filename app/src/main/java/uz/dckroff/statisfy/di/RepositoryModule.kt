package uz.dckroff.statisfy.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.dckroff.statisfy.data.local.dao.CategoryDao
import uz.dckroff.statisfy.data.local.dao.FactDao
import uz.dckroff.statisfy.data.local.dao.FavoriteDao
import uz.dckroff.statisfy.data.local.dao.FavoriteFolderDao
import uz.dckroff.statisfy.data.local.dao.NewsDao
import uz.dckroff.statisfy.data.local.dao.UserDao
import uz.dckroff.statisfy.data.local.dao.UserPreferencesDao
import uz.dckroff.statisfy.data.remote.api.AuthApi
import uz.dckroff.statisfy.data.remote.api.CategoryApi
import uz.dckroff.statisfy.data.remote.api.FactApi
import uz.dckroff.statisfy.data.remote.api.FavoriteApi
import uz.dckroff.statisfy.data.remote.api.NewsApi
import uz.dckroff.statisfy.data.repository.AuthRepositoryImpl
import uz.dckroff.statisfy.data.repository.CategoryRepositoryImpl
import uz.dckroff.statisfy.data.repository.FactRepositoryImpl
import uz.dckroff.statisfy.data.repository.FavoritesRepositoryImpl
import uz.dckroff.statisfy.data.repository.NewsRepositoryImpl
import uz.dckroff.statisfy.data.repository.UserPreferencesRepositoryImpl
import uz.dckroff.statisfy.data.repository.UserStatsRepositoryImpl
import uz.dckroff.statisfy.domain.repository.AuthRepository
import uz.dckroff.statisfy.domain.repository.CategoryRepository
import uz.dckroff.statisfy.domain.repository.FactRepository
import uz.dckroff.statisfy.domain.repository.FavoritesRepository
import uz.dckroff.statisfy.domain.repository.NewsRepository
import uz.dckroff.statisfy.domain.repository.UserPreferencesRepository
import uz.dckroff.statisfy.domain.repository.UserStatsRepository
import uz.dckroff.statisfy.utils.NetworkChecker
import uz.dckroff.statisfy.utils.PreferenceManager
import javax.inject.Singleton

/**
 * Модуль для предоставления репозиториев
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    /**
     * Предоставляет репозиторий аутентификации
     */
    @Provides
    @Singleton
    fun provideAuthRepository(
        authApi: AuthApi,
        userDao: UserDao,
        preferenceManager: PreferenceManager
    ): AuthRepository {
        return AuthRepositoryImpl(authApi, userDao, preferenceManager)
    }
    
    /**
     * Предоставляет репозиторий фактов
     */
    @Provides
    @Singleton
    fun provideFactRepository(
        factApi: FactApi,
        factDao: FactDao,
        categoryDao: CategoryDao,
        networkChecker: NetworkChecker
    ): FactRepository {
        return FactRepositoryImpl(factApi, factDao, categoryDao, networkChecker)
    }
    
    /**
     * Предоставляет репозиторий категорий
     */
    @Provides
    @Singleton
    fun provideCategoryRepository(
        categoryApi: CategoryApi,
        categoryDao: CategoryDao,
        networkChecker: NetworkChecker
    ): CategoryRepository {
        return CategoryRepositoryImpl(categoryApi, categoryDao, networkChecker)
    }
    
    /**
     * Предоставляет репозиторий избранного
     */
    @Provides
    @Singleton
    fun provideFavoritesRepository(
        favoriteDao: FavoriteDao,
        favoriteFolderDao: FavoriteFolderDao
    ): FavoritesRepository {
        return FavoritesRepositoryImpl(favoriteDao, favoriteFolderDao)
    }
    
    /**
     * Предоставляет репозиторий новостей
     */
    @Provides
    @Singleton
    fun provideNewsRepository(
        newsApi: NewsApi,
        newsDao: NewsDao,
        categoryDao: CategoryDao
    ): NewsRepository {
        return NewsRepositoryImpl(newsApi, newsDao, categoryDao)
    }
    
    /**
     * Предоставляет репозиторий статистики пользователя
     */
    @Provides
    @Singleton
    fun provideUserStatsRepository(): UserStatsRepository {
        return UserStatsRepositoryImpl()
    }
    
    /**
     * Предоставляет репозиторий пользовательских предпочтений
     */
    @Provides
    @Singleton
    fun provideUserPreferencesRepository(
        userPreferencesDao: UserPreferencesDao
    ): UserPreferencesRepository {
        return UserPreferencesRepositoryImpl(userPreferencesDao)
    }
}