package uz.dckroff.statisfy.data.remote.dto

/**
 * DTO для запроса авторизации
 */
data class LoginRequest(
    val username: String,
    val password: String
)

/**
 * DTO для запроса регистрации
 */
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

/**
 * DTO для запроса восстановления пароля
 */
data class ForgotPasswordRequest(
    val email: String
) 