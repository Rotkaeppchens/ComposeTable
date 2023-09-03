package ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Backspace
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntegerInputDialog(
    onValueSubmit: (Int) -> Unit,
    onDismissRequest: () -> Unit,
    initialValue: Int = 0,
    modifier: Modifier = Modifier
) {
    val (input, setInput) = remember { mutableStateOf(initialValue.toString()) }

    val addNumber: (String) -> Unit = {
        if (input == "0") setInput(it)
        else setInput(input + it)
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnClickOutside = true
        ),
        modifier = modifier
            .width(250.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.large
                )
                .padding(8.dp)
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = setInput,
                singleLine = true,
                trailingIcon = {
                    IconButton(
                        onClick = {
                            setInput("0")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Cancel,
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Row {
                NumberButton(number = 1, onClick = { addNumber("1") })
                NumberButton(number = 2, onClick = { addNumber("2") })
                NumberButton(number = 3, onClick = { addNumber("3") })
            }
            Row {
                NumberButton(number = 4, onClick = { addNumber("4") })
                NumberButton(number = 5, onClick = { addNumber("5") })
                NumberButton(number = 6, onClick = { addNumber("6") })
            }
            Row {
                NumberButton(number = 7, onClick = { addNumber("7") })
                NumberButton(number = 8, onClick = { addNumber("8") })
                NumberButton(number = 9, onClick = { addNumber("9") })
            }
            Row {
                FilledTonalIconButton(
                    onClick = { setInput(initialValue.toString()) }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.RestartAlt,
                        contentDescription = null
                    )
                }
                NumberButton(number = 0, onClick = { addNumber("0") })
                FilledTonalIconButton(
                    onClick = {
                        if (input.length == 1) setInput("0")
                        else setInput(input.dropLast(1))
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Backspace,
                        contentDescription = null
                    )
                }
            }
            Divider(
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 8.dp)
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = onDismissRequest
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = null
                    )
                }
                Button(
                    onClick = { onValueSubmit(input.toIntOrNull() ?: 0) }
                ) {
                    Text("OK")
                }
            }
        }
    }
}

@Composable
fun NumberButton(
    number: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val numberInputColors = IconButtonDefaults.filledTonalIconButtonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    )

    FilledTonalIconButton(
        onClick = onClick,
        colors = numberInputColors,
        modifier = modifier
    ) {
        Text(
            text = number.toString()
        )
    }
}
