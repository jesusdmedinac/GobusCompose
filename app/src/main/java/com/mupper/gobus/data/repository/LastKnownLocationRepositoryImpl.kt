package com.mupper.gobus.data.repository

import android.location.Location
import com.mupper.gobus.domain.models.LatLng
import com.mupper.gobus.presentation.ui.location.LocationFacade
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ExperimentalCoroutinesApi
class LocationRepository @Inject constructor(
    private val locationFacade: LocationFacade
) {
    fun locationUpdates(): Flow<LatLng> =
        locationFacade.locationUpdates()
            .map { it.toDomainLatLng() }

    suspend fun lastKnownLocation(): LatLng =
        locationFacade.lastKnownLocation()?.toDomainLatLng()
            ?: run { LatLng(0.0, 0.0) }

    private fun Location.toDomainLatLng() =
        LatLng(latitude, longitude)
}