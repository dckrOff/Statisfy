package uz.dckroff.statisfy.presentation.ui.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.dckroff.statisfy.R
import uz.dckroff.statisfy.presentation.viewmodel.AuthViewModel
import uz.dckroff.statisfy.utils.Logger
import uz.dckroff.statisfy.utils.Result
import uz.dckroff.statisfy.utils.showToast

/**
 * Базовый фрагмент для экранов аутентификации
 */
@AndroidEntryPoint
abstract class BaseAuthFragment : Fragment() {
    
    protected val authViewModel: AuthViewModel by activityViewModels()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Logger.lifecycle(this.javaClass.simpleName, "onViewCreated")
        
        setupViews()
        setupObservers()
    }
    
    /**
     * Настройка представлений
     */
    protected abstract fun setupViews()
    
    /**
     * Настройка наблюдателей
     */
    protected open fun setupObservers() {
        Logger.d("${this.javaClass.simpleName}: Setting up observers")
        
        // Наблюдение за текущим пользователем
        authViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                Logger.d("${this.javaClass.simpleName}: Current user observed: ${user.username}")
                navigateToHome()
            } else {
                Logger.d("${this.javaClass.simpleName}: No current user observed")
            }
        }
    }
    
    /**
     * Навигация на главный экран
     */
    private fun navigateToHome() {
        Logger.d("${this.javaClass.simpleName}: Navigating to home screen")
        try {
            // Проверяем, находимся ли мы уже на главном экране
            val currentDestination = findNavController().currentDestination
            if (currentDestination?.id == R.id.homeFragment) {
                Logger.d("${this.javaClass.simpleName}: Already on home screen, no navigation needed")
                return
            }
            
            // Выбираем правильное действие навигации в зависимости от текущего фрагмента
            when (currentDestination?.id) {
                R.id.loginFragment -> findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                R.id.registerFragment -> findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
                R.id.splashFragment -> findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
                else -> {
                    // Если нет прямого действия, навигируем напрямую к homeFragment
                    Logger.d("${this.javaClass.simpleName}: No direct action found, navigating directly to homeFragment")
                    findNavController().navigate(R.id.homeFragment)
                }
            }
        } catch (e: Exception) {
            Logger.e("${this.javaClass.simpleName}: Navigation error", e)
        }
    }
    
    /**
     * Обработка состояния результата
     */
    protected fun <T> handleResult(result: Result<T>, onSuccess: (T) -> Unit) {
        when (result) {
            is Result.Loading -> {
                Logger.d("${this.javaClass.simpleName}: Loading state")
                showLoading(true)
            }
            is Result.Success -> {
                Logger.d("${this.javaClass.simpleName}: Success state")
                showLoading(false)
                onSuccess(result.data)
            }
            is Result.Error -> {
                Logger.e("${this.javaClass.simpleName}: Error state: ${result.message}")
                showLoading(false)
                showToast(result.message)
            }
        }
    }
    
    /**
     * Показать/скрыть индикатор загрузки
     */
    protected abstract fun showLoading(isLoading: Boolean)
    
    override fun onStart() {
        super.onStart()
        Logger.lifecycle(this.javaClass.simpleName, "onStart")
    }
    
    override fun onResume() {
        super.onResume()
        Logger.lifecycle(this.javaClass.simpleName, "onResume")
    }
    
    override fun onPause() {
        super.onPause()
        Logger.lifecycle(this.javaClass.simpleName, "onPause")
    }
    
    override fun onStop() {
        super.onStop()
        Logger.lifecycle(this.javaClass.simpleName, "onStop")
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        Logger.lifecycle(this.javaClass.simpleName, "onDestroyView")
    }
} 