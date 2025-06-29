package uz.dckroff.statisfy.utils

/**
 * Класс для обработки результатов запросов
 */
sealed class Result<out T> {
    object Loading : Result<Nothing>()
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    
    /**
     * Выполнить действие при успешном результате
     */
    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) action(data)
        return this
    }
    
    /**
     * Выполнить действие при ошибке
     */
    inline fun onFailure(action: (String) -> Unit): Result<T> {
        if (this is Error) action(message)
        return this
    }
    
    /**
     * Выполнить действие при загрузке
     */
    inline fun onLoading(action: () -> Unit): Result<T> {
        if (this is Loading) action()
        return this
    }
    
    /**
     * Преобразовать результат
     */
    fun <R> map(transform: (T) -> R): Result<R> {
        return when (this) {
            is Success -> Success(transform(data))
            is Error -> Error(message)
            is Loading -> Loading
        }
    }
} 