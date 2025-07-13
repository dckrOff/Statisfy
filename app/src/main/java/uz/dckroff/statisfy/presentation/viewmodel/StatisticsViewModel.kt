package uz.dckroff.statisfy.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.dckroff.statisfy.domain.model.UserStats
import uz.dckroff.statisfy.domain.repository.UserStatsRepository
import uz.dckroff.statisfy.domain.repository.UserPreferencesRepository
import uz.dckroff.statisfy.utils.Logger
import uz.dckroff.statisfy.utils.Result
import uz.dckroff.statisfy.utils.UiState
import javax.inject.Inject

/**
 * ViewModel для экрана статистики
 */
@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val userStatsRepository: UserStatsRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    private val _effect = MutableStateFlow<StatisticsEffect?>(null)
    val effect: StateFlow<StatisticsEffect?> = _effect.asStateFlow()

    init {
        loadUserStats()
    }

    /**
     * Обработка событий от UI
     */
    fun handleEvent(event: StatisticsEvent) {
        Logger.d("StatisticsViewModel: Processing event: ${event::class.simpleName}")
        
        when (event) {
            is StatisticsEvent.LoadStats -> loadUserStats()
            is StatisticsEvent.RefreshStats -> refreshStats()
            is StatisticsEvent.RetryLoad -> loadUserStats()
            is StatisticsEvent.SelectTimePeriod -> selectTimePeriod(event.period)
            is StatisticsEvent.SelectCategory -> selectCategory(event.categoryId)
            is StatisticsEvent.ShareStats -> shareStats()
            is StatisticsEvent.ExportStats -> exportStats()
        }
    }

    /**
     * Загрузка статистики пользователя
     */
    private fun loadUserStats() {
        viewModelScope.launch {
            try {
                Logger.d("StatisticsViewModel: Loading user stats")
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    error = null
                )

                val currentUser = userPreferencesRepository.getCurrentUser()
                if (currentUser == null) {
                    Logger.e("StatisticsViewModel: No current user found")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Пользователь не найден"
                    )
                    return@launch
                }

                when (val result = userStatsRepository.getUserStats(currentUser.id)) {
                    is Result.Success -> {
                        Logger.d("StatisticsViewModel: Stats loaded successfully")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            userStats = result.data,
                            error = null
                        )
                    }
                    is Result.Error -> {
                        Logger.e("StatisticsViewModel: Error loading stats: ${result.exception.message}")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.exception.message ?: "Ошибка загрузки статистики"
                        )
                    }
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }
            } catch (e: Exception) {
                Logger.e("StatisticsViewModel: Exception loading stats", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Неизвестная ошибка"
                )
            }
        }
    }

    /**
     * Обновление статистики
     */
    private fun refreshStats() {
        Logger.d("StatisticsViewModel: Refreshing stats")
        loadUserStats()
    }

    /**
     * Выбор периода времени для отображения
     */
    private fun selectTimePeriod(period: TimePeriod) {
        Logger.d("StatisticsViewModel: Selecting time period: $period")
        _uiState.value = _uiState.value.copy(selectedTimePeriod = period)
        // Здесь можно добавить логику для фильтрации данных по периоду
    }

    /**
     * Выбор категории для фильтрации
     */
    private fun selectCategory(categoryId: String?) {
        Logger.d("StatisticsViewModel: Selecting category: $categoryId")
        _uiState.value = _uiState.value.copy(selectedCategoryId = categoryId)
    }

    /**
     * Поделиться статистикой
     */
    private fun shareStats() {
        Logger.d("StatisticsViewModel: Sharing stats")
        val stats = _uiState.value.userStats
        if (stats != null) {
            val shareText = buildShareText(stats)
            _effect.value = StatisticsEffect.ShareStats(shareText)
        }
    }

    /**
     * Экспорт статистики
     */
    private fun exportStats() {
        Logger.d("StatisticsViewModel: Exporting stats")
        val stats = _uiState.value.userStats
        if (stats != null) {
            _effect.value = StatisticsEffect.ExportStats(stats)
        }
    }

    /**
     * Построение текста для поделиться
     */
    private fun buildShareText(stats: UserStats): String {
        return buildString {
            appendLine("📊 Моя статистика в Statisfy:")
            appendLine()
            appendLine("📚 Прочитано: ${stats.overallStats.totalItemsRead}")
            appendLine("⏱️ Времени потрачено: ${stats.overallStats.totalTimeSpent} мин")
            appendLine("🏆 Уровень: ${stats.overallStats.currentLevel}")
            appendLine("🔥 Текущая серия: ${stats.streakStats.currentStreak} дней")
            appendLine("⭐ Достижений: ${stats.achievements.size}")
            appendLine()
            appendLine("Присоединяйтесь к Statisfy - узнавайте интересные факты каждый день!")
        }
    }

    /**
     * Очистка эффекта
     */
    fun clearEffect() {
        _effect.value = null
    }
}

/**
 * Состояние UI для экрана статистики
 */
data class StatisticsUiState(
    val isLoading: Boolean = false,
    val userStats: UserStats? = null,
    val error: String? = null,
    val selectedTimePeriod: TimePeriod = TimePeriod.ALL_TIME,
    val selectedCategoryId: String? = null,
    val isRefreshing: Boolean = false
)

/**
 * События для экрана статистики
 */
sealed class StatisticsEvent {
    object LoadStats : StatisticsEvent()
    object RefreshStats : StatisticsEvent()
    object RetryLoad : StatisticsEvent()
    data class SelectTimePeriod(val period: TimePeriod) : StatisticsEvent()
    data class SelectCategory(val categoryId: String?) : StatisticsEvent()
    object ShareStats : StatisticsEvent()
    object ExportStats : StatisticsEvent()
}

/**
 * Эффекты для экрана статистики
 */
sealed class StatisticsEffect {
    data class ShareStats(val text: String) : StatisticsEffect()
    data class ExportStats(val stats: UserStats) : StatisticsEffect()
    data class ShowMessage(val message: String) : StatisticsEffect()
}

/**
 * Периоды времени для фильтрации
 */
enum class TimePeriod(val displayName: String) {
    WEEK("Неделя"),
    MONTH("Месяц"),
    YEAR("Год"),
    ALL_TIME("Всё время")
}