package com.mupper.gobus.presentation.ui.screens

import android.Manifest
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.mupper.gobus.R
import com.mupper.gobus.presentation.ui.composables.GoogleMapComposable
import com.mupper.gobus.presentation.ui.dialogs.StartTravelingDialog
import com.mupper.gobus.presentation.ui.dialogs.StopTravelingDialog
import com.mupper.gobus.presentation.viewmodel.MapViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@ExperimentalPermissionsApi
@Composable
fun MainScreen(
    state: MapViewModel.State,
    sideEffect: MapViewModel.SideEffect,
    navController: NavController,
    allPermissionGranted: () -> Unit,
    notAllPermissionGranted: () -> Unit,
    showStartStopTraveling: () -> Unit,
    hideStartTravelingDialog: () -> Unit,
    hideStopTravelingDialog: () -> Unit,
    startTraveling: () -> Unit,
    stopTraveling: () -> Unit,
    retrieveLastKnownLocation: () -> Unit,
    requestPermissions: () -> Unit
) {
    val multiplePermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    if (multiplePermissionState.allPermissionsGranted) allPermissionGranted()
    else notAllPermissionGranted()

    when (sideEffect) {
        MapViewModel.SideEffect.Idle -> {}
        MapViewModel.SideEffect.RequestPermissions -> {
            LaunchedEffect(Unit) {
                multiplePermissionState.launchMultiplePermissionRequest()
            }
        }
        MapViewModel.SideEffect.SetupMap -> {}
    }
    val isTraveling = state.isTraveling
    val startTravelingIsShown = state.startTravelingIsShown
    val stopTravelingIsShown = state.stopTravelingIsShown

    Scaffold(
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            when (state.allPermissionsState) {
                MapViewModel.AllPermissionState.AllPermissionGranted -> {
                    FloatingActionButton(onClick = showStartStopTraveling) {
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
                retrieveLastKnownLocation()
                GoogleMapComposable(state)
            }
            else -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    Button(
                        modifier = Modifier.align(Alignment.Center),
                        onClick = requestPermissions
                    ) {
                        Text("Solicitar permisos")
                    }
                }
            }
        }
        StartTravelingDialog(
            startTravelingIsShown,
            startTraveling,
            hideStartTravelingDialog,
        )
        StopTravelingDialog(
            stopTravelingIsShown,
            stopTraveling,
            hideStopTravelingDialog
        )
    }
}
