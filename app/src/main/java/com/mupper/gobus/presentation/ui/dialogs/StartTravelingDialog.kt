package com.mupper.gobus.presentation.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mupper.gobus.R

@Composable
fun StartTravelingDialog(
    showDialog: Boolean,
    startTraveling: () -> Unit,
    onDismissRequest: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(stringResource(R.string.lets_travel_title))
            },
            text = {
                Text(stringResource(R.string.lets_travel_message))
            },
            buttons = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text(stringResource(R.string.maybe_later))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = startTraveling) {
                        Text(stringResource(R.string.lets_travel))
                    }
                }
            },
            modifier = Modifier,
        )
    }
}