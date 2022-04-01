package com.mupper.gobus.presentation.ui.app

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
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
        composable("main") {
            val mapViewModel: MapViewModel = hiltViewModel()

            val state by mapViewModel
                .container
                .stateFlow
                .collectAsState()
            val sideEffect by mapViewModel
                .container
                .sideEffectFlow
                .collectAsState(initial = MapViewModel.SideEffect.Idle)

            MainScreen(
                state,
                sideEffect,
                navController,
                mapViewModel::allPermissionGranted,
                mapViewModel::notAllPermissionGranted,
                mapViewModel::showStartStopTraveling,
                mapViewModel::hideStartTravelingDialog,
                mapViewModel::hideStopTravelingDialog,
                mapViewModel::startTraveling,
                mapViewModel::stopTraveling,
                mapViewModel::retrieveLastKnownLocation,
                mapViewModel::requestPermissions,
            )
        }
    }

}
