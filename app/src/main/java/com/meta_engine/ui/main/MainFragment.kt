package com.meta_engine.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.meta_engine.R
import com.meta_engine.base.BaseFragment
import com.meta_engine.common.di.ComponentsHolder
import com.meta_engine.common.utils.Utils
import com.meta_engine.common.utils.UtilsOnJava
import com.meta_engine.model.Marker
import com.meta_engine.model.SearcArea
import kotlinx.android.synthetic.main.fragment_main.*
import javax.inject.Inject

const val DEFAULT_Z_INDEX = 10f
const val BOTTOM_Z_INDEX = 5f
const val TOP_Z_INDEX = 15f
const val DEFAULT_ZOOM = 18f

class MainFragment : BaseFragment(), IMainFragment {


    @Inject
    lateinit var presenter: MainPresenter
    lateinit var map: GoogleMap
    var myMarker: com.google.android.gms.maps.model.Marker? = null
    var myPath: Polyline? = null


    var fragmentView: View? = null

    init {
        ComponentsHolder.mainComponent().inject(this)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (fragmentView == null) {
            fragmentView = inflater.inflate(R.layout.fragment_main, container, false)
        }
        return fragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myPositionButton.setOnClickListener { presenter.onMyPositionClick() }

        (childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment).getMapAsync {
            map = it
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            map.uiSettings.isZoomControlsEnabled = true
            map.uiSettings.isCompassEnabled = true
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(44.988744, 39.113381), DEFAULT_ZOOM))


            presenter.viewReady(this)
        }

    }

    override fun onDestroyView() {
        presenter.viewDied(this)
        super.onDestroyView()
    }

    override fun addMarker(marker: Marker) {
        val latLng = LatLng(marker.lat, marker.lng)
        addMarker(latLng, BitmapDescriptorFactory.fromResource(Utils.getMarkerIcon(marker.type)))
        val circleOptions = CircleOptions()
            .center(latLng)
            .radius(marker.radius.toDouble())
            .strokeColor(Color.TRANSPARENT)
            .fillColor(Utils.getMarkerColor(context!!, marker.type))
            .zIndex(DEFAULT_Z_INDEX)
        map.addCircle(circleOptions)


    }

    private fun addPath(latLngs: List<LatLng>, color: Int): Polyline {
        val polyLineOptions = PolylineOptions()
            .color(color)
            .addAll(latLngs)
            .width(UtilsOnJava.metertopixel(map))
            .zIndex(BOTTOM_Z_INDEX)




        return map.addPolyline(polyLineOptions)
    }


    private fun addMarker(latLng: LatLng, icon: BitmapDescriptor): com.google.android.gms.maps.model.Marker {
        val markerOptions = MarkerOptions()
            .position(latLng)
            .icon(icon)
            .anchor(0.5f, 0.5f)
            .zIndex(TOP_Z_INDEX)
        return map.addMarker(markerOptions)
    }

    override fun showMe(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        if (myMarker == null) {

            myMarker = addMarker(
                latLng
                ,
                BitmapDescriptorFactory.fromResource(R.drawable.ic_me)
            )
            showMyPosition(latLng)

        } else {
            myMarker?.position = LatLng(location.latitude, location.longitude)
        }

        if (myPath == null) {
            myPath = addPath(arrayListOf(latLng), ContextCompat.getColor(context!!, R.color.myPath))
        }else{
            val path = myPath!!.points
            path.add(latLng)
            myPath!!.points = path
        }

    }

    override fun showSearchArea(searcArea: SearcArea) {
        val circleOptions = CircleOptions()
            .center(LatLng(searcArea.lat, searcArea.lng))
            .radius(searcArea.radius.toDouble())
            .strokeColor(ContextCompat.getColor(context!!, R.color.unzone))
            .strokeWidth(600f)
            .fillColor(Color.TRANSPARENT)
            .zIndex(TOP_Z_INDEX)
        map.addCircle(circleOptions)
    }

    override fun showMyPosition(latLng: LatLng) {
        map.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition(latLng, DEFAULT_ZOOM, 0f, 0f)))

    }

    override fun checkGeoPermission() {
        if (ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ), 1
            )
        } else {
            presenter.permissionReceived()
        }
    }

}