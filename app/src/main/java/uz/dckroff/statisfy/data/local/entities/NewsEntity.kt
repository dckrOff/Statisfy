package uz.dckroff.statisfy.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import uz.dckroff.statisfy.domain.model.Category
import uz.dckroff.statisfy.domain.model.News

/**
 * Сущность для новости в базе данных
 */
@Entity(
    tableName = "news",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class NewsEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val summary: String,
    val content: String? = null,
    val url: String,
    val imageUrl: String? = null,
    val source: String,
    val publishedAt: String,
    val categoryId: String,
    val isRelevant: Boolean = false,
    val isFavorite: Boolean = false,
    val createdAt: String,
    val updatedAt: String
) {
    /**
     * Преобразование сущности в доменную модель
     */
    fun toDomainModel(category: Category): News {
        return News(
            id = id,
            title = title,
            summary = summary,
            content = content,
            url = url,
            imageUrl = imageUrl,
            source = source,
            publishedAt = publishedAt,
            isRelevant = isRelevant,
            category = category
        )
    }
    
    companion object {
        /**
         * Преобразование доменной модели в сущность
         */
        fun fromDomainModel(news: News): NewsEntity {
            return NewsEntity(
                id = news.id,
                title = news.title,
                summary = news.summary,
                content = news.content,
                url = news.url,
                imageUrl = news.imageUrl,
                source = news.source,
                publishedAt = news.publishedAt,
                categoryId = news.category.id,
                isRelevant = news.isRelevant,
                createdAt = news.createdAt ?: "",
                updatedAt = news.updatedAt ?: ""
            )
        }
    }
}