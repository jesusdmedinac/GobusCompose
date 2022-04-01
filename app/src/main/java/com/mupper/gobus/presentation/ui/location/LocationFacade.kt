package com.mupper.gobus.presentation.ui.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LocationFacade @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val fusedLocationProviderClient: FusedLocationProviderClient
) {
    @ExperimentalCoroutinesApi
    fun lastCollectionFlow(): Flow<Location> = channelFlow {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.locations.forEachByIndex { kotlin.runCatching { it }.getOrDefault(false) }
            }
        }
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.lastLocation.await<Location?>()?.let { send(it) }
            fusedLocationProviderClient.requestLocationUpdates(
                LocationRequest.create().apply {
                    interval = 10000
                    fastestInterval = 5000
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                },
                locationCallback,
                Looper.getMainLooper()
            ).await()
            awaitClose {
                fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            }
        }
    }
}