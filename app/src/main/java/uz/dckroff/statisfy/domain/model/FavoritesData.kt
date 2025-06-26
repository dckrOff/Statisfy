package uz.dckroff.statisfy.domain.model

/**
 * Модель данных для экрана избранного
 */
data class FavoritesData(
    val favorites: List<Favorite> = emptyList()
)

/**
 * Модель данных для избранного элемента
 */
data class Favorite(
    val id: String,
    val type: FavoriteType,
    val title: String,
    val content: String,
    val savedAt: String
)

/**
 * Тип избранного элемента
 */
enum class FavoriteType {
    FACT,
    NEWS,
    STATISTIC
} 