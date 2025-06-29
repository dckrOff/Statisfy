package uz.dckroff.statisfy.utils

import android.util.Log

/**
 * Утилитарный класс для логирования
 */
object Logger {
    private const val TAG = "Statisfy"

    /**
     * Логирование информационных сообщений
     */
    fun i(message: String, tag: String = TAG) {
        Log.i(tag, message)
    }

    /**
     * Логирование сообщений об ошибках
     */
    fun e(message: String, throwable: Throwable? = null, tag: String = TAG) {
        if (throwable != null) {
            Log.e(tag, message, throwable)
        } else {
            Log.e(tag, message)
        }
    }

    /**
     * Логирование предупреждений
     */
    fun w(message: String, tag: String = TAG) {
        Log.w(tag, message)
    }

    /**
     * Логирование отладочных сообщений
     */
    fun d(message: String, tag: String = TAG) {
//        if (BuildConfig.DEBUG) {
        Log.d(tag, message)
//        }
    }

    /**
     * Логирование HTTP запросов
     */
    fun http(method: String, url: String, statusCode: Int? = null, duration: Long? = null) {
        val statusInfo = statusCode?.let { " | Status: $it" } ?: ""
        val durationInfo = duration?.let { " | Duration: ${it}ms" } ?: ""
        Log.i("$TAG-HTTP", "$method $url$statusInfo$durationInfo")
    }

    /**
     * Логирование вызовов методов
     */
    fun methodCall(className: String, methodName: String) {
//        if (BuildConfig.DEBUG) {
        Log.d("$TAG-Method", "$className.$methodName() called")
//        }
    }

    /**
     * Логирование жизненного цикла фрагментов/активити
     */
    fun lifecycle(component: String, event: String) {
//        if (BuildConfig.DEBUG) {
        Log.v("$TAG-Lifecycle", "$component: $event")
//        }
    }
} 