package com.meta_engine.common.utils

import android.app.Activity
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Handler
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import java.text.SimpleDateFormat
import java.util.*


object Utils {
    private var dateFormat: SimpleDateFormat? = null
    private var pattern: String? = null

    fun formatDate(date: Date?, pattern: String): String {
        return getFormatter(pattern).format(date)
    }

    private fun getFormatter(pattern: String): SimpleDateFormat {
        if (pattern != Utils.pattern) {
            Utils.pattern = pattern
            dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        }
        return dateFormat!!
    }


    fun showSoftKeyboard(context: Context, editText: EditText) {
        Handler().postDelayed({
            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        }, 100)
    }

    fun hideSoftKeyboard(activity: Activity): Boolean {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity.currentFocus
        if (view == null) {
            view = View(activity)
        }
        return imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun hideSoftKeyboardFrom(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


    fun changeIconColor(context: Context, icon: Drawable, @ColorRes color: Int): Drawable {
        var icon = icon
        icon = DrawableCompat.wrap(icon).mutate()
        DrawableCompat.setTintList(icon, ContextCompat.getColorStateList(context, color))
        DrawableCompat.setTintMode(icon, PorterDuff.Mode.SRC_IN)
        return icon
    }

}