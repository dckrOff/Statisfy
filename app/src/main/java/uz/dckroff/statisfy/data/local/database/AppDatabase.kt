package uz.dckroff.statisfy.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import uz.dckroff.statisfy.data.local.dao.CategoryDao
import uz.dckroff.statisfy.data.local.dao.FactDao
import uz.dckroff.statisfy.data.local.dao.UserDao
import uz.dckroff.statisfy.data.local.entities.CategoryEntity
import uz.dckroff.statisfy.data.local.entities.FactEntity
import uz.dckroff.statisfy.data.local.entities.UserEntity

/**
 * База данных Room для приложения
 */
@Database(
    entities = [
        UserEntity::class,
        FactEntity::class,
        CategoryEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    /**
     * Получить DAO для работы с пользователями
     */
    abstract fun userDao(): UserDao
    
    /**
     * Получить DAO для работы с фактами
     */
    abstract fun factDao(): FactDao
    
    /**
     * Получить DAO для работы с категориями
     */
    abstract fun categoryDao(): CategoryDao
} 