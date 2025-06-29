package uz.dckroff.statisfy.domain.model

/**
 * Модель данных для пользователя в domain слое
 */
data class User(
    val id: String,
    val username: String,
    val email: String,
    val role: String
) 