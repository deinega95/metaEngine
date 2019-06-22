package com.meta_engine.common.di

import android.app.Application

object ComponentsHolder {
    lateinit var applicationComponent: ApplicationComponent
        private set

    private var mainComponent: MainComponent? = null


    fun setup(app: Application) {
        applicationComponent = DaggerApplicationComponent.builder()
            .androidModule(AndroidModule(app)).build()
    }

    fun mainComponent(): MainComponent {
        if (mainComponent == null) mainComponent = applicationComponent.getMainComponent()
        return mainComponent!!
    }
}