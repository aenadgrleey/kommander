package com.aenadgrleey.kommander.app_actions.new_script

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.AlertDialog
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aenadgrleey.kommander.app_actions.new_script.model.Event
import com.aenadgrleey.kommander.app_actions.new_script.model.State

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewScriptDialog(
    state: State,
    dispatch: (Event) -> Unit
) {
    if (state is State.Visible) {
        AlertDialog(
            onDismissRequest = { dispatch(Event.Dismiss) },
            title = { Text("Create New File", style = MaterialTheme.typography.titleMedium) },
            text = {
                Column {
                    Text("Target directory:")

                    OutlinedTextField(
                        value = state.dirPath,
                        onValueChange = { dispatch(Event.UpdateDir(it)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("File name:")
                    OutlinedTextField(
                        value = state.name,
                        onValueChange = { dispatch(Event.UpdateName(it)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        isError = state.fileError.isNullOrBlank(),
                        label = { state.fileError?.let { Text(it) } }
                    )
                }
            },
            confirmButton = {
                Button(onClick = { dispatch(Event.Create) }) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { dispatch(Event.Dismiss) }) {
                    Text("Cancel")
                }
            }
        )
    }
}