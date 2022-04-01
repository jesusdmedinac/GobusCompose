package com.mupper.gobus.presentation.ui.screens.composables

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.mupper.gobus.presentation.viewmodel.MapViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@ExperimentalPermissionsApi
@Composable
fun GoogleMapComposable(
    mapViewModelState: MapViewModel.State,
) {
    val userPosition = mapViewModelState.latLng.toGoogleMapsLatLng()
    val cameraPositionState = CameraPositionState(
        position = CameraPosition.fromLatLngZoom(userPosition, 14f)
    )
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        Marker(position = userPosition, title = "Position", snippet = "User Position")
    }
}

@ExperimentalCoroutinesApi
@ExperimentalPermissionsApi
private fun MapViewModel.LatLng.toGoogleMapsLatLng(): LatLng =
    LatLng(latitude, longitude)