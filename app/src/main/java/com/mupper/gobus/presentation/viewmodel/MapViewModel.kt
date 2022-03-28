package com.mupper.gobus.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.mupper.gobus.data.repository.LastKnownLocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
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
    private val lastKnownLocationRepository: LastKnownLocationRepository,
) : ViewModel(),
    ContainerHost<MapViewModel.State, MapViewModel.SideEffect> {

    override val container: Container<State, SideEffect> =
        container(State())

    fun retrieveLastKnownLocation() = intent {
        lastKnownLocationRepository
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
    )

    sealed class SideEffect {
        object Idle : SideEffect()
        object RequestPermissions : SideEffect()
        object SetupMap : SideEffect()
    }
}