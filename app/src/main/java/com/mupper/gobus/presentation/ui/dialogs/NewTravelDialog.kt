package com.mupper.gobus.presentation.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.mupper.gobus.R
import com.mupper.gobus.presentation.ui.theme.GobusTheme
import com.mupper.gobus.presentation.viewmodel.TravelViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalComposeUiApi
@ExperimentalCoroutinesApi
@ExperimentalPermissionsApi
@Composable
fun NewTravelDialog(
    newTravelViewModelState: TravelViewModel.State,
    hideNewTravelDialog: () -> Unit,
    onNameValueChange: (String) -> Unit,
    onColorValueChange: (String) -> Unit,
    onCapacityValueChange: (String) -> Unit,
    onStepSelected: (TravelViewModel.StepToCreateNewTravel) -> Unit,
    onStepValueSaved: (TravelViewModel.StepToCreateNewTravel) -> Unit,
    onStepValueCanceled: (TravelViewModel.StepToCreateNewTravel) -> Unit,
    startTraveling: () -> Unit
) {
    val dialogIsShown = newTravelViewModelState.newTravelDialogIsShown
    val selectedStep = newTravelViewModelState.selectedStep
    if (dialogIsShown) {
        Dialog(
            onDismissRequest = hideNewTravelDialog,
            properties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            Scaffold(
                topBar = {
                    NewTravelDialogTopAppBar(startTraveling)
                }
            ) {
                NewTravelDialogScaffoldContent(
                    newTravelViewModelState,
                    onStepSelected,
                    selectedStep,
                    onNameValueChange,
                    onColorValueChange,
                    onCapacityValueChange,
                    onStepValueSaved,
                    onStepValueCanceled
                )
            }
        }
    }
}

@ExperimentalCoroutinesApi
@ExperimentalPermissionsApi
@Composable
private fun NewTravelDialogScaffoldContent(
    newTravelViewModelState: TravelViewModel.State,
    onStepSelected: (TravelViewModel.StepToCreateNewTravel) -> Unit,
    selectedStep: TravelViewModel.StepToCreateNewTravel,
    onNameValueChange: (String) -> Unit,
    onColorValueChange: (String) -> Unit,
    onCapacityValueChange: (String) -> Unit,
    onStepValueSaved: (TravelViewModel.StepToCreateNewTravel) -> Unit,
    onStepValueCanceled: (TravelViewModel.StepToCreateNewTravel) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        NewTravelDialogStepsTabs(newTravelViewModelState, onStepSelected, selectedStep)

        val travel = newTravelViewModelState.travel
        val bus = travel?.bus
        val pathName = "${bus?.path}"
        val busColor = "${bus?.color}"
        val busCapacity = "${bus?.capacity}"

        val editPathTextFieldValues = EditPathTextFieldValues(
            nameTextFieldValue = TextFieldValue(pathName),
            onNameTextFieldValueChange = { textFieldValue ->
                val text = textFieldValue.text
                onNameValueChange(text)
            },
            colorTextFieldValue = TextFieldValue(busColor),
            onColorTextFieldValueChange = { textFieldValue ->
                val text = textFieldValue.text
                onColorValueChange(text)
            },
            capacityTextFieldValue = TextFieldValue(busCapacity),
            onCapacityTextFieldValueChange = { textFieldValue ->
                val text = textFieldValue.text
                onCapacityValueChange(text)
            },
        )

        EditPathStepContent(
            modifier = Modifier.fillMaxWidth(),
            selectedStep = selectedStep,
            editPathTextFieldValues = editPathTextFieldValues,
            onStepValueSaved = onStepValueSaved,
            onStepValueCanceled = onStepValueCanceled,
        )
    }
}

@ExperimentalCoroutinesApi
@ExperimentalPermissionsApi
@Composable
private fun NewTravelDialogStepsTabs(
    newTravelViewModelState: TravelViewModel.State,
    onStepSelected: (TravelViewModel.StepToCreateNewTravel) -> Unit,
    selectedStep: TravelViewModel.StepToCreateNewTravel
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        shape = MaterialTheme.shapes.large,
        elevation = 4.dp,
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            item {
                Spacer(modifier = Modifier.size(24.dp))
            }
            val items = newTravelViewModelState.availableSteps
            items(
                items
            ) { step ->
                NewTravelDialogStepTap(onStepSelected, step, selectedStep)
            }
            item {
                Spacer(modifier = Modifier.size(24.dp))
            }
        }
    }
}

