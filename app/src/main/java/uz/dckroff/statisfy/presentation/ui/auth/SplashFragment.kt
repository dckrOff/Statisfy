package uz.dckroff.statisfy.presentation.ui.auth

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.dckroff.statisfy.R
import uz.dckroff.statisfy.databinding.FragmentSplashBinding
import uz.dckroff.statisfy.presentation.viewmodel.AuthViewModel
import uz.dckroff.statisfy.utils.Constants
import uz.dckroff.statisfy.utils.Logger

/**
 * Фрагмент для экрана загрузки и проверки авторизации
 */
@AndroidEntryPoint
class SplashFragment : Fragment() {
    
    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: AuthViewModel by viewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Logger.lifecycle("SplashFragment", "onCreateView")
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Logger.lifecycle("SplashFragment", "onViewCreated")
        
        // Задержка для отображения экрана загрузки
        Handler(Looper.getMainLooper()).postDelayed({
            checkAuthAndNavigate()
        }, Constants.SPLASH_DELAY)
    }
    
    /**
     * Проверка авторизации и навигация
     */
    private fun checkAuthAndNavigate() {
        Logger.d("SplashFragment: Checking authentication status")
        if (viewModel.isLoggedIn()) {
            Logger.i("SplashFragment: User is logged in, navigating to home")
            findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
        } else {
            Logger.i("SplashFragment: User is not logged in, navigating to login")
            findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        Logger.lifecycle("SplashFragment", "onDestroyView")
        _binding = null
    }
} 