package com.mupper.gobus.presentation.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.mupper.gobus.presentation.ui.map.MapFacade
import com.mupper.gobus.presentation.viewmodel.MapViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

@ExperimentalCoroutinesApi
@ExperimentalPermissionsApi
@Composable
fun MainScreen(
    onMapFacadeCreated: (MapFacade) -> Unit,
    mapViewModel: MapViewModel,
) {
    var mapFacade: MapFacade? by remember { mutableStateOf(null) }
    val state by mapViewModel.container.stateFlow.collectAsState()

    Scaffold {
        when (state.allPermissionsState) {
            MapViewModel.AllPermissionState.AllPermissionGranted -> {
                LaunchedEffect(Unit) {
                    mapViewModel.retrieveLastKnownLocation()
                }
                AndroidView(
                    factory = {
                        MapView(it).apply {
                            mapFacade = MapFacade(this)
                                .apply {
                                    onMapFacadeCreated(this)
                                    moveMap()
                                }
                        }
                    },
                    update = {
                        val geoPoint = GeoPoint(state.latLng.latitude, state.latLng.longitude)
                        mapFacade?.moveMapTo(geoPoint)
                    }
                )
            }
            else -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    Button(
                        modifier = Modifier.align(Alignment.Center),
                        onClick = { mapViewModel.requestPermissions() }) {
                        Text("Solicitar permisos")
                    }
                }
            }
        }
    }
}