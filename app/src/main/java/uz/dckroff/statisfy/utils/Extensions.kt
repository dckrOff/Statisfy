package uz.dckroff.statisfy.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment

/**
 * Показать Toast сообщение из фрагмента
 */
fun Fragment.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Logger.d("Showing toast: $message")
    Toast.makeText(requireContext(), message, duration).show()
}

/**
 * Показать Toast сообщение из контекста
 */
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Logger.d("Showing toast: $message")
    Toast.makeText(this, message, duration).show()
}

/**
 * Скрыть клавиатуру из фрагмента
 */
fun Fragment.hideKeyboard() {
    Logger.d("Hiding keyboard")
    val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val view = requireActivity().currentFocus ?: View(requireContext())
    imm.hideSoftInputFromWindow(view.windowToken, 0)
} 