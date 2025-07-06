package uz.dckroff.statisfy.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.dckroff.statisfy.domain.model.News
import uz.dckroff.statisfy.domain.model.NewsData
import uz.dckroff.statisfy.domain.repository.NewsRepository
import uz.dckroff.statisfy.utils.Resource
import uz.dckroff.statisfy.utils.UiState
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : BaseViewModel<NewsData>() {
    
    // Состояния для UI
    private val _newsState = MutableStateFlow<UiState<NewsData>>(UiState.Idle)
    val newsState: StateFlow<UiState<NewsData>> = _newsState.asStateFlow()
    
    private val _searchState = MutableStateFlow<UiState<NewsData>>(UiState.Idle)
    val searchState: StateFlow<UiState<NewsData>> = _searchState.asStateFlow()
    
    private val _selectedNews = MutableStateFlow<News?>(null)
    val selectedNews: StateFlow<News?> = _selectedNews.asStateFlow()
    
    // Параметры для пагинации и фильтрации
    private val _currentPage = MutableStateFlow(1)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()
    
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()
    
    // Список новостей
    private val _newsList = MutableStateFlow<List<News>>(emptyList())
    val newsList: StateFlow<List<News>> = _newsList.asStateFlow()
    
    // Данные о пагинации
    private val _hasMorePages = MutableStateFlow(true)
    val hasMorePages: StateFlow<Boolean> = _hasMorePages.asStateFlow()
    
    // Сортировка
    private val _sortBy = MutableStateFlow("date_desc")
    val sortBy: StateFlow<String> = _sortBy.asStateFlow()
    
    companion object {
        private const val PAGE_SIZE = 10
        private const val TAG = "NewsViewModel"
    }
    
    init {
        loadNews()
    }
    
    /**
     * Загрузить новости
     */
    fun loadNews(refresh: Boolean = false) {
        viewModelScope.launch {
            if (refresh) {
                _currentPage.value = 1
                _newsList.value = emptyList()
                _hasMorePages.value = true
            }
            
            _newsState.value = UiState.Loading
            
            val result = newsRepository.getNews(
                page = _currentPage.value,
                limit = PAGE_SIZE,
                category = _selectedCategory.value,
                search = null,
                sort = _sortBy.value
            )
            
            when (result) {
                is Resource.Success -> {
                    val newsData = result.data
                    
                    if (refresh) {
                        _newsList.value = newsData.news
                    } else {
                        _newsList.value = _newsList.value + newsData.news
                    }
                    
                    _hasMorePages.value = _currentPage.value < newsData.totalPages
                    _newsState.value = UiState.Success(newsData)
                }
                is Resource.Error -> {
                    _newsState.value = UiState.Error(result.message ?: "Ошибка загрузки новостей")
                }
            }
        }
    }
    
    /**
     * Загрузить больше новостей (пагинация)
     */
    fun loadMoreNews() {
        if (_isLoadingMore.value || !_hasMorePages.value) return
        
        viewModelScope.launch {
            _isLoadingMore.value = true
            _currentPage.value = _currentPage.value + 1
            
            val result = newsRepository.getNews(
                page = _currentPage.value,
                limit = PAGE_SIZE,
                category = _selectedCategory.value,
                search = null,
                sort = _sortBy.value
            )
            
            when (result) {
                is Resource.Success -> {
                    val newsData = result.data
                    _newsList.value = _newsList.value + newsData.news
                    _hasMorePages.value = _currentPage.value < newsData.totalPages
                }
                is Resource.Error -> {
                    _currentPage.value = _currentPage.value - 1 // Откатить страницу
                    _newsState.value = UiState.Error(result.message ?: "Ошибка загрузки новостей")
                }
            }
            
            _isLoadingMore.value = false
        }
    }
    
    /**
     * Поиск новостей
     */
    fun searchNews(query: String) {
        _searchQuery.value = query
        
        if (query.isEmpty()) {
            _searchState.value = UiState.Idle
            return
        }
        
        viewModelScope.launch {
            _searchState.value = UiState.Loading
            
            val result = newsRepository.searchNews(
                query = query,
                page = 1,
                limit = PAGE_SIZE * 2, // Больше результатов для поиска
                category = _selectedCategory.value
            )
            
            when (result) {
                is Resource.Success -> {
                    _searchState.value = UiState.Success(result.data)
                }
                is Resource.Error -> {
                    _searchState.value = UiState.Error(result.message ?: "Ошибка поиска")
                }
            }
        }
    }
    
    /**
     * Получить новости по категории
     */
    fun getNewsByCategory(categoryId: String?) {
        _selectedCategory.value = categoryId
        loadNews(refresh = true)
    }
    
    /**
     * Получить релевантные новости
     */
    fun getRelevantNews() {
        viewModelScope.launch {
            _newsState.value = UiState.Loading
            
            val result = newsRepository.getRelevantNews(limit = PAGE_SIZE)
            
            when (result) {
                is Resource.Success -> {
                    val newsData = NewsData(
                        news = result.data,
                        totalPages = 1,
                        currentPage = 1
                    )
                    _newsList.value = result.data
                    _hasMorePages.value = false
                    _newsState.value = UiState.Success(newsData)
                }
                is Resource.Error -> {
                    _newsState.value = UiState.Error(result.message ?: "Ошибка загрузки новостей")
                }
            }
        }
    }
    
    /**
     * Получить популярные новости
     */
    fun getPopularNews(period: String = "week") {
        viewModelScope.launch {
            _newsState.value = UiState.Loading
            
            val result = newsRepository.getPopularNews(limit = PAGE_SIZE, period = period)
            
            when (result) {
                is Resource.Success -> {
                    val newsData = NewsData(
                        news = result.data,
                        totalPages = 1,
                        currentPage = 1
                    )
                    _newsList.value = result.data
                    _hasMorePages.value = false
                    _newsState.value = UiState.Success(newsData)
                }
                is Resource.Error -> {
                    _newsState.value = UiState.Error(result.message ?: "Ошибка загрузки новостей")
                }
            }
        }
    }
    
    /**
     * Получить новость по ID
     */
    fun getNewsById(id: String) {
        viewModelScope.launch {
            val result = newsRepository.getNewsById(id)
            
            when (result) {
                is Resource.Success -> {
                    _selectedNews.value = result.data
                }
                is Resource.Error -> {
                    _newsState.value = UiState.Error(result.message ?: "Ошибка загрузки новости")
                }
            }
        }
    }
    
    /**
     * Переключить статус избранного
     */
    fun toggleFavorite(newsId: String) {
        viewModelScope.launch {
            val result = newsRepository.toggleFavorite(newsId)
            
            when (result) {
                is Resource.Success -> {
                    // Обновить список новостей
                    _newsList.value = _newsList.value.map { news ->
                        if (news.id == newsId) {
                            news.copy()
                        } else {
                            news
                        }
                    }
                    
                    // Обновить выбранную новость
                    _selectedNews.value?.let { selectedNews ->
                        if (selectedNews.id == newsId) {
                            _selectedNews.value = selectedNews.copy()
                        }
                    }
                }
                is Resource.Error -> {
                    _newsState.value = UiState.Error(result.message ?: "Ошибка изменения статуса")
                }
            }
        }
    }
    
    /**
     * Отметить новость как прочитанную
     */
    fun markAsRead(newsId: String) {
        viewModelScope.launch {
            newsRepository.markAsRead(newsId)
        }
    }
    
    /**
     * Изменить сортировку
     */
    fun setSortBy(sortBy: String) {
        _sortBy.value = sortBy
        loadNews(refresh = true)
    }
    
    /**
     * Синхронизировать новости
     */
    fun syncNews() {
        viewModelScope.launch {
            _newsState.value = UiState.Loading
            
            val result = newsRepository.syncNews()
            
            when (result) {
                is Resource.Success -> {
                    loadNews(refresh = true)
                }
                is Resource.Error -> {
                    _newsState.value = UiState.Error(result.message ?: "Ошибка синхронизации")
                }
            }
        }
    }
    
    /**
     * Очистить результаты поиска
     */
    fun clearSearch() {
        _searchQuery.value = ""
        _searchState.value = UiState.Idle
    }
    
    /**
     * Очистить выбранную новость
     */
    fun clearSelectedNews() {
        _selectedNews.value = null
    }
    
    /**
     * Очистить кэш новостей
     */
    fun clearCache() {
        viewModelScope.launch {
            newsRepository.clearCache()
            loadNews(refresh = true)
        }
    }
    
    /**
     * Обновить данные
     */
    fun refresh() {
        loadNews(refresh = true)
    }
} 