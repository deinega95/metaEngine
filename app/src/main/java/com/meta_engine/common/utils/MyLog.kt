package com.meta_engine.common.utils

import android.util.Log
import com.meta_engine.BuildConfig

object MyLog {
    private val TAG = "metaEngine"


    fun show(s: String?) {
        show(s, TAG)
    }


    fun show(s: String?, tag: String) {
        if (BuildConfig.DEBUG)
            Log.e(tag, s)
    }


}