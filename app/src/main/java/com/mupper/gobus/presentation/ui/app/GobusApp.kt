package com.mupper.gobus.presentation.ui.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.mupper.gobus.presentation.ui.dialogs.NewTravelDialog
import com.mupper.gobus.presentation.ui.dialogs.StartTravelingDialog
import com.mupper.gobus.presentation.ui.dialogs.StopTravelingDialog
import com.mupper.gobus.presentation.ui.screens.MainScreen
import com.mupper.gobus.presentation.ui.screens.MainScreenDialogs
import com.mupper.gobus.presentation.viewmodel.MapViewModel
import com.mupper.gobus.presentation.viewmodel.TravelViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalComposeUiApi
@ExperimentalCoroutinesApi
@ExperimentalPermissionsApi
@Composable
fun GobusApp() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "main") {
        composable("main") {
            val mapViewModel: MapViewModel = hiltViewModel()
            val travelViewModel: TravelViewModel = hiltViewModel()

            val mapViewModelState by mapViewModel
                .container
                .stateFlow
                .collectAsState()
            val mapViewModelSideEffect by mapViewModel
                .container
                .sideEffectFlow
                .collectAsState(initial = MapViewModel.SideEffect.Idle)

            val newTravelViewModelState by travelViewModel
                .container
                .stateFlow
                .collectAsState()
            val newTravelViewModelSideEffect by travelViewModel
                .container
                .sideEffectFlow
                .collectAsState(initial = TravelViewModel.SideEffect.Idle)

            val mainScreenDialogs = object : MainScreenDialogs {
                override val startTravelingDialog = @Composable {
                    StartTravelingDialog(
                        mapViewModelState.startTravelingDialogIsShown,
                        travelViewModel::showNewTravelDialog,
                        mapViewModel::hideStartTravelingDialog,
                    )
                }
                override val stopTravelingDialog = @Composable {
                    StopTravelingDialog(
                        mapViewModelState.stopTravelingDialogIsShown,
                        travelViewModel::stopTraveling,
                        mapViewModel::hideStopTravelingDialog
                    )
                }
                override val newTravelDialog = @Composable {
                    NewTravelDialog(
                        newTravelViewModelState,
                        travelViewModel::hideNewTravelDialog,
                        travelViewModel.stepsEvents,
                        travelViewModel::startTraveling
                    )
                }
            }
            MainScreen(
                mapViewModel.container,
                mapViewModel.permissionEvents,
                mapViewModel.controlDialogEvents,
                mapViewModel::moveMapCameraToUserLastKnownLocation,
                mainScreenDialogs,
            )
        }
    }

}
