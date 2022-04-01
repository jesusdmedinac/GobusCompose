package com.mupper.gobus.presentation.ui.app

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.maps.android.compose.GoogleMap
import com.mupper.gobus.presentation.ui.screens.MainScreen
import com.mupper.gobus.presentation.viewmodel.MapViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@ExperimentalPermissionsApi
@Composable
fun GobusApp() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "main") {
        composable("main") { backStackEntry ->
            PrepareMainScreen()
        }
    }

}

@ExperimentalCoroutinesApi
@ExperimentalPermissionsApi
@Composable
private fun PrepareMainScreen() {
    val mapViewModel: MapViewModel = hiltViewModel()

    val multiplePermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    if (multiplePermissionState.allPermissionsGranted) mapViewModel.allPermissionGranted()
    else mapViewModel.notAllPermissionGranted()

    val sideEffect by mapViewModel
        .container
        .sideEffectFlow
        .collectAsState(initial = MapViewModel.SideEffect.Idle)
    if (sideEffect == MapViewModel.SideEffect.RequestPermissions) {
        LaunchedEffect(Unit) {
            multiplePermissionState.launchMultiplePermissionRequest()
        }
    }

    MainScreen(mapViewModel)
}