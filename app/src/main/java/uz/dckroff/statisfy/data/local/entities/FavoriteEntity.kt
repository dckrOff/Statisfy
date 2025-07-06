package uz.dckroff.statisfy.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import uz.dckroff.statisfy.data.local.converters.MapConverter
import uz.dckroff.statisfy.data.local.converters.StringListConverter
import uz.dckroff.statisfy.domain.model.Category
import uz.dckroff.statisfy.domain.model.ContentType
import uz.dckroff.statisfy.domain.model.FavoriteItem

/**
 * Расширенная сущность для хранения избранного контента
 */
@Entity(
    tableName = "favorites",
    foreignKeys = [
        ForeignKey(
            entity = FavoriteFolderEntity::class,
            parentColumns = ["id"],
            childColumns = ["folderId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
@TypeConverters(StringListConverter::class, MapConverter::class)
data class FavoriteEntity(
    @PrimaryKey
    val id: String,
    val contentId: String,
    val contentType: String, // FACT, NEWS, STATISTIC
    val title: String,
    val summary: String?,
    val imageUrl: String?,
    val source: String?,
    val categoryId: String?,
    val categoryName: String?,
    val folderId: String? = null,
    val tags: List<String> = emptyList(),
    val addedAt: String,
    val lastViewedAt: String? = null,
    val viewCount: Int = 0,
    val metadata: Map<String, String> = emptyMap(),
    val isArchived: Boolean = false,
    val sortOrder: Int = 0
) {
    /**
     * Преобразование в доменную модель
     */
    fun toDomainModel(): FavoriteItem {
        return FavoriteItem(
            id = id,
            contentId = contentId,
            contentType = ContentType.fromString(contentType) ?: ContentType.FACT,
            title = title,
            summary = summary,
            imageUrl = imageUrl,
            source = source,
            category = if (categoryId != null && categoryName != null) {
                Category(categoryId, categoryName)
            } else null,
            folderId = folderId,
            tags = tags,
            addedAt = addedAt,
            lastViewedAt = lastViewedAt,
            metadata = metadata
        )
    }
    
    companion object {
        const val TYPE_FACT = "FACT"
        const val TYPE_NEWS = "NEWS"
        const val TYPE_STATISTIC = "STATISTIC"
        
        /**
         * Создание из доменной модели
         */
        fun fromDomainModel(favoriteItem: FavoriteItem): FavoriteEntity {
            return FavoriteEntity(
                id = favoriteItem.id,
                contentId = favoriteItem.contentId,
                contentType = favoriteItem.contentType.name,
                title = favoriteItem.title,
                summary = favoriteItem.summary,
                imageUrl = favoriteItem.imageUrl,
                source = favoriteItem.source,
                categoryId = favoriteItem.category?.id,
                categoryName = favoriteItem.category?.name,
                folderId = favoriteItem.folderId,
                tags = favoriteItem.tags,
                addedAt = favoriteItem.addedAt,
                lastViewedAt = favoriteItem.lastViewedAt,
                metadata = favoriteItem.metadata
            )
        }
        
        /**
         * Создание из факта
         */
        fun fromFact(fact: uz.dckroff.statisfy.domain.model.Fact, folderId: String? = null): FavoriteEntity {
            return FavoriteEntity(
                id = "fav_fact_${fact.id}",
                contentId = fact.id,
                contentType = TYPE_FACT,
                title = fact.title,
                summary = fact.content,
                imageUrl = null,
                source = fact.source,
                categoryId = fact.category.id,
                categoryName = fact.category.name,
                folderId = folderId,
                addedAt = fact.createdAt,
                metadata = mapOf("type" to "fact")
            )
        }
        
        /**
         * Создание из новости
         */
        fun fromNews(news: uz.dckroff.statisfy.domain.model.News, folderId: String? = null): FavoriteEntity {
            return FavoriteEntity(
                id = "fav_news_${news.id}",
                contentId = news.id,
                contentType = TYPE_NEWS,
                title = news.title,
                summary = news.summary,
                imageUrl = news.imageUrl,
                source = news.source,
                categoryId = news.category.id,
                categoryName = news.category.name,
                folderId = folderId,
                addedAt = news.createdAt ?: news.publishedAt,
                metadata = mapOf(
                    "type" to "news",
                    "url" to news.url
                )
            )
        }
    }
} 