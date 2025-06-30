package uz.dckroff.statisfy.utils

/**
 * Класс для представления состояния UI
 */
sealed class UiState<T> {
    /**
     * Состояние загрузки
     */
    class Loading<T> : UiState<T>()
    
    /**
     * Состояние успешной загрузки данных
     */
    data class Success<T>(val data: T) : UiState<T>()
    
    /**
     * Состояние ошибки
     */
    data class Error<T>(val message: String) : UiState<T>()
} 