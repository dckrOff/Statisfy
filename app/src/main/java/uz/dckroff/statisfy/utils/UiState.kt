package uz.dckroff.statisfy.utils

/**
 * Запечатанный класс для управления состояниями UI
 */
sealed class UiState<out T> {
    /**
     * Состояние ожидания (ничего не происходит)
     */
    data object Idle : UiState<Nothing>()
    
    /**
     * Состояние загрузки
     */
    data object Loading : UiState<Nothing>()
    
    /**
     * Успешное состояние с данными
     */
    data class Success<T>(val data: T) : UiState<T>()
    
    /**
     * Состояние ошибки
     */
    data class Error(val message: String) : UiState<Nothing>()
}

/**
 * Расширение для проверки состояний
 */
fun <T> UiState<T>.isLoading(): Boolean = this is UiState.Loading

fun <T> UiState<T>.isSuccess(): Boolean = this is UiState.Success

fun <T> UiState<T>.isError(): Boolean = this is UiState.Error

fun <T> UiState<T>.isIdle(): Boolean = this is UiState.Idle

/**
 * Получить данные из успешного состояния или null
 */
fun <T> UiState<T>.getDataOrNull(): T? {
    return when (this) {
        is UiState.Success -> data
        else -> null
    }
}

/**
 * Получить сообщение об ошибке или null
 */
fun <T> UiState<T>.getErrorMessageOrNull(): String? {
    return when (this) {
        is UiState.Error -> message
        else -> null
    }
} 