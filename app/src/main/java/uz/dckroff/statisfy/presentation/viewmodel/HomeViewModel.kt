package uz.dckroff.statisfy.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.dckroff.statisfy.domain.model.HomeData
import uz.dckroff.statisfy.domain.repository.FactRepository
import uz.dckroff.statisfy.utils.Logger
import uz.dckroff.statisfy.utils.Result
import uz.dckroff.statisfy.utils.UiState
import javax.inject.Inject

/**
 * ViewModel для главного экрана
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val factRepository: FactRepository
) : ViewModel() {

    // Состояние UI для главного экрана
    private val _homeState = MutableStateFlow<UiState<HomeData>>(UiState.Loading)
    val homeState: StateFlow<UiState<HomeData>> = _homeState.asStateFlow()

    // Индикатор обновления
    private val _isRefreshing = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    init {
        loadHomeData()
    }

    /**
     * Загрузка данных для главного экрана
     */
    fun loadHomeData() {
        Logger.methodCall("HomeViewModel", "loadHomeData")

        viewModelScope.launch {
            _homeState.value = UiState.Loading

            try {
                // Получаем факт дня
                val dailyFactResult = factRepository.getDailyFact()

                // Получаем недавние факты
                val recentFactsResult = factRepository.getRecentFacts()

                if (dailyFactResult is Result.Success ||
                    recentFactsResult is Result.Success
                ) {
                    val daily =
                        if (dailyFactResult is Result.Success) dailyFactResult.data else null
                    val recent =
                        if (recentFactsResult is Result.Success) recentFactsResult.data else null

                    val homeData = HomeData(
                        dailyFact = daily,
                        recentFacts = recent
                    )

                    _homeState.value = UiState.Success(homeData)
                    Logger.d("HomeViewModel: Successfully loaded home data")
                } else {
                    val errorMessage = when {
                        dailyFactResult is Result.Error ->
                            dailyFactResult.message

                        recentFactsResult is Result.Error ->
                            recentFactsResult.message

                        else -> "Неизвестная ошибка при загрузке данных"
                    }

                    _homeState.value = UiState.Error(errorMessage)
                    Logger.e("HomeViewModel: Error loading home data: $errorMessage")
                }
            } catch (e: Exception) {
                _homeState.value = UiState.Error("Ошибка при загрузке данных: ${e.message}")
                Logger.e("HomeViewModel: Exception while loading home data", e)
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    /**
     * Обновление данных (pull-to-refresh)
     */
    fun refreshData() {
        Logger.methodCall("HomeViewModel", "refreshData")

        _isRefreshing.value = true
        loadHomeData()
    }
} 