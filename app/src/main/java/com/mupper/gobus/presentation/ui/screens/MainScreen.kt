package com.mupper.gobus.presentation.ui.screens

import android.Manifest
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.mupper.gobus.R
import com.mupper.gobus.presentation.ui.composables.GoogleMapComposable
import com.mupper.gobus.presentation.ui.dialogs.NewTravelDialog
import com.mupper.gobus.presentation.ui.dialogs.StartTravelingDialog
import com.mupper.gobus.presentation.ui.dialogs.StopTravelingDialog
import com.mupper.gobus.presentation.viewmodel.MapViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.orbitmvi.orbit.Container

@ExperimentalComposeUiApi
@ExperimentalCoroutinesApi
@ExperimentalPermissionsApi
@Composable
fun MainScreen(
    container: Container<MapViewModel.State, MapViewModel.SideEffect>,
    permissionEvents: MapViewModel.PermissionEvents,
    controlDialogEvents: MapViewModel.ControlDialogEvents,
    moveMapCameraToUserLastKnownLocation: () -> Unit,
    mainScreenDialogs: MainScreenDialogs
) {
    val multiplePermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    if (multiplePermissionState.allPermissionsGranted) permissionEvents.onAllPermissionGranted()
    else permissionEvents.onNotAllPermissionGranted()

    val state by container.stateFlow.collectAsState()
    val sideEffect by container.sideEffectFlow.collectAsState(initial = MapViewModel.SideEffect.Idle)

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

    Scaffold(
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            when (state.allPermissionsState) {
                MapViewModel.AllPermissionState.AllPermissionGranted -> {
                    FloatingActionButton(
                        onClick = isTraveling.showStartOrStopTravelingDialog(
                            controlDialogEvents.showStopTravelingDialog,
                            controlDialogEvents.showStartTravelingDialog,
                        )
                    ) {
                        val drawableId = isTraveling.getStartOrStopDrawable()

                        Icon(painterResource(drawableId), contentDescription = "Start travel")
                    }
                }
                else -> {}
            }
        }
    ) {
        when (state.allPermissionsState) {
            MapViewModel.AllPermissionState.AllPermissionGranted -> {
                moveMapCameraToUserLastKnownLocation()
                GoogleMapComposable(state)
            }
            else -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    Button(
                        modifier = Modifier.align(Alignment.Center),
                        onClick = permissionEvents.onPermissionRequested
                    ) {
                        Text("Solicitar permisos")
                    }
                }
            }
        }

        mainScreenDialogs.ComposeDialogs()
    }
}

interface MainScreenDialogs {
    @Composable
    fun ComposeDialogs() {
        startTravelingDialog()
        stopTravelingDialog()
        newTravelDialog()
    }

    val startTravelingDialog: @Composable () -> Unit
    val stopTravelingDialog: @Composable () -> Unit
    val newTravelDialog: @Composable () -> Unit
}

private fun Boolean.getStartOrStopDrawable() = if (this) R.drawable.ic_stop
else R.drawable.ic_play

private fun Boolean.showStartOrStopTravelingDialog(
    showStopTravelingDialog: () -> Unit,
    showStartTravelingDialog: () -> Unit
) = if (this) showStopTravelingDialog
else showStartTravelingDialog
