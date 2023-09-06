package ui.screens

import androidx.compose.animation.core.*
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import data.modules.TimerModule
import org.koin.compose.koinInject
import ui.composables.ColorDisplay
import ui.composables.ColorSelectorDialog
import ui.composables.IntegerInputDialog
import ui.theme.AppTheme
import ui.toColor
import ui.toLedColor
import view_models.TimerViewModel
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
        timer = uiState.currentTimer,
        inputDuration = uiState.inputDuration,
        inputConfig = uiState.inputConfig,
        onSetTimer = { viewModel.setTimerDuration(it) },
        onSetFillType = { viewModel.setTimerFillType(it) },
        onSetTailType = { viewModel.setTimerTail(it) },
        onSetColor = { viewModel.setTimerColor(it.toLedColor()) },
        onStartPauseClicked = { viewModel.startPauseTimer() },
        onResetClicked = { viewModel.resetTimer() },
        onStopClicked = { viewModel.stopTimer() },
        modifier = modifier
    )
}

@Composable
fun TimerScreen(
    timer: TimerModule.Timer,
    inputDuration: Duration,
    inputConfig: TimerModule.TimerConfig,
    onSetTimer: (Duration) -> Unit,
    onSetFillType: (TimerModule.FillType) -> Unit,
    onSetTailType: (TimerModule.TailType) -> Unit,
    onSetColor: (Color) -> Unit,
    onStartPauseClicked: () -> Unit,
    onResetClicked: () -> Unit,
    onStopClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row (
        modifier = modifier
    ) {
        TimerDisplay(
            timer = timer,
            modifier = Modifier
                .width(200.dp)
                .fillMaxHeight()
        )
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            TimerInput(
                inputDuration = inputDuration,
                onSetTimer = onSetTimer
            )
            TimerFillType(
                fillType = inputConfig.fillType,
                onSetFillType = onSetFillType
            )
            TimerTailType(
                tailType = inputConfig.tailType,
                onSetTailType = onSetTailType
            )
            TimerColor(
                color = inputConfig.fillColor.toColor(),
                onSetColor = onSetColor
            )
            TimerControls(
                timerState = timer.state,
                onStartPauseClicked = onStartPauseClicked,
                onResetClicked = onResetClicked,
                onStopClicked = onStopClicked,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

enum class DialogState {
    HIDDEN,
    HOURS,
    MINUTES,
    SECONDS
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

    var displayDialog by remember { mutableStateOf(DialogState.HIDDEN) }

    if (displayDialog != DialogState.HIDDEN) {
        IntegerInputDialog(
            onValueSubmit = {
                onSetTimer(
                    when (displayDialog) {
                        DialogState.HIDDEN -> 0.seconds
                        DialogState.HOURS -> it.hours + inputMinutes.minutes + inputSeconds.seconds
                        DialogState.MINUTES -> inputHours.hours + it.minutes + inputSeconds.seconds
                        DialogState.SECONDS -> inputMinutes.hours + inputMinutes.minutes + it.seconds
                    }
                )
                displayDialog = DialogState.HIDDEN
            },
            onDismissRequest = { displayDialog = DialogState.HIDDEN },
            initialValue = when (displayDialog) {
                DialogState.HIDDEN -> 0
                DialogState.HOURS -> inputHours
                DialogState.MINUTES -> inputMinutes
                DialogState.SECONDS -> inputSeconds
            }
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        TimerInputPart(
            value = inputHours,
            description = "Hours",
            onClick = { displayDialog = DialogState.HOURS }
        )
        Text(
            text = ":",
            style = MaterialTheme.typography.titleLarge
        )
        TimerInputPart(
            value = inputMinutes,
            description = "Minutes",
            onClick = { displayDialog = DialogState.MINUTES }
        )
        Text(
            text = ":",
            style = MaterialTheme.typography.titleLarge
        )
        TimerInputPart(
            value = inputSeconds,
            description = "Seconds",
            onClick = { displayDialog = DialogState.SECONDS }
        )
    }
}

@Composable
fun TimerInputPart(
    value: Int,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onBackground,
                shape = MaterialTheme.shapes.small
            )
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = description,
            fontWeight = FontWeight.Light,
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Composable
fun TimerFillType(
    fillType: TimerModule.FillType,
    onSetFillType: (TimerModule.FillType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text("Fill Type:")
        RadioButton(
            selected = fillType == TimerModule.FillType.COMPLETE,
            onClick = { onSetFillType(TimerModule.FillType.COMPLETE) }
        )
        Text("Complete")
        RadioButton(
            selected = fillType == TimerModule.FillType.SIDES,
            onClick = { onSetFillType(TimerModule.FillType.SIDES) }
        )
        Text("Sides")
    }
}

@Composable
fun TimerTailType(
    tailType: TimerModule.TailType,
    onSetTailType: (TimerModule.TailType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text("Tail Type:")
        RadioButton(
            selected = tailType == TimerModule.TailType.FILL,
            onClick = { onSetTailType(TimerModule.TailType.FILL) }
        )
        Text("Fill")
        RadioButton(
            selected = tailType == TimerModule.TailType.SHORT,
            onClick = { onSetTailType(TimerModule.TailType.SHORT) }
        )
        Text("Short")
        RadioButton(
            selected = tailType == TimerModule.TailType.LONG,
            onClick = { onSetTailType(TimerModule.TailType.LONG) }
        )
        Text("Long")
    }
}

@Composable
fun TimerColor(
    color: Color,
    onSetColor: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    var displayColorDialog by remember { mutableStateOf(false) }

    if (displayColorDialog) {
        ColorSelectorDialog(
            initialColor = color,
            onColorSelect = {
                onSetColor(it)
                displayColorDialog = false
            },
            onDismissRequest = {
                displayColorDialog = false
            }
        )
    }

    Row(
        modifier = modifier
    ) {
        ColorDisplay(
            color
        )
        FilledTonalIconButton(
            onClick = { displayColorDialog = true }
        ) {
            Icon(Icons.Outlined.Edit, contentDescription = null)
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
            if (timerState != TimerModule.TimerState.RUNNING) {
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
            enabled = timerState != TimerModule.TimerState.STOPPED
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
    timer: TimerModule.Timer,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(8.dp)
    ) {
        when(timer.state) {
            TimerModule.TimerState.FINISHED -> Text("FINISHED")
            TimerModule.TimerState.PAUSED -> {
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
                    duration = timer.duration,
                    timeLeft = timer.timeLeft,
                    percentage = timer.percentage,
                    modifier = Modifier.alpha(alpha)
                )
            }
            TimerModule.TimerState.RUNNING -> {
                TimerStateRunning(
                    duration = timer.duration,
                    timeLeft = timer.timeLeft,
                    percentage = timer.percentage
                )
            }
            TimerModule.TimerState.STOPPED -> Text("STOPPED")
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
