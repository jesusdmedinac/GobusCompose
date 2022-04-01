package com.mupper.gobus.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.mupper.gobus.data.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@ExperimentalCoroutinesApi
@ExperimentalPermissionsApi
@HiltViewModel
class MapViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val locationRepository: LocationRepository,
) : ViewModel(),
    ContainerHost<MapViewModel.State, MapViewModel.SideEffect> {

    override val container: Container<State, SideEffect> =
        container(State())

    fun retrieveLastKnownLocation() = intent {
        locationRepository
            .lastKnownLatLng()
            .collect { latLng ->
                reduce {
                    state.copy(
                        latLng = LatLng(
                            latitude = latLng.latitude,
                            longitude = latLng.longitude
                        )
                    )
                }
            }
    }

    fun requestPermissions() = intent {
        postSideEffect(SideEffect.RequestPermissions)
    }

    fun allPermissionGranted() = intent {
        reduce { state.copy(allPermissionsState = AllPermissionState.AllPermissionGranted) }
    }

    fun notAllPermissionGranted() = intent {
        reduce { state.copy(allPermissionsState = AllPermissionState.NotAllPermissionGranted) }
    }

    fun showStartStopTraveling() = intent {
        reduce {
            if (!state.isTraveling)
                state.copy(startTravelingIsShown = true)
            else
                state.copy(stopTravelingIsShown = true)
        }
    }

    fun hideStartTravelingDialog() = intent {
        reduce {
            state.copy(startTravelingIsShown = false)
        }
    }

    fun hideStopTravelingDialog() = intent {
        reduce {
            state.copy(stopTravelingIsShown = true)
        }
    }

    fun startTraveling() = intent {
        reduce {
            state.copy(
                startTravelingIsShown = false,
                isTraveling = true,
            )
        }
    }

    fun stopTraveling() = intent {
        reduce {
            state.copy(
                stopTravelingIsShown = false,
                isTraveling = false,
            )
        }
    }

    sealed class AllPermissionState {
        object Idle : AllPermissionState()
        object AllPermissionGranted : AllPermissionState()
        object NotAllPermissionGranted : AllPermissionState()
    }

    data class LatLng(
        val latitude: Double = 0.0,
        val longitude: Double = 0.0,
    )

    @ExperimentalPermissionsApi
    data class State(
        val allPermissionsState: AllPermissionState = AllPermissionState.Idle,
        val latLng: LatLng = LatLng(),
        val isTraveling: Boolean = false,
        val startTravelingIsShown: Boolean = false,
        val stopTravelingIsShown: Boolean = false,
    )

    sealed class SideEffect {
        object Idle : SideEffect()
        object RequestPermissions : SideEffect()
        object SetupMap : SideEffect()
    }
}