@ExperimentalCoroutinesApi
@ExperimentalPermissionsApi
@Composable
private fun NewTravelDialogStepTap(
    onStepSelected: (TravelViewModel.StepToCreateNewTravel) -> Unit,
    step: TravelViewModel.StepToCreateNewTravel,
    selectedStep: TravelViewModel.StepToCreateNewTravel
) {
    Row(
        modifier = Modifier
            .fillMaxHeight()
            .clickable { onStepSelected(step) }
    ) {
        val isFirstStep =
            step is TravelViewModel.StepToCreateNewTravel.Name
        if (!isFirstStep) {
            Divider(
                modifier = Modifier
                    .width(32.dp)
                    .align(Alignment.CenterVertically),
                thickness = 1.dp,
            )
            Spacer(modifier = Modifier.size(8.dp))
        }
        val isSelectedStep = selectedStep == step
        val stepColor = when {
            isSelectedStep -> MaterialTheme.colors.primary
            MaterialTheme.colors.isLight -> Color.Gray
            else -> Color.Gray
        }
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(stepColor)
                .align(Alignment.CenterVertically),
        ) {
            val isStepDone = step.isStepDone
            if (isStepDone) {
                Icon(
                    painterResource(R.drawable.ic_check),
                    contentDescription = stringResource(R.string.content_description_step_is_done)
                )
            } else {
                Text(
                    step.displayNumber(),
                    color = MaterialTheme.colors.onPrimary,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            step.displayName(),
            modifier = Modifier.align(Alignment.CenterVertically),
        )
        Spacer(modifier = Modifier.size(8.dp))
    }
}

@Composable
private fun NewTravelDialogTopAppBar(startTraveling: () -> Unit) {
    TopAppBar(
        title = {
            Text("Iniciemos un nuevo viaje")
        },
        navigationIcon = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    painterResource(id = R.drawable.ic_close),
                    contentDescription = "Close dialog",
                )
            }
        },
        actions = {
            Row(modifier = Modifier.fillMaxHeight()) {
                TextButton(onClick = startTraveling) {
                    Text(stringResource(R.string.lets_travel))
                }
            }
        },
    )
}

@ExperimentalCoroutinesApi
@ExperimentalPermissionsApi
@Composable
private fun EditPathStepContent(
    modifier: Modifier = Modifier,
    selectedStep: TravelViewModel.StepToCreateNewTravel,
    editPathTextFieldValues: EditPathTextFieldValues,
    onStepValueCanceled: (TravelViewModel.StepToCreateNewTravel) -> Unit,
    onStepValueSaved: (TravelViewModel.StepToCreateNewTravel) -> Unit,
) {
    Column(
        modifier = modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        when (selectedStep) {
            is TravelViewModel.StepToCreateNewTravel.Name -> EditPathName(
                nameTextFieldValue = editPathTextFieldValues.nameTextFieldValue,
                onNameTextFieldValueChange = editPathTextFieldValues.onNameTextFieldValueChange,
                modifier = modifier.fillMaxWidth()
            )
            is TravelViewModel.StepToCreateNewTravel.Color -> EditPathColor(
                colorTextFieldValue = editPathTextFieldValues.colorTextFieldValue,
                onColorTextFieldValueChange = editPathTextFieldValues.onColorTextFieldValueChange,
                modifier = modifier.fillMaxWidth()
            )
            is TravelViewModel.StepToCreateNewTravel.Capacity -> EditPathCapacity(
                capacityTextFieldValue = editPathTextFieldValues.capacityTextFieldValue,
                onCapacityTextFieldValueChange = editPathTextFieldValues.onCapacityTextFieldValueChange,
                modifier = modifier.fillMaxWidth()
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            TextButton(onClick = { onStepValueCanceled(selectedStep) }) {
                Text(stringResource(R.string.button_cancel))
            }
            Spacer(modifier = Modifier.size(8.dp))
            Button(onClick = { onStepValueSaved(selectedStep) }) {
                val isCapacityStepToCreateNewTravel =
                    selectedStep is TravelViewModel.StepToCreateNewTravel.Capacity
                val text = if (isCapacityStepToCreateNewTravel) stringResource(R.string.lets_travel)
                else stringResource(R.string.button_continue)
                Text(text)
            }
        }
    }
}

data class EditPathTextFieldValues(
    val nameTextFieldValue: TextFieldValue,
    val onNameTextFieldValueChange: (TextFieldValue) -> Unit,
    val colorTextFieldValue: TextFieldValue,
    val onColorTextFieldValueChange: (TextFieldValue) -> Unit,
    val capacityTextFieldValue: TextFieldValue,
    val onCapacityTextFieldValueChange: (TextFieldValue) -> Unit,
)

@ExperimentalCoroutinesApi
@ExperimentalPermissionsApi
@Composable
@Preview
fun EditPathStepContentPreview() {
    GobusTheme {
        EditPathStepContent(
            modifier = Modifier.fillMaxWidth(),
            selectedStep = listOf(
                TravelViewModel.StepToCreateNewTravel.Name(),
                TravelViewModel.StepToCreateNewTravel.Color(),
                TravelViewModel.StepToCreateNewTravel.Capacity(),
            ).random(),
            editPathTextFieldValues = EditPathTextFieldValues(
                nameTextFieldValue = TextFieldValue(),
                onNameTextFieldValueChange = {},
                colorTextFieldValue = TextFieldValue(),
                onColorTextFieldValueChange = {},
                capacityTextFieldValue = TextFieldValue(),
                onCapacityTextFieldValueChange = {},
            ),
            onStepValueSaved = {},
            onStepValueCanceled = {},
        )
    }
}

@Composable
fun EditPathName(
    modifier: Modifier = Modifier,
    nameTextFieldValue: TextFieldValue = TextFieldValue(),
    onNameTextFieldValueChange: (TextFieldValue) -> Unit = {},
) {
    Column(
        modifier = modifier
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = nameTextFieldValue,
            onValueChange = onNameTextFieldValueChange,
            placeholder = {
                Text(stringResource(R.string.edit_path_name_placeholder))
            }
        )
    }
}

