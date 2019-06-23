package com.meta_engine.ui.main

import com.google.android.gms.maps.model.LatLng
import com.meta_engine.base.BasePresenter
import com.meta_engine.common.di.MainScope
import com.meta_engine.common.services.NearbyService
import com.meta_engine.common.utils.MyLog
import com.meta_engine.common.utils.Utils
import com.meta_engine.model.Coordinates
import com.meta_engine.model.Human
import com.meta_engine.model.HumanType
import com.meta_engine.model.SearcArea
import com.meta_engine.repositories.MarkersRepository
import com.meta_engine.services.GeoPositionService
import com.meta_engine.services.LOCATION_UPDATES_INTERVAL
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random


@MainScope
class MainPresenter @Inject constructor() : BasePresenter<IMainFragment>() {
    @Inject
    lateinit var markersRepository: MarkersRepository
    @Inject
    lateinit var geoService: GeoPositionService
    @Inject
    lateinit var nearbyService: NearbyService

    var me = Human(Utils.getNewID(), HumanType.RESCUER, ArrayList<Coordinates>())

    var lastLat = 45.021422
    var lastLng = 39.030435

    override fun viewAttached() {
        getData()
        getGeoPosition()
        getSearchArea()
        getOther()
    }

    private fun getOther() =launch{
        while (true){
            delay(LOCATION_UPDATES_INTERVAL)
            lastLat-= Random.nextDouble(0.000001, 0.0001)
            lastLng+= Random.nextDouble(0.000001, 0.0001)
            view?.showOther(LatLng(lastLat,lastLng))
        }
    }

    private fun getSearchArea() {
        view?.showSearchArea(SearcArea(1000, 45.020422,39.042635))
    }

    private fun getGeoPosition() {
        view?.checkGeoPermission()
    }

    private fun getData() = launch {
        markersRepository.getData().forEach {
            view?.addMarker(it)
        }
    }

    override fun viewDettached() {
        nearbyService.stopAllEndpoints()
        geoService.unsubscribe()
    }

    fun permissionReceived() {
        geoService.subscribe {
            me.path.add(Coordinates(it.latitude, it.longitude))
            view?.showMe(it)
            MyLog.show("subscribe result")
        }

        nearbyService.connect()
    }

    fun onMyPositionClick() {
        if (me.path.size > 0) {
            val coordinates = me.path.get(me.path.lastIndex)
            view?.showMyPosition(LatLng(coordinates.lat, coordinates.lng))
        }
    }
}