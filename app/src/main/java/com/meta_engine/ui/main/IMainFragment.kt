package com.meta_engine.ui.main

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.meta_engine.model.Marker
import com.meta_engine.model.SearcArea

interface IMainFragment {
    fun addMarker(it: Marker)
    fun showMe(location: Location)
    fun checkGeoPermission()
    fun showMyPosition(latLng: LatLng)
    fun showSearchArea(searcArea: SearcArea)
    fun showOther(latLng: LatLng)
}