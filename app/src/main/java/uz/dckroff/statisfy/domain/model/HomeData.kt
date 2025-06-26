package uz.dckroff.statisfy.domain.model

/**
 * Модель данных для главного экрана
 */
data class HomeData(
    val dailyFact: Fact? = null,
    val recentFacts: List<Fact> = emptyList()
)

/**
 * Модель данных для факта
 */
data class Fact(
    val id: String,
    val title: String,
    val content: String,
    val category: Category,
    val source: String,
    val createdAt: String
)

/**
 * Модель данных для категории
 */
data class Category(
    val id: String,
    val name: String
) 