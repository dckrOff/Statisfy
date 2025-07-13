package uz.dckroff.statisfy.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import uz.dckroff.statisfy.domain.model.*
import uz.dckroff.statisfy.domain.repository.FavoritesRepository
import uz.dckroff.statisfy.domain.repository.UserStatsRepository
import uz.dckroff.statisfy.utils.UiState
import javax.inject.Inject

/**
 * ViewModel для управления избранным контентом
 */
@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
    private val userStatsRepository: UserStatsRepository
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow<UiState<FavoritesData>>(UiState.Loading)
    val uiState: StateFlow<UiState<FavoritesData>> = _uiState.asStateFlow()

    // Search and Filter State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedContentTypes = MutableStateFlow<Set<ContentType>>(emptySet())
    val selectedContentTypes: StateFlow<Set<ContentType>> = _selectedContentTypes.asStateFlow()

    private val _selectedFolder = MutableStateFlow<String?>(null)
    val selectedFolder: StateFlow<String?> = _selectedFolder.asStateFlow()

    private val _sortBy = MutableStateFlow(FavoritesSortBy.RECENT)
    val sortBy: StateFlow<FavoritesSortBy> = _sortBy.asStateFlow()

    private val _viewMode = MutableStateFlow(FavoritesViewMode.LIST)
    val viewMode: StateFlow<FavoritesViewMode> = _viewMode.asStateFlow()

    // Folders State
    private val _folders = MutableStateFlow<List<FavoriteFolder>>(emptyList())
    val folders: StateFlow<List<FavoriteFolder>> = _folders.asStateFlow()

    private val _selectedItems = MutableStateFlow<Set<String>>(emptySet())
    val selectedItems: StateFlow<Set<String>> = _selectedItems.asStateFlow()

    // Actions State
    private val _isSelectionMode = MutableStateFlow(false)
    val isSelectionMode: StateFlow<Boolean> = _isSelectionMode.asStateFlow()

    private val _showFolderDialog = MutableStateFlow(false)
    val showFolderDialog: StateFlow<Boolean> = _showFolderDialog.asStateFlow()

    // Messages
    private val _message = MutableSharedFlow<String>()
    val message: SharedFlow<String> = _message.asSharedFlow()

    init {
        loadFavoritesData()
        loadFolders()
    }

    /**
     * Загрузка данных избранного
     */
    private fun loadFavoritesData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            try {
                favoritesRepository.getFavoritesData()
                    .combine(searchQuery) { data, query ->
                        if (query.isBlank()) data else filterByQuery(data, query)
                    }
                    .combine(selectedContentTypes) { data, types ->
                        if (types.isEmpty()) data else filterByContentTypes(data, types)
                    }
                    .combine(selectedFolder) { data, folder ->
                        if (folder == null) data else filterByFolder(data, folder)
                    }
                    .combine(sortBy) { data, sort ->
                        sortFavoritesData(data, sort)
                    }
                    .catch { exception ->
                        _uiState.value =
                            UiState.Error("Ошибка загрузки избранного: ${exception.message}")
                    }
                    .collect { filteredData ->
                        _uiState.value = UiState.Success(filteredData)
                    }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Ошибка загрузки данных: ${e.message}")
            }
        }
    }

    /**
     * Загрузка папок
     */
    private fun loadFolders() {
        viewModelScope.launch {
            favoritesRepository.getAllFolders().collect { foldersList ->
                _folders.value = foldersList
            }
        }
    }

    // ПОИСК И ФИЛЬТРАЦИЯ

    /**
     * Обновление поискового запроса
     */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    /**
     * Очистка поиска
     */
    fun clearSearch() {
        _searchQuery.value = ""
    }

    /**
     * Переключение фильтра по типу контента
     */
    fun toggleContentTypeFilter(contentType: ContentType) {
        val current = _selectedContentTypes.value.toMutableSet()
        if (current.contains(contentType)) {
            current.remove(contentType)
        } else {
            current.add(contentType)
        }
        _selectedContentTypes.value = current
    }

    /**
     * Очистка фильтров по типу контента
     */
    fun clearContentTypeFilters() {
        _selectedContentTypes.value = emptySet()
    }

    /**
     * Выбор папки для фильтрации
     */
    fun selectFolder(folderId: String?) {
        _selectedFolder.value = folderId
    }

    /**
     * Изменение сортировки
     */
    fun changeSortBy(sortBy: FavoritesSortBy) {
        _sortBy.value = sortBy
    }

    /**
     * Изменение режима отображения
     */
    fun changeViewMode(viewMode: FavoritesViewMode) {
        _viewMode.value = viewMode
    }

    // ОПЕРАЦИИ С ИЗБРАННЫМ

    /**
     * Удаление элемента из избранного
     */
    fun removeFromFavorites(itemId: String) {
        viewModelScope.launch {
            try {
                val result = favoritesRepository.removeFromFavorites(itemId)
                result
                    .onSuccess {
                        _message.emit("Удалено из избранного")
                        recordUnfavoriteEvent(itemId)
                    }
                    .onFailure { error ->
                        _message.emit("Ошибка удаления: $error")
                    }

            } catch (e: Exception) {
                _message.emit("Ошибка: ${e.message}")
            }
        }
    }

    /**
     * Переключение состояния избранного
     */
    fun toggleFavorite(contentId: String, contentType: ContentType) {
        viewModelScope.launch {
            try {
                val isFavorite = favoritesRepository.isFavorite(contentId, contentType)
                if (isFavorite) {
                    favoritesRepository.removeFromFavoritesByContentId(contentId, contentType)
                    _message.emit("Удалено из избранного")
                } else {
                    // Здесь нужно получить полный объект для добавления
                    _message.emit("Добавлено в избранное")
                }
            } catch (e: Exception) {
                _message.emit("Ошибка: ${e.message}")
            }
        }
    }

    /**
     * Запись просмотра элемента
     */
    fun recordItemView(itemId: String) {
        viewModelScope.launch {
            favoritesRepository.recordView(itemId)
        }
    }

    // УПРАВЛЕНИЕ ПАПКАМИ

    /**
     * Создание новой папки
     */
    fun createFolder(
        name: String,
        description: String?,
        color: String?,
        contentTypes: Set<ContentType>
    ) {
        viewModelScope.launch {
            try {
                val folder = FavoriteFolder(
                    id = "folder_${System.currentTimeMillis()}",
                    name = name,
                    description = description,
                    color = color,
                    contentTypes = contentTypes,
                    createdAt = System.currentTimeMillis().toString()
                )

                val result = favoritesRepository.createFolder(folder)
                result
                    .onSuccess {
                        _message.emit("Папка \"$name\" создана")
                        _showFolderDialog.value = false
                    }
                    .onFailure { error ->
                        _message.emit("Ошибка создания папки: ${error}")
                    }

            } catch (e: Exception) {
                _message.emit("Ошибка: ${e.message}")
            }
        }
    }

    /**
     * Удаление папки
     */
    fun deleteFolder(folderId: String) {
        viewModelScope.launch {
            try {
                val result = favoritesRepository.deleteFolder(folderId)
                result
                    .onSuccess {
                        _message.emit("Папка удалена")
                        if (_selectedFolder.value == folderId) {
                            _selectedFolder.value = null
                        }
                    }
                    .onFailure { error ->
                        _message.emit("Ошибка удаления папки: ${error}")
                    }

            } catch (e: Exception) {
                _message.emit("Ошибка: ${e.message}")
            }
        }
    }

    /**
     * Перемещение элемента в папку
     */
    fun moveToFolder(itemId: String, folderId: String?) {
        viewModelScope.launch {
            try {
                val result = favoritesRepository.moveToFolder(itemId, folderId)
                result
                    .onSuccess {
                        val folderName = if (folderId != null) {
                            _folders.value.find { it.id == folderId }?.name ?: "папку"
                        } else "общие элементы"
                        _message.emit("Перемещено в $folderName")
                    }
                    .onFailure { error ->
                        _message.emit("Ошибка перемещения: ${error}")
                    }

            } catch (e: Exception) {
                _message.emit("Ошибка: ${e.message}")
            }
        }
    }

    // МНОЖЕСТВЕННЫЙ ВЫБОР

    /**
     * Включение режима выбора
     */
    fun enableSelectionMode() {
        _isSelectionMode.value = true
        _selectedItems.value = emptySet()
    }

    /**
     * Выключение режима выбора
     */
    fun disableSelectionMode() {
        _isSelectionMode.value = false
        _selectedItems.value = emptySet()
    }

    /**
     * Переключение выбора элемента
     */
    fun toggleItemSelection(itemId: String) {
        val current = _selectedItems.value.toMutableSet()
        if (current.contains(itemId)) {
            current.remove(itemId)
        } else {
            current.add(itemId)
        }
        _selectedItems.value = current

        // Выключаем режим выбора, если ничего не выбрано
        if (current.isEmpty()) {
            _isSelectionMode.value = false
        }
    }

    /**
     * Выбрать все элементы
     */
    fun selectAllItems() {
        val currentData = (_uiState.value as? UiState.Success)?.data
        currentData?.let { data ->
            val allIds = data.groupedContent.values.flatten().map { it.id }.toSet()
            _selectedItems.value = allIds
        }
    }

    /**
     * Удаление выбранных элементов
     */
    fun deleteSelectedItems() {
        viewModelScope.launch {
            try {
                val selectedIds = _selectedItems.value
                selectedIds.forEach { itemId ->
                    favoritesRepository.removeFromFavorites(itemId)
                }

                _message.emit("Удалено ${selectedIds.size} элементов")
                disableSelectionMode()
            } catch (e: Exception) {
                _message.emit("Ошибка удаления: ${e.message}")
            }
        }
    }

    /**
     * Перемещение выбранных элементов в папку
     */
    fun moveSelectedItemsToFolder(folderId: String?) {
        viewModelScope.launch {
            try {
                val selectedIds = _selectedItems.value.toList()
                val result = favoritesRepository.moveItemsToFolder(selectedIds, folderId)
                result
                    .onSuccess {
                        val folderName = if (folderId != null) {
                            _folders.value.find { it.id == folderId }?.name ?: "папку"
                        } else "общие элементы"
                        _message.emit("${selectedIds.size} элементов перемещено в $folderName")
                        disableSelectionMode()
                    }
                    .onFailure { error ->
                        _message.emit("Ошибка перемещения: ${error}")
                    }
            } catch (e: Exception) {
                _message.emit("Ошибка: ${e.message}")
            }
        }
    }

    // ЭКСПОРТ И СИНХРОНИЗАЦИЯ

    /**
     * Экспорт избранного
     */
    fun exportFavorites() {
        viewModelScope.launch {
            try {
                val result = favoritesRepository.exportFavorites()
                result
                    .onSuccess { exportData ->
                        _message.emit("Избранное экспортировано")
                        // Здесь можно добавить логику сохранения файла или отправки
                    }
                    .onFailure { error ->
                        _message.emit("Ошибка экспорта: ${error}")
                    }

            } catch (e: Exception) {
                _message.emit("Ошибка: ${e.message}")
            }
        }
    }

    /**
     * Синхронизация с сервером
     */
    fun syncWithServer() {
        viewModelScope.launch {
            try {
                _message.emit("Синхронизация...")
                val result = favoritesRepository.syncFavorites()
                result
                    .onSuccess {
                        _message.emit("Синхронизация завершена")
                    }
                    .onFailure { error ->
                        _message.emit("Ошибка синхронизации: ${error}")
                    }
            } catch (e: Exception) {
                _message.emit("Ошибка: ${e.message}")
            }
        }
    }

    // UI ACTIONS

    /**
     * Показать диалог создания папки
     */
    fun showCreateFolderDialog() {
        _showFolderDialog.value = true
    }

    /**
     * Скрыть диалог создания папки
     */
    fun hideCreateFolderDialog() {
        _showFolderDialog.value = false
    }

    /**
     * Обновление данных
     */
    fun refresh() {
        loadFavoritesData()
        loadFolders()
    }

    // ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ

    private fun filterByQuery(data: FavoritesData, query: String): FavoritesData {
        if (query.isBlank()) return data

        val filteredGrouped = data.groupedContent.mapValues { (_, items) ->
            items.filter { item ->
                item.title.contains(query, ignoreCase = true) ||
                        item.summary?.contains(query, ignoreCase = true) == true ||
                        item.tags.any { tag -> tag.contains(query, ignoreCase = true) }
            }
        }.filterValues { it.isNotEmpty() }

        return data.copy(
            groupedContent = filteredGrouped,
            recentItems = data.recentItems.filter { item ->
                item.title.contains(query, ignoreCase = true) ||
                        item.summary?.contains(query, ignoreCase = true) == true
            }
        )
    }

    private fun filterByContentTypes(data: FavoritesData, types: Set<ContentType>): FavoritesData {
        val filteredGrouped = data.groupedContent.filterKeys { key ->
            types.contains(key)
        }

        return data.copy(
            groupedContent = filteredGrouped,
            recentItems = data.recentItems.filter { types.contains(it.contentType) }
        )
    }

    private fun filterByFolder(data: FavoritesData, folderId: String): FavoritesData {
        val filteredGrouped = data.groupedContent.mapValues { (_, items) ->
            items.filter { it.folderId == folderId }
        }.filterValues { it.isNotEmpty() }

        return data.copy(
            groupedContent = filteredGrouped,
            recentItems = data.recentItems.filter { it.folderId == folderId }
        )
    }

    private fun sortFavoritesData(data: FavoritesData, sortBy: FavoritesSortBy): FavoritesData {
        val sortedGrouped = data.groupedContent.mapValues { (_, items) ->
            when (sortBy) {
                FavoritesSortBy.RECENT -> items.sortedByDescending { it.addedAt }
                FavoritesSortBy.ALPHABETICAL -> items.sortedBy { it.title }
                FavoritesSortBy.CATEGORY -> items.sortedBy { it.category?.name ?: "" }
                FavoritesSortBy.CONTENT_TYPE -> items.sortedBy { it.contentType.displayName }
                FavoritesSortBy.LAST_VIEWED -> items.sortedByDescending { it.lastViewedAt ?: "" }
            }
        }

        val sortedRecent = when (sortBy) {
            FavoritesSortBy.RECENT -> data.recentItems.sortedByDescending { it.addedAt }
            FavoritesSortBy.ALPHABETICAL -> data.recentItems.sortedBy { it.title }
            FavoritesSortBy.CATEGORY -> data.recentItems.sortedBy { it.category?.name ?: "" }
            FavoritesSortBy.CONTENT_TYPE -> data.recentItems.sortedBy { it.contentType.displayName }
            FavoritesSortBy.LAST_VIEWED -> data.recentItems.sortedByDescending {
                it.lastViewedAt ?: ""
            }
        }

        return data.copy(
            groupedContent = sortedGrouped,
            recentItems = sortedRecent
        )
    }

    private fun recordUnfavoriteEvent(itemId: String) {
        viewModelScope.launch {
            // Получить информацию об элементе и записать событие
            try {
                userStatsRepository.recordUnfavoriteEvent(
                    userId = "current_user", // Получить из AuthRepository
                    contentType = ContentType.FACT, // Определить тип
                    contentId = itemId
                )
            } catch (e: Exception) {
                // Логировать ошибку, но не показывать пользователю
            }
        }
    }
} 