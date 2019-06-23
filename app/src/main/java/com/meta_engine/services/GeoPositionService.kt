package com.meta_engine.services

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.meta_engine.common.utils.MyLog

const val LOCATION_UPDATES_INTERVAL = 1000 * 3L
const val ACCURACY = 2000
const val PATH_WIDTH = 20

class GeoPositionService(context: Context) {
    private var subscriber: ((Location) -> Unit)? = null
    val locationClient = LocationServices.getFusedLocationProviderClient(context)
    val locationCallback by lazy {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                MyLog.show("location received " + locationResult.lastLocation.toString())
                if (locationResult.lastLocation.accuracy <= ACCURACY) subscriber?.invoke(locationResult.lastLocation)
            }
        }
    }


    @SuppressLint("MissingPermission")
    fun subscribe(callback: (Location) -> Unit) {
        MyLog.show("subscribe")
        this.subscriber = callback
        val locationRequest = LocationRequest.create()
        locationRequest.interval = LOCATION_UPDATES_INTERVAL
        locationRequest.fastestInterval = LOCATION_UPDATES_INTERVAL
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationClient.requestLocationUpdates(
            locationRequest, locationCallback,
            null
        )
    }

    fun unsubscribe() {
        locationClient.removeLocationUpdates(locationCallback)
    }


}