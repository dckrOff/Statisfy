package uz.dckroff.statisfy.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import uz.dckroff.statisfy.utils.UiState

/**
 * Базовый класс для всех ViewModel
 */
abstract class BaseViewModel<T> : ViewModel() {

    protected val _uiState = MutableStateFlow<UiState<T>>(UiState.Loading())
    val uiState: StateFlow<UiState<T>> = _uiState

} 