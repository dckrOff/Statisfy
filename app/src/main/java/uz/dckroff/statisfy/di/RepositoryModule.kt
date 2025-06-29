package uz.dckroff.statisfy.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.dckroff.statisfy.data.repository.AuthRepositoryImpl
import uz.dckroff.statisfy.domain.repository.AuthRepository
import javax.inject.Singleton

/**
 * Модуль для внедрения репозиториев
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    /**
     * Связывает реализацию репозитория аутентификации с интерфейсом
     */
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository
} 