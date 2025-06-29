package uz.dckroff.statisfy.presentation.ui.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.dckroff.statisfy.R
import uz.dckroff.statisfy.databinding.FragmentRegisterBinding
import uz.dckroff.statisfy.presentation.viewmodel.AuthViewModel
import uz.dckroff.statisfy.utils.Logger
import uz.dckroff.statisfy.utils.Result
import uz.dckroff.statisfy.utils.ValidationUtils
import uz.dckroff.statisfy.utils.showToast

/**
 * Фрагмент для регистрации пользователя
 */
@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Logger.lifecycle("RegisterFragment", "onCreateView")
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        setupPasswordStrengthMeter()
        observeViewModel()
    }

    private fun setupViews() {
        Logger.d("RegisterFragment: Setting up views")

        // Кнопка регистрации
        binding.btnRegister.setOnClickListener {
            Logger.d("RegisterFragment: Register button clicked")
            val username = binding.etUsername.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            if (validateInputs(username, email, password, confirmPassword)) {
                Logger.d("RegisterFragment: Input validation passed, attempting registration")
                viewModel.register(username, email, password)
            }
        }

        // Переход на экран входа
        binding.tvLogin.setOnClickListener {
            Logger.d("RegisterFragment: Login text clicked")
            findNavController().navigateUp()
        }

        // Отслеживание изменений пароля для индикатора силы
        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updatePasswordStrengthIndicator(s?.toString() ?: "")
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupPasswordStrengthMeter() {
        // Наблюдаем за силой пароля
        viewModel.passwordStrength.observe(viewLifecycleOwner) { strength ->
//            updatePasswordStrengthUI(strength)
        }
    }

    private fun updatePasswordStrengthIndicator(password: String) {
        Logger.d("RegisterFragment: Updating password strength indicator")
        val strength = ValidationUtils.calculatePasswordStrength(password)

        binding.pbPasswordStrength.progress = strength

        val strengthText = when {
            strength < 25 -> {
                Logger.d("RegisterFragment: Password strength - Very Weak")
                getString(R.string.password_strength_very_weak)
            }

            strength < 50 -> {
                Logger.d("RegisterFragment: Password strength - Weak")
                getString(R.string.password_strength_weak)
            }

            strength < 75 -> {
                Logger.d("RegisterFragment: Password strength - Medium")
                getString(R.string.password_strength_medium)
            }

            else -> {
                Logger.d("RegisterFragment: Password strength - Strong")
                getString(R.string.password_strength_strong)
            }
        }

        binding.tvPasswordStrength.text = strengthText

        val color = when {
            strength == 0 -> R.color.gray
            strength < 25 -> R.color.red
            strength < 50 -> R.color.orange
            strength < 75 -> R.color.yellow
            else -> R.color.green
        }

        binding.pbPasswordStrength.progressTintList =
            ContextCompat.getColorStateList(requireContext(), color)
    }

    private fun observeViewModel() {
        Logger.d("RegisterFragment: Setting up register observers")

        // Наблюдаем за состоянием регистрации
        viewModel.registerState.observe(viewLifecycleOwner) { state ->
            Logger.d("RegisterFragment: Register state changed: $state")
            handleResult(state) { user ->
                Logger.i("RegisterFragment: Registration successful for user: ${user.username}")
            }
        }


        // Наблюдаем за ошибками валидации
        viewModel.usernameError.observe(viewLifecycleOwner) { error ->
            binding.tilUsername.error = error
        }

        viewModel.emailError.observe(viewLifecycleOwner) { error ->
            binding.tilEmail.error = error
        }

        viewModel.passwordError.observe(viewLifecycleOwner) { error ->
            binding.tilPassword.error = error
        }

        viewModel.confirmPasswordError.observe(viewLifecycleOwner) { error ->
            binding.tilConfirmPassword.error = error
        }
    }

    private fun <T> handleResult(result: Result<T>, onSuccess: (T) -> Unit) {
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

    private fun validateInputs(
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        Logger.d("RegisterFragment: Validating inputs")

        if (username.isEmpty()) {
            Logger.w("RegisterFragment: Username is empty")
            binding.tilUsername.error = getString(R.string.error_empty_username)
            return false
        }

        if (email.isEmpty()) {
            Logger.w("RegisterFragment: Email is empty")
            binding.tilEmail.error = getString(R.string.error_empty_email)
            return false
        }

        if (!ValidationUtils.isValidEmail(email)) {
            Logger.w("RegisterFragment: Invalid email format")
            binding.tilEmail.error = getString(R.string.error_invalid_email)
            return false
        }

        if (password.isEmpty()) {
            Logger.w("RegisterFragment: Password is empty")
            binding.tilPassword.error = getString(R.string.error_empty_password)
            return false
        }

        if (ValidationUtils.calculatePasswordStrength(password) < 50) {
            Logger.w("RegisterFragment: Password is too weak")
            binding.tilPassword.error = getString(R.string.error_weak_password)
            return false
        }

        if (confirmPassword.isEmpty()) {
            Logger.w("RegisterFragment: Confirm password is empty")
            binding.tilConfirmPassword.error = getString(R.string.error_empty_confirm_password)
            return false
        }

        if (password != confirmPassword) {
            Logger.w("RegisterFragment: Passwords do not match")
            binding.tilConfirmPassword.error = getString(R.string.error_passwords_not_match)
            return false
        }

        return true
    }

    private fun showLoading(isLoading: Boolean) {
        Logger.d("RegisterFragment: Show loading: $isLoading")
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnRegister.isEnabled = !isLoading
        binding.etUsername.isEnabled = !isLoading
        binding.etEmail.isEnabled = !isLoading
        binding.etPassword.isEnabled = !isLoading
        binding.etConfirmPassword.isEnabled = !isLoading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Logger.lifecycle("RegisterFragment", "onDestroyView")
        _binding = null
    }
} 