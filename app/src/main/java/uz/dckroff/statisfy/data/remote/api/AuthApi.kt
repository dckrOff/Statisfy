package uz.dckroff.statisfy.data.remote.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import uz.dckroff.statisfy.data.remote.dto.AuthResponse
import uz.dckroff.statisfy.data.remote.dto.ForgotPasswordRequest
import uz.dckroff.statisfy.data.remote.dto.LoginRequest
import uz.dckroff.statisfy.data.remote.dto.RegisterRequest
import uz.dckroff.statisfy.data.remote.dto.RegisterResponse

/**
 * API интерфейс для аутентификации
 */
interface AuthApi {
    
    /**
     * Вход пользователя
     */
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
    
    /**
     * Регистрация пользователя
     */
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>
    
    /**
     * Восстановление пароля
     */
    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<Unit>
} 