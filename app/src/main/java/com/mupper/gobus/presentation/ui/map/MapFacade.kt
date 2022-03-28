package com.mupper.gobus.presentation.ui.map

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.mupper.gobus.presentation.viewmodel.MapViewModel
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@ExperimentalPermissionsApi
class MapFacade(
    private val map: MapView,
) {
    fun moveMap() {
        val mapController = map.controller
        mapController.setZoom(9.5)
        val startPoint = GeoPoint(0.0, 0.0)
        mapController.setCenter(startPoint)

        val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(map.context), map)
        locationOverlay.enableMyLocation()
        map.overlays.add(locationOverlay)
    }

    fun onResume() {
        map.onResume()
    }

    fun onPause() {
        map.onPause()
    }

    fun moveMapTo(geoPoint: GeoPoint) {
        val mapController = map.controller
        mapController.setCenter(geoPoint)
    }
}