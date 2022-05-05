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
                travel = Travel(
                    bus = Bus(
                        path = "",
                        color = "",
                    )
                )
            )
        )

    val stepsEvents = object : StepsEvents {
        override val onStepSelected = { stepToCreateNewTravel: StepToCreateNewTravel ->
            intent {
                reduce {
                    state.copy(selectedStep = stepToCreateNewTravel)
                }
            }
        }
        override val onStepValueSaved = { stepToCreateNewTravel: StepToCreateNewTravel ->
            intent {
                reduce {
                    with(stepToCreateNewTravel) {
                        when (this) {
                            is StepToCreateNewTravel.Capacity -> state.copy(
                                capacityStep = copy(
                                    isStepDone = true
                                )
                            )
                            is StepToCreateNewTravel.Color -> state.copy(
                                colorStep = copy(
                                    isStepDone = true
                                )
                            )
                            is StepToCreateNewTravel.Name -> state.copy(
                                nameStep = copy(
                                    isStepDone = true
                                )
                            )
                        }
                    }
                }
            }
        }
        override val onStepValueCanceled = { stepToCreateNewTravel: StepToCreateNewTravel ->
            intent {
                reduce {
                    with(stepToCreateNewTravel) {
                        when (this) {
                            is StepToCreateNewTravel.Capacity -> state.copy(
                                capacityStep = copy(
                                    isStepDone = false
                                )
                            )
                            is StepToCreateNewTravel.Color -> state.copy(
                                colorStep = copy(
                                    isStepDone = false
                                )
                            )
                            is StepToCreateNewTravel.Name -> state.copy(
                                nameStep = copy(
                                    isStepDone = false
                                )
                            )
                        }
                    }
                }
            }
        }
    }

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

    sealed class StepToCreateNewTravel(
        open val isStepDone: Boolean,
    ) {
        data class Name(override val isStepDone: Boolean = false) :
            StepToCreateNewTravel(isStepDone)

        data class Color(override val isStepDone: Boolean = false) :
            StepToCreateNewTravel(isStepDone)

        data class Capacity(override val isStepDone: Boolean = false) :
            StepToCreateNewTravel(isStepDone)
    }

    data class Bus(
        val path: String,
        val capacity: Int? = null,
        val color: String,
    )

    data class Traveler(
        val currentPosition: MapViewModel.LatLng? = null,
        val email: String? = null,
        val traveling: Boolean? = null,
    )

    data class Travel(
        val bus: Bus,
        val traveler: Traveler? = null,
        val startDate: Date? = null,
        val endDate: Date? = null,
    )

    data class State(
        val newTravelDialogIsShown: Boolean,
        val selectedStep: StepToCreateNewTravel,
        val nameStep: StepToCreateNewTravel.Name,
        val colorStep: StepToCreateNewTravel.Color,
        val capacityStep: StepToCreateNewTravel.Capacity,
        val travel: Travel,
    ) {
        val availableSteps get() = listOf(nameStep, colorStep, capacityStep)
        val allStepsAreDone
            get() = nameStep.isStepDone
                    && colorStep.isStepDone
                    && capacityStep.isStepDone
    }

    sealed class SideEffect {
        object Idle : SideEffect()
    }

    interface StepsEvents {
        val onStepSelected: (TravelViewModel.StepToCreateNewTravel) -> Unit
        val onStepValueSaved: (TravelViewModel.StepToCreateNewTravel) -> Unit
        val onStepValueCanceled: (TravelViewModel.StepToCreateNewTravel) -> Unit
    }
}