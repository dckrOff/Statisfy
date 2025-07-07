package uz.dckroff.statisfy.domain.model

/**
 * Класс для обработки состояний загрузки данных
 */
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    /**
     * Состояние успешной загрузки
     */
    class Success<T>(data: T) : Resource<T>(data)

    /**
     * Состояние ошибки
     */
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)

    /**
     * Состояние загрузки
     */
    class Loading<T>(data: T? = null) : Resource<T>(data)
}