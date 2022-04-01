package com.mupper.gobus.presentation.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.mupper.gobus.R
import com.mupper.gobus.presentation.ui.screens.composables.GoogleMapComposable
import com.mupper.gobus.presentation.viewmodel.MapViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@ExperimentalPermissionsApi
@Composable
fun MainScreen(
    mapViewModel: MapViewModel,
) {
    val state by mapViewModel.container.stateFlow.collectAsState()
    val isTraveling = state.isTraveling

    Scaffold(
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            when (state.allPermissionsState) {
                MapViewModel.AllPermissionState.AllPermissionGranted -> {
                    FloatingActionButton(onClick = mapViewModel::startTraveling) {
                        val drawableId = if (isTraveling) R.drawable.ic_stop
                        else R.drawable.ic_play

                        Icon(painterResource(drawableId), contentDescription = "Start travel")
                    }
                }
                else -> {}
            }
        }
    ) {
        when (state.allPermissionsState) {
            MapViewModel.AllPermissionState.AllPermissionGranted -> {
                LaunchedEffect(Unit) {
                    mapViewModel.retrieveLastKnownLocation()
                }
                GoogleMapComposable(state)
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
