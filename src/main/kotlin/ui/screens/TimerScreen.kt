package ui.screens

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.koin.compose.koinInject
import ui.theme.AppTheme
import view_models.TimerViewModel


@Composable
fun TimerScreen(
    viewModel: TimerViewModel = koinInject(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    TimerScreen(
        timerState = uiState.timerState,
        onStartTimer = { viewModel.startTimer(it) },
        onResetTimer = { viewModel.resetTimer() },
        modifier = modifier
    )
}

@Composable
fun TimerScreen(
    timerState: TimerViewModel.TimerState,
    onStartTimer: (seconds: Int) -> Unit,
    onResetTimer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (secInput, setSecInput) = remember { mutableStateOf(0) }

    Column(
        modifier = modifier
    ) {
        OutlinedTextField(
            value = secInput.toString(),
            onValueChange = { input ->
                setSecInput(input.toIntOrNull() ?: 0)
            },
            label = {
                Text(text = "Seconds")
            }
        )
        Row {
            Button(
                onClick = { onStartTimer(secInput) },
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding
            ) {
                Icon(
                    imageVector = Icons.Outlined.Timer,
                    contentDescription = null,
                    modifier = Modifier
                        .size(ButtonDefaults.IconSize)
                )
                Spacer(
                    modifier = Modifier
                        .size(ButtonDefaults.IconSpacing)
                )
                Text("Start")
            }
            Button(
                onClick = onResetTimer
            ) {
                Text("Reset")
            }
        }
        TimerDisplay(
            timerState = timerState
        )
    }
}

@Composable
fun TimerDisplay(
    timerState: TimerViewModel.TimerState,
    modifier: Modifier = Modifier
) {
    val text = when (timerState) {
        is TimerViewModel.TimerState.Stopped -> "STOPPED"
        is TimerViewModel.TimerState.Running -> "Seconds left: ${timerState.secondsLeft}"
        is TimerViewModel.TimerState.Finished -> "FINISHED"
    }

    Text(
        text = text,
        modifier = modifier
    )
}

@Preview
@Composable
fun TimerScreenPreview() {
    AppTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            TimerScreen()
        }
    }
}