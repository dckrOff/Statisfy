package uz.dckroff.statisfy.di

import okhttp3.Interceptor
import okhttp3.Response
import uz.dckroff.statisfy.utils.Logger
import java.io.IOException

/**
 * Перехватчик для логирования HTTP запросов
 */
class LoggingInterceptor : Interceptor {
    
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()
        val method = request.method
        
        Logger.http(method, url)
        
        val startTime = System.currentTimeMillis()
        val response: Response
        
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            Logger.e("HTTP Request Failed: $method $url", e)
            throw e
        }
        
        val duration = System.currentTimeMillis() - startTime
        val statusCode = response.code
        
        Logger.http(method, url, statusCode, duration)
        
        return response
    }
} 