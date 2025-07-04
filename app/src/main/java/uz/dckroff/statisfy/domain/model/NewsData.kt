package uz.dckroff.statisfy.domain.model

/**
 * Модель данных для экрана новостей
 */
data class NewsData(
    val news: List<News> = emptyList(),
    val totalPages: Int = 0,
    val currentPage: Int = 0
)