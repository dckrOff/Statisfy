package uz.dckroff.statisfy.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import uz.dckroff.statisfy.data.local.dao.UserDao
import uz.dckroff.statisfy.data.local.entities.UserEntity

/**
 * База данных Room для приложения
 */
@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    /**
     * Получить DAO для работы с пользователями
     */
    abstract fun userDao(): UserDao
} 