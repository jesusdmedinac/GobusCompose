package com.mupper.gobus.data.repository

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
    fun lastKnownLatLng(): Flow<LatLng> =
        locationFacade.lastCollectionFlow()
            .map { LatLng(it.latitude, it.longitude) }
}