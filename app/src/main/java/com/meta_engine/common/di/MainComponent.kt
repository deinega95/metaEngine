package com.meta_engine.common.di

import com.google.gson.Gson
import dagger.Subcomponent

@Subcomponent
@MainScope
interface MainComponent {
    abstract val getGson: Gson

}