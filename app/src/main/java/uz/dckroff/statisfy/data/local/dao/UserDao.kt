package uz.dckroff.statisfy.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import uz.dckroff.statisfy.data.local.entities.UserEntity

/**
 * DAO для работы с пользователями в Room
 */
@Dao
interface UserDao {
    
    /**
     * Получить пользователя по ID
     */
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?
    
    /**
     * Получить текущего пользователя
     */
    @Query("SELECT * FROM users LIMIT 1")
    fun getCurrentUserFlow(): Flow<UserEntity?>
    
    /**
     * Вставить или обновить пользователя
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
    
    /**
     * Удалить всех пользователей
     */
    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
} 