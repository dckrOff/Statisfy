package uz.dckroff.statisfy.utils

/**
 * Константы приложения
 */
object Constants {
    // API
//    const val BASE_URL = "https://api.statisfy.uz/" // prod ip
    const val BASE_URL = "http://192.168.52.32:8080/" // dev ip

    // SharedPreferences
    const val PREFS_NAME = "statisfy_prefs"
    const val PREF_TOKEN = "auth_token"
    const val PREF_USER_ID = "user_id"
    const val PREF_IS_LOGGED_IN = "is_logged_in"
    const val PREF_REMEMBERED_EMAIL = "remembered_email"
    const val PREF_BIOMETRIC_ENABLED = "biometric_enabled"

    // Screens
    const val SPLASH_DELAY = 1500L
} 