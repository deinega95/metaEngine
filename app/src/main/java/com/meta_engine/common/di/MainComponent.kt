package com.meta_engine.common.di

import com.google.gson.Gson
import com.meta_engine.ui.main.MainFragment
import dagger.Subcomponent

@Subcomponent
@MainScope
interface MainComponent {
    fun inject(mainFragment: MainFragment)

    abstract val getGson: Gson

}