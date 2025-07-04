package uz.dckroff.statisfy.domain.model

/**
 * Модель новости
 */
data class News(
    val id: String,
    val title: String,
    val summary: String,
    val url: String,
    val source: String,
    val publishedAt: String,
    val isRelevant: Boolean,
    val category: Category
)