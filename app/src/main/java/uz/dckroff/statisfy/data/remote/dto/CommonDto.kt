package uz.dckroff.statisfy.data.remote.dto

import com.google.gson.annotations.SerializedName
import uz.dckroff.statisfy.domain.model.Category

/**
 * DTO для категории
 */
data class CategoryDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String
) {
    /**
     * Преобразование DTO в доменную модель
     */
    fun toDomainModel(): Category {
        return Category(
            id = id,
            name = name
        )
    }
}

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