@Composable
@Preview
fun EditPathNamePreview() {
    GobusTheme {
        EditPathName()
    }
}

@Composable
fun EditPathColor(
    modifier: Modifier = Modifier,
    colorTextFieldValue: TextFieldValue = TextFieldValue(),
    onColorTextFieldValueChange: (TextFieldValue) -> Unit = {},
) {
    Column(
        modifier = modifier
    ) {
        OutlinedTextField(
            value = colorTextFieldValue,
            onValueChange = onColorTextFieldValueChange,
            placeholder = {
                Text(stringResource(R.string.edit_path_color_placeholder))
            }
        )
    }
}

@Composable
@Preview
fun EditPathColorPreview() {
    GobusTheme {
        EditPathColor()
    }
}

@Composable
fun EditPathCapacity(
    modifier: Modifier = Modifier,
    capacityTextFieldValue: TextFieldValue = TextFieldValue(),
    onCapacityTextFieldValueChange: (TextFieldValue) -> Unit = {},
) {
    Column(
        modifier = modifier
    ) {
        OutlinedTextField(
            value = capacityTextFieldValue,
            onValueChange = onCapacityTextFieldValueChange,
            placeholder = {
                Text(stringResource(R.string.edit_path_capacity_placeholder))
            }
        )
    }
}

@Composable
@Preview
fun EditPathCapacityPreview() {
    GobusTheme {
        EditPathCapacity()
    }
}

@ExperimentalCoroutinesApi
@ExperimentalPermissionsApi
@Composable
private fun TravelViewModel.StepToCreateNewTravel.displayName() = when (this) {
    is TravelViewModel.StepToCreateNewTravel.Name -> stringResource(R.string.step_name_path_name)
    is TravelViewModel.StepToCreateNewTravel.Color -> stringResource(R.string.step_name_path_color)
    is TravelViewModel.StepToCreateNewTravel.Capacity -> stringResource(R.string.step_name_path_capacity)
}

@ExperimentalCoroutinesApi
@ExperimentalPermissionsApi
@Composable
private fun TravelViewModel.StepToCreateNewTravel.displayNumber() = when (this) {
    is TravelViewModel.StepToCreateNewTravel.Name -> stringResource(R.string.step_number_path_name)
    is TravelViewModel.StepToCreateNewTravel.Color -> stringResource(R.string.step_number_path_color)
    is TravelViewModel.StepToCreateNewTravel.Capacity -> stringResource(R.string.step_number_path_capacity)
}
