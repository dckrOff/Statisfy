package uz.dckroff.statisfy.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import uz.dckroff.statisfy.domain.model.Category

/**
 * Сущность для категории в базе данных
 */
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    val id: String,
    val name: String
) {
    /**
     * Преобразование сущности в доменную модель
     */
    fun toDomainModel(): Category {
        return Category(
            id = id,
            name = name
        )
    }
    
    companion object {
        /**
         * Преобразование доменной модели в сущность
         */
        fun fromDomainModel(category: Category): CategoryEntity {
            return CategoryEntity(
                id = category.id,
                name = category.name
            )
        }
    }
} 