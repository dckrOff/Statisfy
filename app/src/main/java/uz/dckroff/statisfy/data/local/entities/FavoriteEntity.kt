package uz.dckroff.statisfy.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Сущность для хранения избранного контента
 */
@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    val itemId: String,
    val itemType: String,
    val title: String,
    val content: String,
    val source: String,
    val imageUrl: String?,
    val categoryId: String,
    val categoryName: String,
    val createdAt: String
) {
    companion object {
        const val TYPE_FACT = "fact"
        const val TYPE_NEWS = "news"
        const val TYPE_STATISTIC = "statistic"
    }
} 