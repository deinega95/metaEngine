package com.meta_engine

import android.app.Application
import com.meta_engine.common.di.ComponentsHolder

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        ComponentsHolder.setup(this)
    }

}