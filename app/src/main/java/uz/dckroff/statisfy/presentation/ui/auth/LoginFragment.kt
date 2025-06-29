package uz.dckroff.statisfy.presentation.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.dckroff.statisfy.R
import uz.dckroff.statisfy.databinding.FragmentLoginBinding
import uz.dckroff.statisfy.presentation.viewmodel.AuthViewModel
import uz.dckroff.statisfy.utils.Logger
import uz.dckroff.statisfy.utils.Result
import uz.dckroff.statisfy.utils.ValidationUtils
import uz.dckroff.statisfy.utils.hideKeyboard
import uz.dckroff.statisfy.utils.showToast

/**
 * Фрагмент для входа пользователя
 */
@AndroidEntryPoint
class LoginFragment : BaseAuthFragment() {

    private val viewModel: AuthViewModel by viewModels()
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Logger.lifecycle("LoginFragment", "onViewCreated")

        setupListeners()
        observeViewModel()
    }

    override fun setupViews() {
        Logger.d("LoginFragment: Setting up views")
        // Обновляем подсказку для поля ввода - теперь это поле для логина/имени пользователя
        binding.tilEmail.hint = getString(R.string.username)
    }

    /**
     * Настройка слушателей
     */
    private fun setupListeners() {
        Logger.d("LoginFragment: Setting up listeners")

        // Кнопка входа
        binding.btnLogin.setOnClickListener {
            Logger.d("LoginFragment: Login button clicked")
            attemptLogin()
        }

        // Переход к регистрации
        binding.tvRegister.setOnClickListener {
            Logger.d("LoginFragment: Register text clicked")
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        // Переход к восстановлению пароля
        binding.tvForgotPassword.setOnClickListener {
            Logger.d("LoginFragment: Forgot password text clicked")
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }

        // Валидация email при изменении
        binding.etEmail.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val email = binding.etEmail.text.toString()
                if (email.isNotEmpty() && !ValidationUtils.isValidEmail(email)) {
                    binding.tilEmail.error = getString(R.string.invalid_email)
                } else {
                    binding.tilEmail.error = null
                }
            }
        }
    }

    /**
     * Наблюдение за ViewModel
     */
    private fun observeViewModel() {
        Logger.d("LoginFragment: Setting up ViewModel observers")

        // Состояние входа
        viewModel.loginState.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    Logger.d("LoginFragment: Login state - Loading")
                    showLoading(true)
                }

                is Result.Success -> {
                    Logger.d("LoginFragment: Login state - Success")
                    showLoading(false)
                    // Навигация будет выполнена через наблюдение за currentUser в BaseAuthFragment
                }

                is Result.Error -> {
                    Logger.e("LoginFragment: Login state - Error: ${result.message}")
                    showLoading(false)
                    showToast(result.message)
                }
            }
        }

        // Ошибки валидации
        viewModel.emailError.observe(viewLifecycleOwner) { error ->
            binding.tilEmail.error = error
        }

        viewModel.passwordError.observe(viewLifecycleOwner) { error ->
            binding.tilPassword.error = error
        }
    }

    /**
     * Попытка входа
     */
    private fun attemptLogin() {
        Logger.d("LoginFragment: Attempting login")

        val username = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        // Проверка полей
        if (username.isEmpty()) {
            binding.tilEmail.error = getString(R.string.username_required)
            return
        } else {
            binding.tilEmail.error = null
        }

        if (password.isEmpty()) {
            binding.tilPassword.error = getString(R.string.password_required)
            return
        } else {
            binding.tilPassword.error = null
        }

        // Скрыть клавиатуру
        hideKeyboard()

        // Запуск входа
        viewModel.login(username, password)
    }

    /**
     * Показать/скрыть индикатор загрузки
     */
    override fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !isLoading
        binding.etEmail.isEnabled = !isLoading
        binding.etPassword.isEnabled = !isLoading
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 