package uz.dckroff.statisfy.presentation.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.dckroff.statisfy.R
import uz.dckroff.statisfy.databinding.FragmentForgotPasswordBinding
import uz.dckroff.statisfy.presentation.viewmodel.AuthViewModel
import uz.dckroff.statisfy.utils.Logger
import uz.dckroff.statisfy.utils.Result
import uz.dckroff.statisfy.utils.ValidationUtils

/**
 * Фрагмент для восстановления пароля
 */
@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {
    
    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: AuthViewModel by viewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Logger.lifecycle("ForgotPasswordFragment", "onCreateView")
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupListeners()
        observeViewModel()
    }
    
    private fun setupListeners() {
        // Кнопка отправки
        binding.btnSend.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            if (validateEmail(email)) {
                Logger.d("ForgotPasswordFragment: Email validation passed, attempting password reset")
                viewModel.forgotPassword(email)
            }
        }
        
        // Возврат на экран входа
        binding.tvBackToLogin.setOnClickListener {
            Logger.d("ForgotPasswordFragment: Back button clicked")
            findNavController().navigateUp()
        }
    }
    
    private fun observeViewModel() {
        // Наблюдаем за состоянием восстановления пароля
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.forgotPasswordState.observe(viewLifecycleOwner) { state ->
                Logger.d("ForgotPasswordFragment: Forgot password state changed: $state")
                handleResult(state) {
                    Logger.i("ForgotPasswordFragment: Password reset request sent successfully")
                    showSuccessMessage()
                }
            }
        }
        
        // Наблюдаем за ошибками валидации
        viewModel.emailError.observe(viewLifecycleOwner) { error ->
            binding.tilEmail.error = error
        }
    }
    
    private fun validateEmail(email: String): Boolean {
        Logger.d("ForgotPasswordFragment: Validating email")
        
        if (email.isEmpty()) {
            Logger.w("ForgotPasswordFragment: Email is empty")
            binding.etEmail.error = "Введите email"
            return false
        }
        
        if (!ValidationUtils.isValidEmail(email)) {
            Logger.w("ForgotPasswordFragment: Invalid email format")
            binding.etEmail.error = "Некорректный формат email"
            return false
        }
        
        return true
    }
    
    private fun showSuccessMessage() {
        Logger.d("ForgotPasswordFragment: Showing success message")
//        binding.layoutForm.isVisible = false // TODO
//        binding.layoutSuccess.isVisible = true
    }
    
    private fun showLoading(isLoading: Boolean) {
        Logger.d("ForgotPasswordFragment: Show loading: $isLoading")
//        binding.progressBar.isVisible = isLoading // TODO
        binding.btnSend.isEnabled = !isLoading
        binding.etEmail.isEnabled = !isLoading
    }
    
    private fun handleResult(state: Result<*>, onSuccess: () -> Unit) {
        when (state) {
            is Result.Loading -> {
                showLoading(true)
            }
            is Result.Success -> {
                showLoading(false)
                onSuccess()
            }
            is Result.Error -> {
                showLoading(false)
                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        Logger.lifecycle("ForgotPasswordFragment", "onDestroyView")
        _binding = null
    }
} 