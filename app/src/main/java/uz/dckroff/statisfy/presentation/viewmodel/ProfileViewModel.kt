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

    private val currentUserId: String
        get() = authRepository.getCurrentUser()?.id ?: ""

    init {
        loadProfileData()
    }

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
        viewModelScope.launch {
            _profileState.value = UiState.Loading
            try {
                val user = authRepository.getCurrentUser()
                if (user != null) {
                    _profileState.value = UiState.Success(user)
                    _displayName.value = user.displayName ?: user.username
                    _bio.value = user.bio ?: ""
                } else {
                    _profileState.value = UiState.Error("Пользователь не найден")
                }
            } catch (e: Exception) {
                _profileState.value = UiState.Error("Ошибка загрузки профиля: ${e.message}")
            }
        }
    }

    /**
     * Загрузка пользовательских предпочтений
     */
    private fun loadPreferences() {
        viewModelScope.launch {
            _preferencesState.value = UiState.Loading
            try {
                userPreferencesRepository.getUserPreferences(currentUserId).fold(
                    onSuccess = { preferences ->
                        _preferencesState.value = UiState.Success(preferences)
                        _selectedTheme.value = preferences.displaySettings.theme
                        _selectedFontSize.value = preferences.displaySettings.fontSize
                    },
                    onFailure = { error ->
                        _preferencesState.value = UiState.Error("Ошибка загрузки настроек: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                _preferencesState.value = UiState.Error("Ошибка: ${e.message}")
            }
        }
    }

    /**
     * Загрузка статистики пользователя
     */
    private fun loadStatistics() {
        viewModelScope.launch {
            _statsState.value = UiState.Loading
            try {
                userStatsRepository.getUserStats(currentUserId).fold(
                    onSuccess = { stats ->
                        _statsState.value = UiState.Success(stats)
                    },
                    onFailure = { error ->
                        _statsState.value = UiState.Error("Ошибка загрузки статистики: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                _statsState.value = UiState.Error("Ошибка: ${e.message}")
            }
        }
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
        _isEditingProfile.value = true
        val currentUser = (_profileState.value as? UiState.Success)?.data
        currentUser?.let {
            _displayName.value = it.displayName ?: it.username
            _bio.value = it.bio ?: ""
        }
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
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentUser = (_profileState.value as? UiState.Success)?.data
                if (currentUser != null) {
                    val updatedUser = currentUser.copy(
                        displayName = _displayName.value.takeIf { it.isNotBlank() },
                        bio = _bio.value.takeIf { it.isNotBlank() }
                    )
                    
                    val result = authRepository.updateUserProfile(updatedUser)
                    result.fold(
                        onSuccess = {
                            _profileState.value = UiState.Success(updatedUser)
                            _isEditingProfile.value = false
                            _message.emit("Профиль обновлен")
                        },
                        onFailure = { error ->
                            _message.emit("Ошибка обновления: ${error.message}")
                        }
                    )
                }
            } catch (e: Exception) {
                _message.emit("Ошибка: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
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
        viewModelScope.launch {
            _selectedTheme.value = theme
            try {
                userPreferencesRepository.setAppTheme(currentUserId, theme).fold(
                    onSuccess = {
                        _message.emit("Тема изменена")
                    },
                    onFailure = { error ->
                        _message.emit("Ошибка изменения темы: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                _message.emit("Ошибка: ${e.message}")
            }
        }
    }

    /**
     * Изменение размера шрифта
     */
    fun changeFontSize(fontSize: FontSize) {
        viewModelScope.launch {
            _selectedFontSize.value = fontSize
            try {
                userPreferencesRepository.setFontSize(currentUserId, fontSize).fold(
                    onSuccess = {
                        _message.emit("Размер шрифта изменен")
                    },
                    onFailure = { error ->
                        _message.emit("Ошибка изменения размера: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                _message.emit("Ошибка: ${e.message}")
            }
        }
    }

    /**
     * Переключение уведомлений
     */
    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            try {
                userPreferencesRepository.setNotificationsEnabled(currentUserId, enabled).fold(
                    onSuccess = {
                        val status = if (enabled) "включены" else "выключены"
                        _message.emit("Уведомления $status")
                        loadPreferences() // Обновить данные
                    },
                    onFailure = { error ->
                        _message.emit("Ошибка настройки уведомлений: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                _message.emit("Ошибка: ${e.message}")
            }
        }
    }

    /**
     * Переключение персонализации
     */
    fun togglePersonalization(enabled: Boolean) {
        viewModelScope.launch {
            try {
                userPreferencesRepository.setPersonalizationEnabled(currentUserId, enabled).fold(
                    onSuccess = {
                        val status = if (enabled) "включена" else "выключена"
                        _message.emit("Персонализация $status")
                        loadPreferences()
                    },
                    onFailure = { error ->
                        _message.emit("Ошибка настройки персонализации: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                _message.emit("Ошибка: ${e.message}")
            }
        }
    }

    // CONTENT PREFERENCES

    /**
     * Обновление интересующих категорий
     */
    fun updateInterestCategories(categories: List<String>) {
        viewModelScope.launch {
            try {
                userPreferencesRepository.updateInterestCategories(currentUserId, categories).fold(
                    onSuccess = {
                        _message.emit("Интересы обновлены")
                        loadPreferences()
                    },
                    onFailure = { error ->
                        _message.emit("Ошибка обновления интересов: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                _message.emit("Ошибка: ${e.message}")
            }
        }
    }

    /**
     * Обновление предпочитаемых источников
     */
    fun updatePreferredSources(sources: List<String>) {
        viewModelScope.launch {
            try {
                userPreferencesRepository.updatePreferredSources(currentUserId, sources).fold(
                    onSuccess = {
                        _message.emit("Источники обновлены")
                        loadPreferences()
                    },
                    onFailure = { error ->
                        _message.emit("Ошибка обновления источников: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                _message.emit("Ошибка: ${e.message}")
            }
        }
    }

    // READING GOALS

    /**
     * Обновление целей чтения
     */
    fun updateReadingGoals(goals: ReadingGoals) {
        viewModelScope.launch {
            try {
                userPreferencesRepository.updateReadingGoals(currentUserId, goals).fold(
                    onSuccess = {
                        _message.emit("Цели обновлены")
                        loadPreferences()
                        loadStatistics() // Обновить статистику
                    },
                    onFailure = { error ->
                        _message.emit("Ошибка обновления целей: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                _message.emit("Ошибка: ${e.message}")
            }
        }
    }

    /**
     * Показать диалог целей чтения
     */
    fun showReadingGoalsDialog() {
        _showReadingGoals.value = true
    }

    /**
     * Скрыть диалог целей чтения
     */
    fun hideReadingGoalsDialog() {
        _showReadingGoals.value = false
    }

    // ACHIEVEMENTS

    /**
     * Показать экран достижений
     */
    fun showAchievements() {
        _showAchievements.value = true
    }

    /**
     * Скрыть экран достижений
     */
    fun hideAchievements() {
        _showAchievements.value = false
    }

    /**
     * Проверить новые достижения
     */
    fun checkNewAchievements() {
        viewModelScope.launch {
            try {
                userStatsRepository.checkNewAchievements(currentUserId).fold(
                    onSuccess = { newAchievements ->
                        if (newAchievements.isNotEmpty()) {
                            _message.emit("Получено ${newAchievements.size} новых достижений!")
                            loadStatistics() // Обновить статистику
                        }
                    },
                    onFailure = { error ->
                        // Тихо логируем ошибку, не показываем пользователю
                    }
                )
            } catch (e: Exception) {
                // Тихо обрабатываем
            }
        }
    }

    // DATA MANAGEMENT

    /**
     * Экспорт данных пользователя
     */
    fun exportUserData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                userPreferencesRepository.exportPreferences(currentUserId).fold(
                    onSuccess = { exportData ->
                        _message.emit("Данные экспортированы")
                        // Здесь можно добавить логику сохранения файла
                    },
                    onFailure = { error ->
                        _message.emit("Ошибка экспорта: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                _message.emit("Ошибка: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Синхронизация с сервером
     */
    fun syncWithServer() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _message.emit("Синхронизация...")
                
                // Синхронизируем предпочтения
                userPreferencesRepository.syncPreferences(currentUserId).fold(
                    onSuccess = {
                        // Синхронизируем статистику
                        userStatsRepository.syncStats(currentUserId).fold(
                            onSuccess = {
                                _message.emit("Синхронизация завершена")
                                loadProfileData() // Обновляем все данные
                            },
                            onFailure = { error ->
                                _message.emit("Ошибка синхронизации статистики: ${error.message}")
                            }
                        )
                    },
                    onFailure = { error ->
                        _message.emit("Ошибка синхронизации предпочтений: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                _message.emit("Ошибка: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Сброс настроек к значениям по умолчанию
     */
    fun resetToDefaults() {
        viewModelScope.launch {
            try {
                userPreferencesRepository.resetToDefaults(currentUserId).fold(
                    onSuccess = {
                        _message.emit("Настройки сброшены")
                        loadPreferences()
                    },
                    onFailure = { error ->
                        _message.emit("Ошибка сброса настроек: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                _message.emit("Ошибка: ${e.message}")
            }
        }
    }

    // ACCOUNT MANAGEMENT

    /**
     * Выход из аккаунта
     */
    fun signOut() {
        viewModelScope.launch {
            try {
                authRepository.signOut()
                _message.emit("Выход выполнен")
            } catch (e: Exception) {
                _message.emit("Ошибка выхода: ${e.message}")
            }
        }
    }

    /**
     * Обновление данных
     */
    fun refresh() {
        loadProfileData()
        checkNewAchievements()
    }

    /**
     * Получение рекомендаций
     */
    fun getPersonalizedRecommendations() {
        viewModelScope.launch {
            try {
                userStatsRepository.getPersonalizedRecommendations(currentUserId).fold(
                    onSuccess = { recommendations ->
                        // Можно добавить отдельный StateFlow для рекомендаций
                        _message.emit("Рекомендации обновлены")
                    },
                    onFailure = { error ->
                        _message.emit("Ошибка получения рекомендаций: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                _message.emit("Ошибка: ${e.message}")
            }
        }
    }
}

/**
 * Вкладки профиля
 */
enum class ProfileTab(val displayName: String) {
    OVERVIEW("Обзор"),
    STATISTICS("Статистика"),
    PREFERENCES("Настройки"),
    ACHIEVEMENTS("Достижения"),
    DATA("Данные")
} 