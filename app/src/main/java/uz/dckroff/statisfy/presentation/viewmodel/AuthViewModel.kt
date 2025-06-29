package uz.dckroff.statisfy.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.dckroff.statisfy.domain.model.User
import uz.dckroff.statisfy.domain.repository.AuthRepository
import uz.dckroff.statisfy.utils.Logger
import uz.dckroff.statisfy.utils.Result
import uz.dckroff.statisfy.utils.ValidationUtils
import javax.inject.Inject

/**
 * ViewModel для управления аутентификацией
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    // Состояние входа
    private val _loginState = MutableLiveData<Result<User>>()
    val loginState: LiveData<Result<User>> = _loginState
    
    // Состояние регистрации
    private val _registerState = MutableLiveData<Result<User>>()
    val registerState: LiveData<Result<User>> = _registerState
    
    // Состояние восстановления пароля
    private val _forgotPasswordState = MutableLiveData<Result<Unit>>()
    val forgotPasswordState: LiveData<Result<Unit>> = _forgotPasswordState
    
    // Текущий пользователь
    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser
    
    // Ошибки валидации
    private val _emailError = MutableLiveData<String?>()
    val emailError: LiveData<String?> = _emailError
    
    private val _passwordError = MutableLiveData<String?>()
    val passwordError: LiveData<String?> = _passwordError
    
    private val _confirmPasswordError = MutableLiveData<String?>()
    val confirmPasswordError: LiveData<String?> = _confirmPasswordError
    
    private val _usernameError = MutableLiveData<String?>()
    val usernameError: LiveData<String?> = _usernameError
    
    // Сила пароля (от 0 до 100)
    private val _passwordStrength = MutableLiveData<Int>()
    val passwordStrength: LiveData<Int> = _passwordStrength
    
    init {
        Logger.d("AuthViewModel initialized")
        getCurrentUser()
    }
    
    /**
     * Вход пользователя
     */
    fun login(username: String, password: String) {
        Logger.d("AuthViewModel: login() called with username: $username")
        _loginState.value = Result.Loading
        
        viewModelScope.launch {
            Logger.d("AuthViewModel: Executing login in viewModelScope")
            val result = authRepository.login(username, password)
            _loginState.value = result
            
            if (result is Result.Success) {
                Logger.i("AuthViewModel: Login successful")
                getCurrentUser()
            } else if (result is Result.Error) {
                Logger.e("AuthViewModel: Login failed: ${result.message}")
            }
        }
    }
    
    /**
     * Регистрация пользователя
     */
    fun register(username: String, email: String, password: String) {
        Logger.d("AuthViewModel: register() called with username: $username, email: $email")
        _registerState.value = Result.Loading
        
        viewModelScope.launch {
            Logger.d("AuthViewModel: Executing register in viewModelScope")
            val result = authRepository.register(username, email, password)
            _registerState.value = result
            
            if (result is Result.Success) {
                Logger.i("AuthViewModel: Registration successful")
                getCurrentUser()
            } else if (result is Result.Error) {
                Logger.e("AuthViewModel: Registration failed: ${result.message}")
            }
        }
    }
    
    /**
     * Восстановление пароля
     */
    fun forgotPassword(email: String) {
        Logger.d("AuthViewModel: forgotPassword() called with email: $email")
        _forgotPasswordState.value = Result.Loading
        
        viewModelScope.launch {
            Logger.d("AuthViewModel: Executing forgotPassword in viewModelScope")
            val result = authRepository.forgotPassword(email)
            _forgotPasswordState.value = result
            
            if (result is Result.Success) {
                Logger.i("AuthViewModel: Password reset request successful")
            } else if (result is Result.Error) {
                Logger.e("AuthViewModel: Password reset failed: ${result.message}")
            }
        }
    }
    
    /**
     * Выход пользователя
     */
    fun logout() {
        Logger.d("AuthViewModel: logout() called")
        viewModelScope.launch {
            Logger.d("AuthViewModel: Executing logout in viewModelScope")
            authRepository.logout()
            _currentUser.value = null
            Logger.i("AuthViewModel: User logged out")
        }
    }
    
    /**
     * Получить текущего пользователя
     */
    private fun getCurrentUser() {
        Logger.d("AuthViewModel: getCurrentUser() called")
        viewModelScope.launch {
            Logger.d("AuthViewModel: Collecting current user flow")
            authRepository.getCurrentUserFlow().collectLatest { user ->
                _currentUser.value = user
                Logger.d("AuthViewModel: Current user updated: ${user?.username ?: "null"}")
            }
        }
    }
    
    /**
     * Проверить, авторизован ли пользователь
     */
    fun isLoggedIn(): Boolean {
        val loggedIn = authRepository.isLoggedIn()
        Logger.d("AuthViewModel: isLoggedIn() = $loggedIn")
        return loggedIn
    }
    
    /**
     * Валидация полей для входа
     */
    private fun validateLoginInput(email: String, password: String): Boolean {
        var isValid = true
        
        if (!validateEmail(email)) {
            isValid = false
        }
        
        if (password.isEmpty()) {
            _passwordError.value = "Введите пароль"
            isValid = false
        } else {
            _passwordError.value = null
        }
        
        return isValid
    }
    
    /**
     * Валидация полей для регистрации
     */
    private fun validateRegisterInput(
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        var isValid = true
        
        if (!ValidationUtils.isValidUsername(username)) {
            _usernameError.value = "Имя пользователя должно содержать минимум 3 символа"
            isValid = false
        } else {
            _usernameError.value = null
        }
        
        if (!validateEmail(email)) {
            isValid = false
        }
        
        if (!ValidationUtils.isValidPassword(password)) {
            _passwordError.value = "Пароль должен содержать минимум 8 символов"
            isValid = false
        } else {
            _passwordError.value = null
        }
        
        if (!ValidationUtils.doPasswordsMatch(password, confirmPassword)) {
            _confirmPasswordError.value = "Пароли не совпадают"
            isValid = false
        } else {
            _confirmPasswordError.value = null
        }
        
        return isValid
    }
    
    /**
     * Валидация email
     */
    private fun validateEmail(email: String): Boolean {
        return if (!ValidationUtils.isValidEmail(email)) {
            _emailError.value = "Введите корректный email"
            false
        } else {
            _emailError.value = null
            true
        }
    }
    
    /**
     * Проверка силы пароля
     */
    fun checkPasswordStrength(password: String) {
        _passwordStrength.value = ValidationUtils.calculatePasswordStrength(password)
    }
} 