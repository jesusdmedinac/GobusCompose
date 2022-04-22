package com.mupper.gobus.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import java.util.*

@ExperimentalCoroutinesApi
@ExperimentalPermissionsApi
class TravelViewModel : ViewModel(),
    ContainerHost<TravelViewModel.State, TravelViewModel.SideEffect> {
    private lateinit var locationJob: Job

    override val container: Container<State, SideEffect> =
        container(
            State(
                newTravelDialogIsShown = false,
                selectedStep = StepToCreateNewTravel.Name(isStepDone = false),
                nameStep = StepToCreateNewTravel.Name(isStepDone = false),
                colorStep = StepToCreateNewTravel.Color(isStepDone = false),
                capacityStep = StepToCreateNewTravel.Capacity(isStepDone = false),
            )
        )

    fun hideNewTravelDialog() = intent {
        reduce {
            state.copy(newTravelDialogIsShown = false)
        }
    }

    fun startTraveling() = intent { }

    fun stopTraveling() = intent { }

    fun showNewTravelDialog() = intent {
        reduce {
            state.copy(newTravelDialogIsShown = true)
        }
    }

    fun onStepSelected(stepToCreateNewTravel: StepToCreateNewTravel) = intent {
        reduce {
            state.copy(selectedStep = stepToCreateNewTravel)
        }
    }

    fun onStepValueSaved(stepToCreateNewTravel: StepToCreateNewTravel) = intent {
        reduce {
            with(stepToCreateNewTravel) {
                when (this) {
                    is StepToCreateNewTravel.Capacity -> state.copy(capacityStep = copy(isStepDone = true))
                    is StepToCreateNewTravel.Color -> state.copy(colorStep = copy(isStepDone = true))
                    is StepToCreateNewTravel.Name -> state.copy(nameStep = copy(isStepDone = true))
                }
            }
        }
    }

    fun onStepValueCanceled(stepToCreateNewTravel: StepToCreateNewTravel) = intent {
        reduce {
            with(stepToCreateNewTravel) {
                when (this) {
                    is StepToCreateNewTravel.Capacity -> state.copy(capacityStep = copy(isStepDone = false))
                    is StepToCreateNewTravel.Color -> state.copy(colorStep = copy(isStepDone = false))
                    is StepToCreateNewTravel.Name -> state.copy(nameStep = copy(isStepDone = false))
                }
            }
        }
    }

    sealed class StepToCreateNewTravel(
        open val isStepDone: Boolean,
    ) {
        data class Name(override val isStepDone: Boolean = false) : StepToCreateNewTravel(isStepDone)
        data class Color(override val isStepDone: Boolean = false) : StepToCreateNewTravel(isStepDone)
        data class Capacity(override val isStepDone: Boolean = false) : StepToCreateNewTravel(isStepDone)
    }

    data class Bus(
        val path: String,
        val travelers: List<Travel>,
        val capacity: Int,
        val color: String,
        val traveling: Boolean,
    )

    data class Driver(
        val bus: Bus,
        val currentPosition: MapViewModel.LatLng,
        val email: String,
    )

    data class Traveler(
        val currentPosition: MapViewModel.LatLng,
        val email: String,
        val traveling: Boolean,
    )

    data class Travel(
        val id: String,
        val bus: Bus,
        val driver: Driver?,
        val traveler: Traveler,
        val startDate: Date,
        val endDate: Date,
        val points: List<MapViewModel.LatLng>
    )

    data class State(
        val newTravelDialogIsShown: Boolean,
        val selectedStep: StepToCreateNewTravel,
        val nameStep: StepToCreateNewTravel.Name,
        val colorStep: StepToCreateNewTravel.Color,
        val capacityStep: StepToCreateNewTravel.Capacity,
        val travel: Travel? = null,
    ) {
        val availableSteps = listOf(nameStep, colorStep, capacityStep)
        val allStepsAreDone = nameStep.isStepDone
                && colorStep.isStepDone
                && capacityStep.isStepDone
    }

    sealed class SideEffect {
        object Idle : SideEffect()
    }
}