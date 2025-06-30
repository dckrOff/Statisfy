package uz.dckroff.statisfy.utils

import android.view.View

/**
 * Сделать View видимым
 */
fun View.visible() {
    visibility = View.VISIBLE
}

/**
 * Скрыть View (с сохранением места в layout)
 */
fun View.invisible() {
    visibility = View.INVISIBLE
}

/**
 * Скрыть View (без сохранения места в layout)
 */
fun View.gone() {
    visibility = View.GONE
} 