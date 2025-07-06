package uz.dckroff.statisfy.data.remote.dto

import com.google.gson.annotations.SerializedName
import uz.dckroff.statisfy.domain.model.Category
import uz.dckroff.statisfy.domain.model.News

/**
 * DTO для новости из API
 */
data class NewsDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("summary")
    val summary: String,
    @SerializedName("content")
    val content: String? = null,
    @SerializedName("url")
    val url: String,
    @SerializedName("image_url")
    val imageUrl: String? = null,
    @SerializedName("source")
    val source: String,
    @SerializedName("published_at")
    val publishedAt: String,
    @SerializedName("is_relevant")
    val isRelevant: Boolean = false,
    @SerializedName("category")
    val category: CategoryDto,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null
) {
    /**
     * Преобразование DTO в доменную модель
     */
    fun toDomainModel(): News {
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
            category = category.toDomainModel(),
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}

/**
 * Ответ API для списка новостей
 */
data class NewsListResponse(
    @SerializedName("data")
    val data: List<NewsDto>,
    @SerializedName("pagination")
    val pagination: PaginationDto
)

/**
 * DTO для пагинации
 */
data class PaginationDto(
    @SerializedName("current_page")
    val currentPage: Int,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("per_page")
    val perPage: Int,
    @SerializedName("total_items")
    val totalItems: Int
)

/**
 * DTO для категории (если еще не существует)
 */
data class CategoryDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String
) {
    fun toDomainModel(): Category {
        return Category(
            id = id,
            name = name
        )
    }
}