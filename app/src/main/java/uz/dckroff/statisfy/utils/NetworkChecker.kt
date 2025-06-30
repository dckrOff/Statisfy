package uz.dckroff.statisfy.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Класс для проверки доступности сети
 */
@Singleton
class NetworkChecker @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /**
     * Проверяет доступность сети
     * @return true если сеть доступна, иначе false
     */
    fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }
} 