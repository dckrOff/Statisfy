package uz.dckroff.statisfy.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import uz.dckroff.statisfy.data.local.dao.UserDao
import uz.dckroff.statisfy.data.local.entities.UserEntity
import uz.dckroff.statisfy.data.remote.api.AuthApi
import uz.dckroff.statisfy.data.remote.dto.ForgotPasswordRequest
import uz.dckroff.statisfy.data.remote.dto.LoginRequest
import uz.dckroff.statisfy.data.remote.dto.RegisterRequest
import uz.dckroff.statisfy.domain.model.User
import uz.dckroff.statisfy.domain.repository.AuthRepository
import uz.dckroff.statisfy.utils.Logger
import uz.dckroff.statisfy.utils.PreferenceManager
import uz.dckroff.statisfy.utils.Result
import javax.inject.Inject

/**
 * Реализация репозитория для аутентификации
 */
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val userDao: UserDao,
    private val preferenceManager: PreferenceManager
) : AuthRepository {
    
    override suspend fun register(username: String, email: String, password: String): Result<User> {
        Logger.methodCall("AuthRepositoryImpl", "register")
        Logger.i("Attempting to register user: $email")
        
        return try {
            val request = RegisterRequest(username, email, password)
            val response = authApi.register(request)
            
            if (response.isSuccessful) {
                response.body()?.let { registerResponse ->
                    // Сохранение токена
                    Logger.d("Registration successful, saving auth token")
                    preferenceManager.saveAuthToken(registerResponse.token)
                    preferenceManager.setLoggedIn(true)
                    
                    // Сохранение пользователя в базу данных
                    val user = User(
                        id = registerResponse.user.id,
                        username = registerResponse.user.username,
                        email = registerResponse.user.email,
                        role = registerResponse.user.role
                    )
                    Logger.d("Saving user to database: ${user.username} (${user.id})")
                    preferenceManager.saveUserId(user.id)
                    userDao.insertUser(UserEntity.fromDomainModel(user))
                    
                    Result.Success(user)
                } ?: run {
                    Logger.e("Registration error: Empty response body")
                    Result.Error("Ошибка регистрации: пустой ответ")
                }
            } else {
                Logger.e("Registration error: ${response.code()} ${response.message()}")
                Result.Error("Ошибка регистрации: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Logger.e("Registration exception", e)
            Result.Error("Ошибка регистрации: ${e.message}")
        }
    }
    
    override suspend fun login(username: String, password: String): Result<User> {
        Logger.methodCall("AuthRepositoryImpl", "login")
        Logger.i("Attempting to login user: $username")
        
        return try {
            val request = LoginRequest(username, password)
            val response = authApi.login(request)
            
            if (response.isSuccessful) {
                response.body()?.let { authResponse ->
                    // Сохранение токена
                    Logger.d("Login successful, saving auth token")
                    preferenceManager.saveAuthToken(authResponse.token)
                    preferenceManager.setLoggedIn(true)
                    
                    // Создание объекта пользователя из ответа
                    val user = User(
                        id = "user_${System.currentTimeMillis()}", // Генерируем временный ID, так как его нет в ответе
                        username = authResponse.username,
                        email = authResponse.email,
                        role = "user" // Роль по умолчанию, так как её нет в ответе
                    )
                    
                    Logger.d("Saving user to database: ${user.username}")
                    preferenceManager.saveUserId(user.id)
                    userDao.insertUser(UserEntity.fromDomainModel(user))
                    
                    Result.Success(user)
                } ?: run {
                    Logger.e("Login error: Empty response body")
                    Result.Error("Ошибка входа: пустой ответ")
                }
            } else {
                Logger.e("Login error: ${response.code()} ${response.message()}")
                Result.Error("Ошибка входа: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Logger.e("Login exception", e)
            Result.Error("Ошибка входа: ${e.message}")
        }
    }
    
    override suspend fun forgotPassword(email: String): Result<Unit> {
        Logger.methodCall("AuthRepositoryImpl", "forgotPassword")
        Logger.i("Attempting to reset password for: $email")
        
        return try {
            val request = ForgotPasswordRequest(email)
            val response = authApi.forgotPassword(request)
            
            if (response.isSuccessful) {
                Logger.d("Password reset request successful")
                Result.Success(Unit)
            } else {
                Logger.e("Password reset error: ${response.code()} ${response.message()}")
                Result.Error("Ошибка восстановления пароля: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Logger.e("Password reset exception", e)
            Result.Error("Ошибка восстановления пароля: ${e.message}")
        }
    }
    
    override fun getCurrentUserFlow(): Flow<User?> {
        Logger.methodCall("AuthRepositoryImpl", "getCurrentUserFlow")
        Logger.d("Getting current user flow")
        
        return userDao.getCurrentUserFlow().map { userEntity ->
            userEntity?.let {
                Logger.d("Current user from DB: ${it.username} (${it.id})")
                User(
                    id = it.id,
                    username = it.username,
                    email = it.email,
                    role = it.role
                )
            } ?: run {
                Logger.d("No current user found in database")
                null
            }
        }
    }
    
    override suspend fun logout() {
        Logger.methodCall("AuthRepositoryImpl", "logout")
        Logger.i("Logging out user")
        
        // Очистка данных пользователя
        preferenceManager.clearAuthData()
        userDao.deleteAllUsers()
        Logger.d("User data cleared")
    }
    
    override fun isLoggedIn(): Boolean {
        Logger.methodCall("AuthRepositoryImpl", "isLoggedIn")
        val isLoggedIn = preferenceManager.isLoggedIn() && preferenceManager.getAuthToken() != null
        Logger.d("User logged in status: $isLoggedIn")
        return isLoggedIn
    }
} 