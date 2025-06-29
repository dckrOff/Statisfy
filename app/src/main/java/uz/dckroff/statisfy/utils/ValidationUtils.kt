package uz.dckroff.statisfy.utils

import android.util.Patterns
import java.util.regex.Pattern

/**
 * Утилитарный класс для валидации полей ввода
 */
object ValidationUtils {
    
    // Регулярное выражение для проверки сложности пароля
    private val PASSWORD_PATTERN = Pattern.compile(
        "^" +               // начало строки
            "(?=.*[0-9])" +         // хотя бы одна цифра
            "(?=.*[a-z])" +         // хотя бы одна строчная буква
            "(?=.*[A-Z])" +         // хотя бы одна заглавная буква
            "(?=.*[@#$%^&+=!])" +   // хотя бы один специальный символ
            "(?=\\S+$)" +           // без пробелов
            ".{8,}" +               // минимум 8 символов
            "$"                     // конец строки
    )
    
    /**
     * Проверка валидности email
     */
    fun isValidEmail(email: String): Boolean {
        Logger.d("ValidationUtils: Checking email validity: $email")
        val isValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        Logger.d("ValidationUtils: Email validity result: $isValid")
        return isValid
    }
    
    /**
     * Проверка сложности пароля
     */
    fun isStrongPassword(password: String): Boolean {
        Logger.d("ValidationUtils: Checking password strength")
        val isStrong = PASSWORD_PATTERN.matcher(password).matches()
        Logger.d("ValidationUtils: Password strength result: $isStrong")
        return isStrong
    }
    
    /**
     * Расчет силы пароля (0-100)
     */
    fun calculatePasswordStrength(password: String): Int {
        Logger.d("ValidationUtils: Calculating password strength")
        
        if (password.isEmpty()) {
            Logger.d("ValidationUtils: Empty password, strength: 0")
            return 0
        }
        
        var score = 0
        
        // Длина пароля (макс. 25 баллов)
        score += minOf(25, password.length * 2)
        
        // Наличие цифр (10 баллов)
        if (password.any { it.isDigit() }) {
            score += 10
        }
        
        // Наличие строчных букв (10 баллов)
        if (password.any { it.isLowerCase() }) {
            score += 10
        }
        
        // Наличие заглавных букв (15 баллов)
        if (password.any { it.isUpperCase() }) {
            score += 15
        }
        
        // Наличие специальных символов (20 баллов)
        if (password.any { !it.isLetterOrDigit() }) {
            score += 20
        }
        
        // Разнообразие символов (макс. 20 баллов)
        val uniqueChars = password.toSet().size
        score += minOf(20, uniqueChars * 2)
        
        // Ограничение максимального значения до 100
        val finalScore = minOf(100, score)
        Logger.d("ValidationUtils: Final password strength: $finalScore")
        
        return finalScore
    }
    
    /**
     * Валидация пароля
     */
    fun isValidPassword(password: String): Boolean {
        return password.length >= 8
    }
    
    /**
     * Валидация имени пользователя
     */
    fun isValidUsername(username: String): Boolean {
        return username.length >= 3
    }
    
    /**
     * Проверка совпадения паролей
     */
    fun doPasswordsMatch(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }
} 