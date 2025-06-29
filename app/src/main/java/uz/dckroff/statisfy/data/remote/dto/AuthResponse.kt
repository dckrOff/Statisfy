package uz.dckroff.statisfy.data.remote.dto

import uz.dckroff.statisfy.domain.model.User

/**
 * DTO для ответа авторизации
 */
data class AuthResponse(
    val token: String,
    val username: String,
    val email: String
)

/**
 * DTO для ответа регистрации (старый формат)
 */
data class RegisterResponse(
    val token: String,
    val user: UserDto
)

/**
 * DTO для пользователя
 */
data class UserDto(
    val id: String,
    val username: String,
    val email: String,
    val role: String
) {
    /**
     * Преобразование DTO в доменную модель
     */
    fun toDomainModel(): User {
        return User(
            id = id,
            username = username,
            email = email,
            role = role
        )
    }
} 