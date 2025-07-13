package uz.dckroff.statisfy.presentation.ui.statistics

import uz.dckroff.statisfy.domain.model.Category

/**
 * Элементы для отображения в списке глобальной статистики
 */
sealed class StatisticsItem {
    
    /**
     * Заголовок секции
     */
    data class Header(
        val title: String
    ) : StatisticsItem()
    
    /**
     * Статистическая запись
     */
    data class StatisticRecord(
        val id: String,
        val title: String,
        val value: Double,
        val unit: String,
        val category: Category,
        val source: String,
        val date: String
    ) : StatisticsItem()
    
    /**
     * Категория для фильтрации
     */
    data class CategoryFilter(
        val category: Category,
        val statisticsCount: Int,
        val isSelected: Boolean
    ) : StatisticsItem()
    
    /**
     * Загрузка данных
     */
    object Loading : StatisticsItem()
    
    /**
     * Ошибка загрузки
     */
    data class Error(
        val message: String
    ) : StatisticsItem()
    
    /**
     * Пустое состояние
     */
    data class Empty(
        val message: String
    ) : StatisticsItem()
}