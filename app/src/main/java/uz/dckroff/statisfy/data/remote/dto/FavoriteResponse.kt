package uz.dckroff.statisfy.data.remote.dto

/**
 * DTO для ответа сервера на запрос избранного
 */
data class FavoriteResponse(
    val facts: List<FavoriteItemDto>,
    val news: List<FavoriteItemDto>,
    val statistics: List<FavoriteItemDto>
)

/**
 * DTO для элемента избранного
 */
data class FavoriteItemDto(
    val id: String,
    val type: String,
    val title: String,
    val content: String?,
    val source: String?,
    val imageUrl: String?,
    val category: CategoryDto,
    val createdAt: String
) 