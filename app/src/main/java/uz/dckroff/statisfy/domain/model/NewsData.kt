package uz.dckroff.statisfy.domain.model

/**
 * Модель данных для экрана новостей
 */
data class NewsData(
    val news: List<News> = emptyList(),
    val totalPages: Int = 0,
    val currentPage: Int = 0
)

/**
 * Модель данных для новости
 */
data class News(
    val id: String,
    val title: String,
    val summary: String,
    val url: String,
    val source: String,
    val publishedAt: String,
    val category: Category,
    val isRelevant: Boolean
) 