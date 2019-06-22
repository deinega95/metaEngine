package com.meta_engine.base

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment


abstract class BaseFragment : Fragment() {


    fun setResult(args: Bundle) {
        if (activity is ResultHandler) (activity as ResultHandler).handleResult(args)
    }

    fun setTitle(@StringRes title: Int) {
        activity?.setTitle(title)
    }

}

enum class Position {
    BOTTOM, TOP
}