package uz.dckroff.statisfy.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import uz.dckroff.statisfy.domain.model.User

/**
 * Сущность для хранения пользователя в Room
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val username: String,
    val email: String,
    val role: String
) {
    /**
     * Преобразование Entity в доменную модель
     */
    fun toDomainModel(): User {
        return User(
            id = id,
            username = username,
            email = email,
            role = role
        )
    }
    
    companion object {
        /**
         * Преобразование доменной модели в Entity
         */
        fun fromDomainModel(user: User): UserEntity {
            return UserEntity(
                id = user.id,
                username = user.username,
                email = user.email,
                role = user.role
            )
        }
    }
} 