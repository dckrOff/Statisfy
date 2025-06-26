package uz.dckroff.statisfy.domain.model

/**
 * Модель данных для экрана профиля
 */
data class ProfileData(
    val user: User? = null
)

/**
 * Модель данных для пользователя
 */
data class User(
    val id: String,
    val username: String,
    val email: String,
    val role: String
) 