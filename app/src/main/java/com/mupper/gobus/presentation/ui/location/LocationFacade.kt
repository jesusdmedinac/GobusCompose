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

    /**
     * Iterates the receiver [List] using an index instead of an [Iterator] like [forEach] would do.
     * Using this function saves an [Iterator] allocation, which is good for immutable lists or usages
     * confined to a single thread like UI thread only use.
     * However, this method will not detect concurrent modification, except if the size of the list
     * changes on an iteration as a result, which may lead to unpredictable behavior.
     *
     * @param action the action to invoke on each list element.
     */
    private inline fun <T> List<T>.forEachByIndex(action: (T) -> Unit) {
        val initialSize = size
        for (i in 0..lastIndex) {
            if (size != initialSize) throw ConcurrentModificationException()
            action(get(i))
        }
    }
}