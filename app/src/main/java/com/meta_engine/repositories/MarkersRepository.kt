package com.meta_engine.repositories

import com.meta_engine.common.di.MainScope
import com.meta_engine.common.utils.Utils
import com.meta_engine.model.Marker
import com.meta_engine.model.MarkerType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@MainScope
class MarkersRepository @Inject constructor() {
    private var data: List<Marker>? = null


    suspend fun getData() = withContext(Dispatchers.IO) {
        if (data == null) data = arrayListOf(Marker(Utils.getNewID(), 45.020422,39.032635, MarkerType.FIRE, 80 ),Marker(Utils.getNewID(), 45.021422,39.031635, MarkerType.WATER, 40 ))

        data!!
    }

}