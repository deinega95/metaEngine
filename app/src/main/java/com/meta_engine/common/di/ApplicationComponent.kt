package com.meta_engine.common.di

import com.meta_engine.base.BaseActivity
import com.meta_engine.common.network.MainInterceptor
import dagger.Component
import javax.inject.Singleton

@Component(modules = arrayOf(AndroidModule::class))
@Singleton
interface ApplicationComponent {
    fun inject(mainInterceptor: MainInterceptor)
    fun inject(mainInterceptor: BaseActivity)
    fun getMainComponent(): MainComponent
}