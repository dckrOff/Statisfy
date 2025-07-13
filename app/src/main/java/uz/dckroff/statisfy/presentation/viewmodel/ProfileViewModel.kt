package uz.dckroff.statisfy.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import uz.dckroff.statisfy.domain.model.*
import uz.dckroff.statisfy.domain.repository.AuthRepository
import uz.dckroff.statisfy.domain.repository.UserPreferencesRepository
import uz.dckroff.statisfy.domain.repository.UserStatsRepository
import uz.dckroff.statisfy.utils.UiState
import javax.inject.Inject

/**
 * ViewModel для управления профилем пользователя
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val userStatsRepository: UserStatsRepository
) : ViewModel() {

    // Profile State
    private val _profileState = MutableStateFlow<UiState<User>>(UiState.Loading)
    val profileState: StateFlow<UiState<User>> = _profileState.asStateFlow()

    // Preferences State
    private val _preferencesState = MutableStateFlow<UiState<UserPreferences>>(UiState.Loading)
    val preferencesState: StateFlow<UiState<UserPreferences>> = _preferencesState.asStateFlow()

    // Statistics State
    private val _statsState = MutableStateFlow<UiState<UserStats>>(UiState.Loading)
    val statsState: StateFlow<UiState<UserStats>> = _statsState.asStateFlow()

    // UI States
    private val _selectedTab = MutableStateFlow(ProfileTab.OVERVIEW)
    val selectedTab: StateFlow<ProfileTab> = _selectedTab.asStateFlow()

    private val _isEditingProfile = MutableStateFlow(false)
    val isEditingProfile: StateFlow<Boolean> = _isEditingProfile.asStateFlow()

    private val _showAchievements = MutableStateFlow(false)
    val showAchievements: StateFlow<Boolean> = _showAchievements.asStateFlow()

    private val _showReadingGoals = MutableStateFlow(false)
    val showReadingGoals: StateFlow<Boolean> = _showReadingGoals.asStateFlow()

    // Loading States
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Messages
    private val _message = MutableSharedFlow<String>()
    val message: SharedFlow<String> = _message.asSharedFlow()

    // Form States
    private val _displayName = MutableStateFlow("")
    val displayName: StateFlow<String> = _displayName.asStateFlow()

    private val _bio = MutableStateFlow("")
    val bio: StateFlow<String> = _bio.asStateFlow()

    private val _selectedTheme = MutableStateFlow(AppTheme.SYSTEM)
    val selectedTheme: StateFlow<AppTheme> = _selectedTheme.asStateFlow()

    private val _selectedFontSize = MutableStateFlow(FontSize.MEDIUM)
    val selectedFontSize: StateFlow<FontSize> = _selectedFontSize.asStateFlow()

//    private val currentUserId: String
//        get() = authRepository.getCurrentUser()?.id ?: ""

//    init {
//        loadProfileData()
//    }

    /**
     * Загрузка всех данных профиля
     */
    private fun loadProfileData() {
        loadProfile()
        loadPreferences()
        loadStatistics()
    }

    /**
     * Загрузка профиля пользователя
     */
    private fun loadProfile() {
//        viewModelScope.launch {
//            _profileState.value = UiState.Loading
//            try {
//                val user = authRepository.getCurrentUser()
//                if (user != null) {
//                    _profileState.value = UiState.Success(user)
//                    _displayName.value = user.displayName ?: user.username
//                    _bio.value = user.bio ?: ""
//                } else {
//                    _profileState.value = UiState.Error("Пользователь не найден")
//                }
//            } catch (e: Exception) {
//                _profileState.value = UiState.Error("Ошибка загрузки профиля: ${e.message}")
//            }
//        }
    }

    /**
     * Загрузка пользовательских предпочтений
     */
    private fun loadPreferences() {
//        viewModelScope.launch {
//            _preferencesState.value = UiState.Loading
//            try {
//                userPreferencesRepository.getUserPreferences(currentUserId)
//                    .onSuccess { preferences ->
//                        _preferencesState.value = UiState.Success(preferences)
//                        _selectedTheme.value = preferences.displaySettings.theme
//                        _selectedFontSize.value = preferences.displaySettings.fontSize
//                    }
//                    .onFailure { error ->
//                        _preferencesState.value =
//                            UiState.Error("Ошибка загрузки настроек: ${error}")
//                    }
//            } catch (e: Exception) {
//                _preferencesState.value = UiState.Error("Ошибка: ${e.message}")
//            }
//        }
    }

    /**
     * Загрузка статистики пользователя
     */
    private fun loadStatistics() {
//        viewModelScope.launch {
//            _statsState.value = UiState.Loading
//            try {
//                val result = userStatsRepository.getUserStats(currentUserId)
//                when (result) {
//                    is uz.dckroff.statisfy.utils.Result.Success -> {
//                        _statsState.value = UiState.Success(result.data)
//                    }
//
//                    is uz.dckroff.statisfy.utils.Result.Error -> {
//                        _statsState.value =
//                            UiState.Error("Ошибка загрузки статистики: ${result.message}")
//                    }
//
//                    uz.dckroff.statisfy.utils.Result.Loading -> TODO()
//                }
//            } catch (e: Exception) {
//                _statsState.value = UiState.Error("Ошибка: ${e.message}")
//            }
//        }
    }

    // TAB NAVIGATION

    /**
     * Выбор вкладки
     */
    fun selectTab(tab: ProfileTab) {
        _selectedTab.value = tab
    }

    // PROFILE EDITING

    /**
     * Начать редактирование профиля
     */
    fun startEditingProfile() {
//        _isEditingProfile.value = true
//        val currentUser = (_profileState.value as? UiState.Success)?.data
//        currentUser?.let {
//            _displayName.value = it.displayName ?: it.username
//            _bio.value = it.bio ?: ""
//        }
    }

    /**
     * Отменить редактирование
     */
    fun cancelEditingProfile() {
        _isEditingProfile.value = false
        loadProfile() // Восстановить оригинальные данные
    }

    /**
     * Сохранить изменения профиля
     */
    fun saveProfileChanges() {
//        viewModelScope.launch {
//            _isLoading.value = true
//            try {
//                val currentUser = (_profileState.value as? UiState.Success)?.data
//                if (currentUser != null) {
//                    val updatedUser = currentUser.copy(
//                        displayName = _displayName.value.takeIf { it.isNotBlank() },
//                        bio = _bio.value.takeIf { it.isNotBlank() }
//                    )
//
//                    val result = authRepository.updateUserProfile(updatedUser)
//                    result.fold(
//                        onSuccess = {
//                            _profileState.value = UiState.Success(updatedUser)
//                            _isEditingProfile.value = false
//                            _message.emit("Профиль обновлен")
//                        },
//                        onFailure = { error ->
//                            _message.emit("Ошибка обновления: ${error.message}")
//                        }
//                    )
//                }
//            } catch (e: Exception) {
//                _message.emit("Ошибка: ${e.message}")
//            } finally {
//                _isLoading.value = false
//            }
//        }
    }

    /**
     * Обновление отображаемого имени
     */
    fun updateDisplayName(name: String) {
        _displayName.value = name
    }

    /**
     * Обновление биографии
     */
    fun updateBio(bio: String) {
        _bio.value = bio
    }

    // THEME AND DISPLAY SETTINGS

    /**
     * Изменение темы приложения
     */
    fun changeTheme(theme: AppTheme) {
//        viewModelScope.launch {
//            _selectedTheme.value = theme
//            try {
//                userPreferencesRepository.setAppTheme(currentUserId, theme).fold(
//                    onSuccess = {
//                        _message.emit("Тема изменена")
//                    },
//                    onFailure = { error ->
//                        _message.emit("Ошибка изменения темы: ${error.message}")
//                    }
//                )
//            } catch (e: Exception) {
//                _message.emit("Ошибка: ${e.message}")
//            }
//        }
    }

    /**
     * Изменение размера шрифта
     */
    fun changeFontSize(fontSize: FontSize) {
//        viewModelScope.launch {
//            _selectedFontSize.value = fontSize
//            try {
//                userPreferencesRepository.setFontSize(currentUserId, fontSize).fold(
//                    onSuccess = {
//                        _message.emit("Размер шрифта изменен")
//                    },
//                    onFailure = { error ->
//                        _message.emit("Ошибка изменения размера шрифта: ${error.message}")
//                    }
//                )
//            } catch (e: Exception) {
//                _message.emit("Ошибка: ${e.message}")
//            }
//        }
    }

    // STATISTICS MANAGEMENT

    /**
     * Показать/скрыть достижения
     */
    fun toggleAchievements() {
        _showAchievements.value = !_showAchievements.value
    }

    /**
     * Показать/скрыть цели чтения
     */
    fun toggleReadingGoals() {
        _showReadingGoals.value = !_showReadingGoals.value
    }

    /**
     * Получить статистику за период
     */
    fun getStatsForPeriod(startDate: String, endDate: String) {
//        viewModelScope.launch {
//            _isLoading.value = true
//            try {
//                val result =
//                    userStatsRepository.getStatsForPeriod(currentUserId, startDate, endDate)
//                when (result) {
//                    is Result.Success -> {
//                        // Handle period stats
//                        _message.emit("Статистика за период загружена")
//                    }
//
//                    is Result.Error -> {
//                        _message.emit("Ошибка загрузки статистики за период: ${result.message}")
//                    }
//                }
//            } catch (e: Exception) {
//                _message.emit("Ошибка: ${e.message}")
//            } finally {
//                _isLoading.value = false
//            }
//        }
    }

    /**
     * Получить достижения пользователя
     */
    fun getUserAchievements() {
//        viewModelScope.launch {
//            try {
//                val result = userStatsRepository.getUserAchievements(currentUserId)
//                when (result) {
//                    is Result.Success -> {
//                        // Handle achievements
//                        _message.emit("Получено ${result.data.size} достижений")
//                    }
//
//                    is Result.Error -> {
//                        _message.emit("Ошибка загрузки достижений: ${result.message}")
//                    }
//                }
//            } catch (e: Exception) {
//                _message.emit("Ошибка: ${e.message}")
//            }
//        }
    }

    /**
     * Обновить серию активности
     */
    fun updateStreak() {
//        viewModelScope.launch {
//            try {
//                val result = userStatsRepository.updateStreak(currentUserId)
//                when (result) {
//                    is Result.Success -> {
//                        loadStatistics() // Перезагрузить статистику
//                        _message.emit("Серия активности обновлена")
//                    }
//
//                    is Result.Error -> {
//                        _message.emit("Ошибка обновления серии: ${result.message}")
//                    }
//                }
//            } catch (e: Exception) {
//                _message.emit("Ошибка: ${e.message}")
//            }
//        }
    }

    /**
     * Экспорт статистики
     */
    fun exportStats() {
//        viewModelScope.launch {
//            _isLoading.value = true
//            try {
//                val result = userStatsRepository.exportStats(currentUserId)
//                when (result) {
//                    is Result.Success -> {
//                        _message.emit("Статистика экспортирована: ${result.data}")
//                    }
//
//                    is Result.Error -> {
//                        _message.emit("Ошибка экспорта статистики: ${result.message}")
//                    }
//                }
//            } catch (e: Exception) {
//                _message.emit("Ошибка: ${e.message}")
//            } finally {
//                _isLoading.value = false
//            }
//        }
    }

    /**
     * Сброс статистики
     */
    fun resetStats() {
//        viewModelScope.launch {
//            _isLoading.value = true
//            try {
//                val result = userStatsRepository.resetStats(currentUserId)
//                when (result) {
//                    is Result.Success -> {
//                        loadStatistics() // Перезагрузить статистику
//                        _message.emit("Статистика сброшена")
//                    }
//
//                    is Result.Error -> {
//                        _message.emit("Ошибка сброса статистики: ${result.message}")
//                    }
//                }
//            } catch (e: Exception) {
//                _message.emit("Ошибка: ${e.message}")
//            } finally {
//                _isLoading.value = false
//            }
//        }
    }

    /**
     * Выход из аккаунта
     */
    fun logout() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                authRepository.logout()
                // Навигация обычно выполняется в UI после наблюдения за результатом
                _message.emit("Вы вышли из аккаунта")
            } catch (e: Exception) {
                _message.emit("Ошибка при выходе: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}

/**
 * Вкладки профиля
 */
enum class ProfileTab {
    OVERVIEW,      // Обзор
    STATISTICS,    // Статистика
    PREFERENCES,   // Настройки
    ACCOUNT        // Аккаунт
} 