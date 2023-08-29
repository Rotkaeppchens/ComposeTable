package ui.screens

import androidx.compose.animation.core.*
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Replay
import androidx.compose.material.icons.outlined.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import data.modules.TimerModule
import org.koin.compose.koinInject
import ui.theme.AppTheme
import view_models.TimerViewModel
import kotlin.math.roundToLong
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds


@Composable
fun TimerScreen(
    viewModel: TimerViewModel = koinInject(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    TimerScreen(
        timerState = uiState.timerState,
        inputDuration = uiState.inputDuration,
        onSetTimer = { viewModel.setTimerDuration(it) },
        onStartPauseClicked = { viewModel.startPauseTimer() },
        onResetClicked = { viewModel.resetTimer() },
        onStopClicked = { viewModel.stopTimer() },
        modifier = modifier
    )
}

@Composable
fun TimerScreen(
    timerState: TimerModule.TimerState,
    inputDuration: Duration,
    onSetTimer: (Duration) -> Unit,
    onStartPauseClicked: () -> Unit,
    onResetClicked: () -> Unit,
    onStopClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row (
        modifier = modifier
    ) {
        TimerDisplay(
            timerState = timerState,
            modifier = Modifier
                .width(200.dp)
                .fillMaxHeight()
        )
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.weight(1f)
            ) {
                TimerInput(
                    inputDuration = inputDuration,
                    onSetTimer = onSetTimer
                )
            }
            TimerControls(
                timerState = timerState,
                onStartPauseClicked = onStartPauseClicked,
                onResetClicked = onResetClicked,
                onStopClicked = onStopClicked,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun TimerInput(
    inputDuration: Duration,
    onSetTimer: (Duration) -> Unit,
    modifier: Modifier = Modifier
) {
    val inputSeconds: Int = remember(inputDuration) { (inputDuration.inWholeSeconds % 60).toInt() }
    val inputMinutes: Int = remember(inputDuration) { (inputDuration.inWholeMinutes % 60).toInt() }
    val inputHours: Int = remember(inputDuration) { inputDuration.inWholeHours.toInt() }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        TimerInputPart(
            value = inputHours,
            onValueChange = { onSetTimer(inputDuration + it.hours) }
        )
        Text(
            text = ":",
            style = MaterialTheme.typography.titleLarge
        )
        TimerInputPart(
            value = inputMinutes,
            onValueChange = { onSetTimer(inputDuration + it.minutes) }
        )
        Text(
            text = ":",
            style = MaterialTheme.typography.titleLarge
        )
        TimerInputPart(
            value = inputSeconds,
            onValueChange = { onSetTimer(inputDuration + it.seconds) }
        )
    }
}

@Composable
fun TimerInputPart(
    value: Int,
    onValueChange: (change: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Row {
            FilledTonalIconButton({ onValueChange(10) }) { Text("+10") }
            FilledTonalIconButton({ onValueChange(1) }) { Text("+1") }
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = "%02d".format(value),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .draggable(
                    state = rememberDraggableState { delta ->
                        onValueChange(-delta.roundToLong())
                    },
                    orientation = Orientation.Vertical
                )
        )
        Spacer(Modifier.height(16.dp))
        Row {
            FilledTonalIconButton({ onValueChange(-10) }) { Text("-10") }
            FilledTonalIconButton({ onValueChange(-1) }) { Text("-1") }
        }
    }
}

@Composable
fun TimerControls(
    timerState: TimerModule.TimerState,
    onStartPauseClicked: () -> Unit,
    onResetClicked: () -> Unit,
    onStopClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconColors = IconButtonDefaults.filledTonalIconButtonColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    )

    Row(
        modifier = modifier
    ) {
        FilledTonalIconButton(
            onClick = onResetClicked,
            colors = iconColors,
            enabled = timerState is TimerModule.TimerState.Running || timerState is TimerModule.TimerState.Paused
        ) {
            Icon(
                imageVector = Icons.Outlined.Replay,
                contentDescription = null
            )
        }
        FilledTonalIconButton(
            onClick = onStartPauseClicked,
            colors = iconColors,
        ) {
            if (timerState !is TimerModule.TimerState.Running) {
                Icon(
                    imageVector = Icons.Outlined.PlayArrow,
                    contentDescription = null
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.Pause,
                    contentDescription = null
                )
            }
        }
        FilledTonalIconButton(
            onClick = onStopClicked,
            colors = iconColors,
            enabled = timerState !is TimerModule.TimerState.Stopped
        ) {
            Icon(
                imageVector = Icons.Outlined.Stop,
                contentDescription = null
            )
        }
    }
}

@Composable
fun TimerDisplay(
    timerState: TimerModule.TimerState,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(8.dp)
    ) {
        when(timerState) {
            TimerModule.TimerState.Finished -> Text("FINISHED")
            is TimerModule.TimerState.Paused -> {
                val transition = rememberInfiniteTransition()
                val alpha by transition.animateFloat(
                    initialValue = 1.0f,
                    targetValue = 0.3f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            durationMillis = 1000,
                            easing = Ease
                        ),
                        repeatMode = RepeatMode.Reverse,
                    )
                )

                TimerStateRunning(
                    duration = timerState.duration,
                    timeLeft = timerState.timeLeft,
                    percentage = timerState.percentage,
                    modifier = Modifier.alpha(alpha)
                )
            }
            is TimerModule.TimerState.Running -> {
                TimerStateRunning(
                    duration = timerState.duration,
                    timeLeft = timerState.timeLeft,
                    percentage = timerState.percentage
                )
            }
            TimerModule.TimerState.Stopped -> Text("STOPPED")
        }
    }
}

@Composable
fun TimerStateRunning(
    duration: Duration,
    timeLeft: Duration,
    percentage: Float,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = durationText(duration),
            fontWeight = FontWeight.Light,
            style = MaterialTheme.typography.titleSmall
        )
        Text(
            text = durationText(timeLeft, displayMillis = true),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(16.dp))
        LinearProgressIndicator(
            progress = percentage,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            trackColor = MaterialTheme.colorScheme.primaryContainer
        )
    }
}

fun durationText(
    duration: Duration,
    displayMillis: Boolean = false
): String {
    val hours = duration.inWholeHours
    val minutes = duration.inWholeMinutes % 60
    val seconds = duration.inWholeSeconds % 60
    val millis = duration.inWholeMilliseconds % 1000

    val format = "%02d"
    val millisFormat = "%03d"

    return if (displayMillis) {
        "${format.format(hours)}:${format.format(minutes)}:${format.format(seconds)}.${millisFormat.format(millis)}"
    } else {
        "${format.format(hours)}:${format.format(minutes)}:${format.format(seconds)}"
    }
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
