package uz.dckroff.statisfy.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import uz.dckroff.statisfy.utils.Logger
import uz.dckroff.statisfy.utils.PreferenceManager
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Интерцептор для добавления токена авторизации к запросам
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val preferenceManager: PreferenceManager
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Получаем токен из SharedPreferences
        val token = preferenceManager.getAuthToken()
        
        // Если токен отсутствует, отправляем запрос без изменений
        if (token == null) {
            Logger.d("AuthInterceptor: No auth token found, proceeding without authorization header")
            return chain.proceed(originalRequest)
        }
        
        // Добавляем токен в заголовок Authorization
        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
        
        Logger.d("AuthInterceptor: Added authorization header to request: ${originalRequest.url}")
        return chain.proceed(newRequest)
    }
} 