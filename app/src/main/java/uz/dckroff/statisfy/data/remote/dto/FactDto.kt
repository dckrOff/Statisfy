package uz.dckroff.statisfy.data.remote.dto

import uz.dckroff.statisfy.domain.model.Category
import uz.dckroff.statisfy.domain.model.Fact

/**
 * DTO для факта
 */
data class FactDto(
    val id: String,
    val title: String,
    val content: String,
    val category: CategoryDto,
    val source: String,
    val createdAt: String
) {
    /**
     * Преобразование DTO в доменную модель
     */
    fun toDomainModel(): Fact {
        return Fact(
            id = id,
            title = title,
            content = content,
            category = category.toDomainModel(),
            source = source,
            createdAt = createdAt
        )
    }
}

/**
 * DTO для категории
 */
data class CategoryDto(
    val id: String,
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
 * DTO для списка фактов с пагинацией
 */
data class FactsResponse(
    val content: List<FactDto>,
    val totalElements: Int,
    val totalPages: Int,
    val number: Int,
    val size: Int
) 