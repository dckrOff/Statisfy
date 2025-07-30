package uz.dckroff.statisfy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.dckroff.statisfy.databinding.ActivityMainBinding
import uz.dckroff.statisfy.utils.Logger

/**
 * Главная активность приложения
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.lifecycle("MainActivity", "onCreate")
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupNavigation()
    }
    
    /**
     * Настройка навигации
     */
    private fun setupNavigation() {
        Logger.d("MainActivity: Setting up navigation")
        
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        
        // Настройка нижней навигации
        binding.bottomNavigation.setupWithNavController(navController)
        
        // Скрытие/отображение нижней навигации в зависимости от экрана
        navController.addOnDestinationChangedListener { _, destination, _ ->
            Logger.d("MainActivity: Navigation to destination: ${destination.label}")
            
            val shouldShowBottomNav = when (destination.id) {
                R.id.homeFragment, R.id.newsFragment, R.id.statisticsFragment, R.id.favoritesFragment, R.id.profileFragment -> true
                else -> false
            }
            
            if (shouldShowBottomNav) {
                Logger.d("MainActivity: Showing bottom navigation")
                binding.bottomNavigation.visibility = android.view.View.VISIBLE
            } else {
                Logger.d("MainActivity: Hiding bottom navigation")
                binding.bottomNavigation.visibility = android.view.View.GONE
            }
        }
    }
    
    override fun onStart() {
        super.onStart()
        Logger.lifecycle("MainActivity", "onStart")
    }
    
    override fun onResume() {
        super.onResume()
        Logger.lifecycle("MainActivity", "onResume")
    }
    
    override fun onPause() {
        super.onPause()
        Logger.lifecycle("MainActivity", "onPause")
    }
    
    override fun onStop() {
        super.onStop()
        Logger.lifecycle("MainActivity", "onStop")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Logger.lifecycle("MainActivity", "onDestroy")
    }
}