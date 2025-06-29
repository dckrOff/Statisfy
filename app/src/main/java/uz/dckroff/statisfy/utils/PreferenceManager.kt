package uz.dckroff.statisfy.utils

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Класс для работы с SharedPreferences
 */
@Singleton
class PreferenceManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        Constants.PREFS_NAME, Context.MODE_PRIVATE
    )
    
    /**
     * Сохранить токен авторизации
     */
    fun saveAuthToken(token: String) {
        prefs.edit().putString(Constants.PREF_TOKEN, token).apply()
    }
    
    /**
     * Получить токен авторизации
     */
    fun getAuthToken(): String? {
        return prefs.getString(Constants.PREF_TOKEN, null)
    }
    
    /**
     * Сохранить ID пользователя
     */
    fun saveUserId(userId: String) {
        prefs.edit().putString(Constants.PREF_USER_ID, userId).apply()
    }
    
    /**
     * Получить ID пользователя
     */
    fun getUserId(): String? {
        return prefs.getString(Constants.PREF_USER_ID, null)
    }
    
    /**
     * Установить статус авторизации
     */
    fun setLoggedIn(isLoggedIn: Boolean) {
        prefs.edit().putBoolean(Constants.PREF_IS_LOGGED_IN, isLoggedIn).apply()
    }
    
    /**
     * Проверить статус авторизации
     */
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(Constants.PREF_IS_LOGGED_IN, false)
    }
    
    /**
     * Очистить данные авторизации
     */
    fun clearAuthData() {
        prefs.edit()
            .remove(Constants.PREF_TOKEN)
            .remove(Constants.PREF_USER_ID)
            .putBoolean(Constants.PREF_IS_LOGGED_IN, false)
            .apply()
    }
    
    /**
     * Сохранить email пользователя для "Запомнить меня"
     */
    fun saveRememberedEmail(email: String) {
        prefs.edit().putString(Constants.PREF_REMEMBERED_EMAIL, email).apply()
    }
    
    /**
     * Получить сохраненный email пользователя
     */
    fun getRememberedEmail(): String? {
        return prefs.getString(Constants.PREF_REMEMBERED_EMAIL, null)
    }
    
    /**
     * Очистить сохраненный email пользователя
     */
    fun clearRememberedEmail() {
        prefs.edit().remove(Constants.PREF_REMEMBERED_EMAIL).apply()
    }
    
    /**
     * Установить использование биометрической аутентификации
     */
    fun setBiometricEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(Constants.PREF_BIOMETRIC_ENABLED, enabled).apply()
    }
    
    /**
     * Проверить использование биометрической аутентификации
     */
    fun isBiometricEnabled(): Boolean {
        return prefs.getBoolean(Constants.PREF_BIOMETRIC_ENABLED, false)
    }
} 