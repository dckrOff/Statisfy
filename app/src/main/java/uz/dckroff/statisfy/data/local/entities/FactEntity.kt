package uz.dckroff.statisfy.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import uz.dckroff.statisfy.domain.model.Category
import uz.dckroff.statisfy.domain.model.Fact

/**
 * Сущность для факта в базе данных
 */
@Entity(
    tableName = "facts",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class FactEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val content: String,
    val categoryId: String,
    val source: String,
    val createdAt: String,
    val isFavorite: Boolean = false,
    val isDaily: Boolean = false
) {
    /**
     * Преобразование сущности в доменную модель
     */
    fun toDomainModel(category: Category): Fact {
        return Fact(
            id = id,
            title = title,
            content = content,
            category = category,
            source = source,
            createdAt = createdAt
        )
    }
    
    companion object {
        /**
         * Преобразование доменной модели в сущность
         */
        fun fromDomainModel(fact: Fact, isDaily: Boolean = false): FactEntity {
            return FactEntity(
                id = fact.id,
                title = fact.title,
                content = fact.content,
                categoryId = fact.category.id,
                source = fact.source,
                createdAt = fact.createdAt,
                isDaily = isDaily
            )
        }
    }
} 