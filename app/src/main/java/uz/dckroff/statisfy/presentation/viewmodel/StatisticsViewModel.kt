package uz.dckroff.statisfy.presentation.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.dckroff.statisfy.domain.model.Statistic
import uz.dckroff.statisfy.domain.model.Category
import uz.dckroff.statisfy.domain.repository.CategoryRepository
import uz.dckroff.statisfy.utils.Logger
import uz.dckroff.statisfy.utils.Result
import uz.dckroff.statisfy.utils.UiState
import javax.inject.Inject

/**
 * ViewModel для экрана глобальной статистики
 */
@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    private val _effect = MutableStateFlow<StatisticsEffect?>(null)
    val effect: StateFlow<StatisticsEffect?> = _effect.asStateFlow()

    init {
        loadStatistics()
        loadCategories()
    }

    /**
     * Обработка событий от UI
     */
    fun handleEvent(event: StatisticsEvent) {
        Logger.d("StatisticsViewModel: Processing event: ${event::class.simpleName}")

        when (event) {
            is StatisticsEvent.LoadStatistics -> loadStatistics()
            is StatisticsEvent.RefreshStatistics -> refreshStatistics()
            is StatisticsEvent.RetryLoad -> loadStatistics()
            is StatisticsEvent.FilterByCategory -> filterByCategory(event.category)
            is StatisticsEvent.SearchStatistics -> searchStatistics(event.query)
            is StatisticsEvent.SelectStatistic -> selectStatistic(event.statistic)
            is StatisticsEvent.ClearFilter -> clearFilter()
            is StatisticsEvent.ShareStatistic -> shareStatistic(event.statistic)
        }
    }

    /**
     * Загрузка статистики
     */
    private fun loadStatistics() {
        viewModelScope.launch {
            try {
                Logger.d("StatisticsViewModel: Loading statistics")
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    error = null
                )

                // TODO: Заменить на реальный API вызов
                // val result = statisticsRepository.getStatistics()

                // Временная заглушка с тестовыми данными
                val mockStatistics = getMockStatistics()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    allStatistics = mockStatistics,
                    filteredStatistics = mockStatistics,
                    error = null
                )

            } catch (e: Exception) {
                Logger.e("StatisticsViewModel: Exception loading statistics", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Ошибка загрузки статистики"
                )
            }
        }
    }

    /**
     * Загрузка категорий
     */
    fun loadCategories() {
        viewModelScope.launch {
            try {
                Logger.d("StatisticsViewModel: Loading categories")

                // TODO: Заменить на реальный API вызов
                // when (val result = categoryRepository.getCategories()) {
                //     is Result.Success -> {
                //         _uiState.value = _uiState.value.copy(categories = result.data)
                //     }
                //     is Result.Error -> {
                //         Logger.e("StatisticsViewModel: Error loading categories: ${result.exception.message}")
                //     }
                // }

                // Временная заглушка с тестовыми категориями
                val mockCategories = getMockCategories()
                _uiState.value = _uiState.value.copy(categories = mockCategories)

            } catch (e: Exception) {
                Logger.e("StatisticsViewModel: Exception loading categories", e)
            }
        }
    }

    /**
     * Обновление статистики
     */
    private fun refreshStatistics() {
        Logger.d("StatisticsViewModel: Refreshing statistics")
        loadStatistics()
    }

    /**
     * Фильтрация по категории
     */
    private fun filterByCategory(category: Category?) {
        Logger.d("StatisticsViewModel: Filtering by category: ${category?.name}")

        val allStats = _uiState.value.allStatistics
        val filteredStats = if (category == null) {
            allStats
        } else {
            allStats.filter { it.category.id == category.id }
        }

        _uiState.value = _uiState.value.copy(
            selectedCategory = category,
            filteredStatistics = filteredStats,
            searchQuery = ""
        )
    }

    /**
     * Поиск статистики
     */
    private fun searchStatistics(query: String) {
        Logger.d("StatisticsViewModel: Searching statistics: $query")

        val allStats = _uiState.value.allStatistics
        val filteredStats = if (query.isBlank()) {
            allStats
        } else {
            allStats.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.unit.contains(query, ignoreCase = true) ||
                it.source.contains(query, ignoreCase = true)
            }
        }

        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            filteredStatistics = filteredStats,
            selectedCategory = null
        )
    }

    /**
     * Выбор статистики
     */
    private fun selectStatistic(statistic: Statistic) {
        Logger.d("StatisticsViewModel: Selecting statistic: ${statistic.title}")
        _uiState.value = _uiState.value.copy(selectedStatistic = statistic)
    }

    /**
     * Очистка фильтров
     */
    private fun clearFilter() {
        Logger.d("StatisticsViewModel: Clearing filters")
        _uiState.value = _uiState.value.copy(
            selectedCategory = null,
            searchQuery = "",
            filteredStatistics = _uiState.value.allStatistics
        )
    }

    /**
     * Поделиться статистикой
     */
    private fun shareStatistic(statistic: Statistic) {
        Logger.d("StatisticsViewModel: Sharing statistic: ${statistic.title}")

        val shareText = buildString {
            appendLine("📊 ${statistic.title}")
            appendLine("📈 ${statistic.value} ${statistic.unit}")
            appendLine("🏷️ Категория: ${statistic.category.name}")
            appendLine("📅 ${statistic.date}")
            appendLine("🔗 Источник: ${statistic.source}")
            appendLine()
            appendLine("Узнавайте больше интересных фактов в приложении Statisfy!")
        }

        _effect.value = StatisticsEffect.ShareStatistic(shareText)
    }

    /**
     * Очистка эффекта
     */
    fun clearEffect() {
        _effect.value = null
    }

    /**
     * Получение тестовых данных статистики
     */
    @SuppressLint("NewApi")
    private fun getMockStatistics(): List<Statistic> {
        val categories = getMockCategories()

        return listOf(
            Statistic(
                id = "1",
                title = "Население России",
                value = 146.0,
                unit = "млн человек",
                category = categories[0], // Население
                source = "Росстат",
                date = java.time.LocalDate.now()
            ),
            Statistic(
                id = "2",
                title = "ВВП США",
                value = 25.46,
                unit = "трлн долларов",
                category = categories[1], // Экономика
                source = "World Bank",
                date = java.time.LocalDate.now()
            ),
            Statistic(
                id = "3",
                title = "Средняя продолжительность жизни в Японии",
                value = 84.6,
                unit = "лет",
                category = categories[2], // Здравоохранение
                source = "WHO",
                date = java.time.LocalDate.now()
            ),
            Statistic(
                id = "4",
                title = "Уровень грамотности в мире",
                value = 86.3,
                unit = "%",
                category = categories[3], // Образование
                source = "UNESCO",
                date = java.time.LocalDate.now()
            ),
            Statistic(
                id = "5",
                title = "Уровень CO2 в атмосфере",
                value = 421.0,
                unit = "ppm",
                category = categories[4], // Экология
                source = "NOAA",
                date = java.time.LocalDate.now()
            ),
            Statistic(
                id = "6",
                title = "Уровень безработицы в Германии",
                value = 3.1,
                unit = "%",
                category = categories[1], // Экономика
                source = "Eurostat",
                date = java.time.LocalDate.now()
            ),
            Statistic(
                id = "7",
                title = "Население Китая",
                value = 1412.0,
                unit = "млн человек",
                category = categories[0], // Население
                source = "UN Population Division",
                date = java.time.LocalDate.now()
            ),
            Statistic(
                id = "8",
                title = "Площадь лесов в Бразилии",
                value = 497.0,
                unit = "млн га",
                category = categories[4], // Экология
                source = "FAO",
                date = java.time.LocalDate.now()
            )
        )
    }

    /**
     * Получение тестовых категорий
     */
    private fun getMockCategories(): List<Category> {
        return listOf(
            Category(id = "1", name = "Население"),
            Category(id = "2", name = "Экономика"),
            Category(id = "3", name = "Здравоохранение"),
            Category(id = "4", name = "Образование"),
            Category(id = "5", name = "Экология")
        )
    }
}

/**
 * Состояние UI для экрана статистики
 */
data class StatisticsUiState(
    val isLoading: Boolean = false,
    val allStatistics: List<Statistic> = emptyList(),
    val filteredStatistics: List<Statistic> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedCategory: Category? = null,
    val selectedStatistic: Statistic? = null,
    val searchQuery: String = "",
    val error: String? = null
)

/**
 * События для экрана статистики
 */
sealed class StatisticsEvent {
    object LoadStatistics : StatisticsEvent()
    object RefreshStatistics : StatisticsEvent()
    object RetryLoad : StatisticsEvent()
    data class FilterByCategory(val category: Category?) : StatisticsEvent()
    data class SearchStatistics(val query: String) : StatisticsEvent()
    data class SelectStatistic(val statistic: Statistic) : StatisticsEvent()
    object ClearFilter : StatisticsEvent()
    data class ShareStatistic(val statistic: Statistic) : StatisticsEvent()
}

/**
 * Эффекты для экрана статистики
 */
sealed class StatisticsEffect {
    data class ShareStatistic(val text: String) : StatisticsEffect()
    data class ShowMessage(val message: String) : StatisticsEffect()
}