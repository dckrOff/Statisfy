package uz.dckroff.statisfy.utils

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

/**
 * Утилиты для работы с View
 */

/**
 * Показать View
 */
fun View.show() {
    visibility = View.VISIBLE
}

/**
 * Скрыть View
 */
fun View.hide() {
    visibility = View.GONE
}

/**
 * Скрыть View (сделать невидимым, но оставить место)
 */
fun View.invisible() {
    visibility = View.INVISIBLE
}

/**
 * Показать или скрыть View в зависимости от условия
 */
fun View.showIf(condition: Boolean) {
    visibility = if (condition) View.VISIBLE else View.GONE
}

/**
 * Показать или сделать невидимым View в зависимости от условия
 */
fun View.invisibleIf(condition: Boolean) {
    visibility = if (condition) View.INVISIBLE else View.VISIBLE
}

/**
 * Переключить видимость View
 */
fun View.toggleVisibility() {
    visibility = if (visibility == View.VISIBLE) View.GONE else View.VISIBLE
}

/**
 * Проверить, видим ли View
 */
fun View.isVisible(): Boolean {
    return visibility == View.VISIBLE
}

/**
 * Установить обработчик нажатия с защитой от множественных нажатий
 */
fun View.setOnSingleClickListener(delay: Long = 500L, action: (View) -> Unit) {
    setOnClickListener(object : View.OnClickListener {
        private var lastClickTime = 0L
        
        override fun onClick(v: View) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime > delay) {
                lastClickTime = currentTime
                action(v)
            }
        }
    })
}

/**
 * Загрузить изображение с помощью Glide
 */
fun ImageView.loadImage(
    url: String?,
    placeholder: Int = 0,
    error: Int = 0
) {
    Glide.with(this)
        .load(url)
        .apply {
            if (placeholder != 0) placeholder(placeholder)
            if (error != 0) error(error)
        }
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}

/**
 * Загрузить изображение с круглой обрезкой
 */
fun ImageView.loadCircleImage(
    url: String?,
    placeholder: Int = 0,
    error: Int = 0
) {
    Glide.with(this)
        .load(url)
        .apply {
            if (placeholder != 0) placeholder(placeholder)
            if (error != 0) error(error)
        }
        .circleCrop()
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}