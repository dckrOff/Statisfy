package uz.dckroff.statisfy.domain.model

/**
 * Модель новости
 */
data class News(
    val id: String,
    val title: String,
    val summary: String,
    val content: String? = null,
    val url: String,
    val imageUrl: String? = null,
    val source: String,
    val publishedAt: String,
    val isRelevant: Boolean = false,
    val category: Category,
    val createdAt: String? = null,
    val updatedAt: String? = null
)