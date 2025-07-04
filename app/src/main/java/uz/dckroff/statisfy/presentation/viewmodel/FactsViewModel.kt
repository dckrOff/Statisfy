package uz.dckroff.statisfy.presentation.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.dckroff.statisfy.domain.model.Category
import uz.dckroff.statisfy.domain.model.Fact
import uz.dckroff.statisfy.domain.repository.CategoryRepository
import uz.dckroff.statisfy.domain.repository.FactRepository
import uz.dckroff.statisfy.domain.repository.FavoritesRepository
import uz.dckroff.statisfy.utils.Logger
import uz.dckroff.statisfy.utils.UiState
import javax.inject.Inject

/**
 * ViewModel для работы с фактами и их деталями
 */
@HiltViewModel
class FactsViewModel @Inject constructor(
    private val factRepository: FactRepository,
    private val favoritesRepository: FavoritesRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    // Состояние для загрузки конкретного факта
    private val _factState = MutableStateFlow<UiState<Fact>>(UiState.Loading())
    val factState: StateFlow<UiState<Fact>> = _factState
    
    // Состояние для списка фактов
    private val _factsState = MutableStateFlow<UiState<List<Fact>>>(UiState.Loading())
    val factsState: StateFlow<UiState<List<Fact>>> = _factsState
    
    // Состояние для списка категорий
    private val _categoriesState = MutableStateFlow<UiState<List<Category>>>(UiState.Loading())
    val categoriesState: StateFlow<UiState<List<Category>>> = _categoriesState
    
    // Текущий факт для работы с ним
    private val _currentFact = MutableLiveData<Fact>()
    val currentFact: LiveData<Fact> = _currentFact
    
    // Статус избранного для текущего факта
    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite
    
    // Индикатор обновления
    private val _isRefreshing = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean> = _isRefreshing
    
    /**
     * Получение факта по ID
     */
    fun getFactById(factId: String) {
        Logger.d("FactsViewModel: getFactById() called with factId: $factId")
        
        viewModelScope.launch {
            _factState.value = UiState.Loading()
            
            try {
                val result = factRepository.getFactById(factId)
                
                if (result is uz.dckroff.statisfy.utils.Result.Success) {
                    _factState.value = UiState.Success(result.data)
                    _currentFact.value = result.data
                    Logger.d("FactsViewModel: Successfully loaded fact with ID: $factId")
                } else if (result is uz.dckroff.statisfy.utils.Result.Error) {
                    _factState.value = UiState.Error(result.message)
                    Logger.e("FactsViewModel: Error loading fact with ID $factId: ${result.message}")
                }
            } catch (e: Exception) {
                _factState.value = UiState.Error("Ошибка при загрузке факта: ${e.message}")
                Logger.e("FactsViewModel: Exception while loading fact", e)
            }
        }
    }
    
    /**
     * Получение списка фактов с пагинацией
     */
    fun getFacts(page: Int = 0, size: Int = 20, categoryId: String? = null) {
        Logger.d("FactsViewModel: getFacts() called with page: $page, size: $size, categoryId: $categoryId")
        
        viewModelScope.launch {
            if (page == 0) {
                _factsState.value = UiState.Loading()
            }
            
            try {
                val result = factRepository.getFacts(page, size, categoryId)
                
                if (result is uz.dckroff.statisfy.utils.Result.Success) {
                    val currentList = if (page > 0 && _factsState.value is UiState.Success) {
                        (_factsState.value as UiState.Success<List<Fact>>).data
                    } else {
                        emptyList()
                    }
                    
                    val newList = currentList + result.data
                    _factsState.value = UiState.Success(newList)
                    Logger.d("FactsViewModel: Successfully loaded facts, page: $page")
                } else if (result is uz.dckroff.statisfy.utils.Result.Error) {
                    _factsState.value = UiState.Error(result.message)
                    Logger.e("FactsViewModel: Error loading facts: ${result.message}")
                }
            } catch (e: Exception) {
                _factsState.value = UiState.Error("Ошибка при загрузке фактов: ${e.message}")
                Logger.e("FactsViewModel: Exception while loading facts", e)
            } finally {
                _isRefreshing.value = false
            }
        }
    }
    
    /**
     * Получение списка категорий
     */
    fun getCategories() {
        Logger.d("FactsViewModel: getCategories() called")
        
        viewModelScope.launch {
            _categoriesState.value = UiState.Loading()
            
            try {
                val result = categoryRepository.getCategories()
                
                if (result is uz.dckroff.statisfy.utils.Result.Success) {
                    _categoriesState.value = UiState.Success(result.data)
                    Logger.d("FactsViewModel: Successfully loaded categories")
                } else if (result is uz.dckroff.statisfy.utils.Result.Error) {
                    _categoriesState.value = UiState.Error(result.message)
                    Logger.e("FactsViewModel: Error loading categories: ${result.message}")
                }
            } catch (e: Exception) {
                _categoriesState.value = UiState.Error("Ошибка при загрузке категорий: ${e.message}")
                Logger.e("FactsViewModel: Exception while loading categories", e)
            }
        }
    }
    
    /**
     * Проверка статуса избранного для факта
     */
    fun checkFavoriteStatus(factId: String) {
        Logger.d("FactsViewModel: checkFavoriteStatus() called with factId: $factId")
        
        viewModelScope.launch {
            try {
                val isFavorite = favoritesRepository.isFavorite(factId)
                _isFavorite.value = isFavorite
                Logger.d("FactsViewModel: Fact favorite status: $isFavorite")
            } catch (e: Exception) {
                Logger.e("FactsViewModel: Exception while checking favorite status", e)
            }
        }
    }
    
    /**
     * Переключение статуса избранного
     */
    fun toggleFavorite() {
        Logger.d("FactsViewModel: toggleFavorite() called")
        
        val fact = _currentFact.value ?: return
        val currentFavoriteStatus = _isFavorite.value ?: false
        
        viewModelScope.launch {
            try {
                if (currentFavoriteStatus) {
                    favoritesRepository.removeFromFavorites(fact.id)
                    _isFavorite.value = false
                    Logger.d("FactsViewModel: Removed fact from favorites: ${fact.id}")
                } else {
                    favoritesRepository.addToFavorites(fact)
                    _isFavorite.value = true
                    Logger.d("FactsViewModel: Added fact to favorites: ${fact.id}")
                }
            } catch (e: Exception) {
                Logger.e("FactsViewModel: Exception while toggling favorite", e)
            }
        }
    }
    
    /**
     * Обновление списка фактов (pull-to-refresh)
     */
    fun refreshFacts(categoryId: String? = null) {
        Logger.d("FactsViewModel: refreshFacts() called")
        _isRefreshing.value = true
        getFacts(0, 20, categoryId)
    }
    
    /**
     * Поделиться фактом
     */
    fun shareFact(factId: String, context: Context) {
        Logger.d("FactsViewModel: shareFact() called with factId: $factId")
        
        viewModelScope.launch {
            try {
                val result = factRepository.getFactById(factId)
                
                if (result is uz.dckroff.statisfy.utils.Result.Success) {
                    val fact = result.data
                    
                    val shareText = "${fact.title}\n\n${fact.content}\n\nИсточник: ${fact.source}"
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, "Поделиться фактом"))
                    
                    Logger.d("FactsViewModel: Shared fact with ID: $factId")
                } else {
                    Logger.e("FactsViewModel: Error sharing fact - could not load fact data")
                }
            } catch (e: Exception) {
                Logger.e("FactsViewModel: Exception while sharing fact", e)
            }
        }
    }
} 