package com.mupper.gobus.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.mupper.gobus.data.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.SimpleSyntax
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import java.util.*
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

    fun requestPermissions() = intent {
        postSideEffect(SideEffect.RequestPermissions)
    }

    fun allPermissionGranted() = intent {
        reduce { state.copy(allPermissionsState = AllPermissionState.AllPermissionGranted) }
    }

    fun notAllPermissionGranted() = intent {
        reduce { state.copy(allPermissionsState = AllPermissionState.NotAllPermissionGranted) }
    }

    fun showStartTravelingDialog() = intent {
        reduce {
            state.copy(startTravelingDialogIsShown = true)
        }
    }

    fun showStopTravelingDialog() = intent {
        reduce {
            state.copy(stopTravelingDialogIsShown = true)
        }
    }

    fun hideStartTravelingDialog() = intent {
        reduce {
            state.copy(startTravelingDialogIsShown = false)
        }
    }

    fun hideStopTravelingDialog() = intent {
        reduce {
            state.copy(stopTravelingDialogIsShown = true)
        }
    }

    private fun SimpleSyntax<State, SideEffect>.launchCollectLastKnownLocation() =
        viewModelScope.launch {
            collectLastKnownLocation(this)
        }

    private suspend fun SimpleSyntax<State, SideEffect>.collectLastKnownLocation(
        coroutineScope: CoroutineScope,
    ) {
        locationRepository
            .locationUpdates()
            .stateIn(coroutineScope)
            .collect { onNewDomainLatLng(it) }
    }

    private suspend fun SimpleSyntax<State, SideEffect>.onNewDomainLatLng(
        latLng: com.mupper.gobus.domain.models.LatLng
    ) {
        reduce {
            val uiLatLng = latLng.toUILatLng()
            state.copy(latLng = uiLatLng)
        }
    }

    private fun com.mupper.gobus.domain.models.LatLng.toUILatLng() = LatLng(
        latitude = latitude,
        longitude = longitude
    )

    fun moveMapCameraToUserLastKnownLocation() = intent {
        val lastKnownLocation = locationRepository.lastKnownLocation()
        reduce {
            state.copy(latLng = lastKnownLocation.toUILatLng())
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
        val startTravelingDialogIsShown: Boolean = false,
        val stopTravelingDialogIsShown: Boolean = false,
    )

    sealed class SideEffect {
        object Idle : SideEffect()
        object RequestPermissions : SideEffect()
        object SetupMap : SideEffect()
    }
}