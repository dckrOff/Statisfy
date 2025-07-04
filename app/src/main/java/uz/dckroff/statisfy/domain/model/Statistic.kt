package uz.dckroff.statisfy.domain.model

import java.time.LocalDate

/**
 * Модель для статистических данных
 */
data class Statistic(
    val id: String,
    val title: String,
    val value: Double,
    val unit: String,
    val category: Category,
    val source: String,
    val date: LocalDate
) 