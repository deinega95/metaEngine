package com.meta_engine.common.utils

import android.content.Context
import android.telephony.PhoneNumberUtils
import android.util.TypedValue
import java.util.*

fun String.formatToDigitsNumber(): String {
    var result = ""
    this.toCharArray().forEach {
        if (it.toInt() in 48..57) result += it
    }
    return result
}

fun Int.convertDpToPixels(context: Context): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics)
        .toInt()
}

fun String.formatPhone(): String {
    if (this.isEmpty()) return this
    val result = PhoneNumberUtils.formatNumber(this, Locale.getDefault().getCountry())
    return if (result == null) this
    else result
}