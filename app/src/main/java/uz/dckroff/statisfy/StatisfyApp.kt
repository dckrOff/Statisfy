package uz.dckroff.statisfy

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import uz.dckroff.statisfy.utils.Logger

/**
 * Класс приложения Statisfy
 */
@HiltAndroidApp
class StatisfyApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        Logger.i("StatisfyApp: Application started")
        
        // Инициализация компонентов приложения
        initializeComponents()
    }
    
    /**
     * Инициализация компонентов приложения
     */
    private fun initializeComponents() {
        Logger.d("StatisfyApp: Initializing application components")
        
        // Здесь можно инициализировать глобальные компоненты, библиотеки и т.д.
        // Например: Firebase, Timber, Crashlytics и т.д.
    }
    
    override fun onLowMemory() {
        super.onLowMemory()
        Logger.w("StatisfyApp: Low memory warning")
    }
    
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Logger.d("StatisfyApp: Trim memory called with level: $level")
    }
} 