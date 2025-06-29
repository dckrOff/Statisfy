package uz.dckroff.statisfy.domain.repository

import kotlinx.coroutines.flow.Flow
import uz.dckroff.statisfy.domain.model.User
import uz.dckroff.statisfy.utils.Result

/**
 * Интерфейс репозитория для аутентификации
 */
interface AuthRepository {
    
    /**
     * Регистрация пользователя
     */
    suspend fun register(username: String, email: String, password: String): Result<User>
    
    /**
     * Вход пользователя
     */
    suspend fun login(username: String, password: String): Result<User>
    
    /**
     * Восстановление пароля
     */
    suspend fun forgotPassword(email: String): Result<Unit>
    
    /**
     * Выход пользователя
     */
    suspend fun logout()
    
    /**
     * Получить текущего пользователя
     */
    fun getCurrentUserFlow(): Flow<User?>
    
    /**
     * Проверить, авторизован ли пользователь
     */
    fun isLoggedIn(): Boolean
} 