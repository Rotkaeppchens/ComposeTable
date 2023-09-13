package ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import data.modules.HealthModule
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import ui.composables.IntegerInputDialog
import view_models.HealthViewModel
import kotlin.math.roundToInt

@Composable
fun HealthScreen(
    viewModel: HealthViewModel = koinInject(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    HealthScreen(
        playerList = uiState.playerList,
        onSetHealth = { playerId, health ->  viewModel.setHealth(playerId, health = health) },
        onSetMaxHealth = { playerId, maxHealth ->  viewModel.setHealth(playerId, maxHealth = maxHealth) },
        modifier = modifier
    )
}

@Composable
fun HealthScreen(
    playerList: List<HealthModule.HealthState>,
    onSetHealth: (playerId: Int, health: Int) -> Unit,
    onSetMaxHealth: (playerId: Int, maxHealth: Int) -> Unit,
    tileWidth: Dp = 250.dp,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val gridState = rememberLazyGridState()

    LazyVerticalGrid(
        state = gridState,
        columns = GridCells.Adaptive(tileWidth),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .draggable(
                state = rememberDraggableState { delta ->
                    scope.launch {
                        gridState.scrollBy(-delta)
                    }
                },
                orientation = Orientation.Vertical
            )
    ) {
        items(playerList) { item ->
            PlayerItem(
                healthState = item,
                onSetHealth = { onSetHealth(item.playerId, it) },
                onSetMaxHealth = { onSetMaxHealth(item.playerId, it) },
                modifier = Modifier.width(tileWidth)
            )
        }
    }
}

@Composable
fun PlayerItem(
    healthState: HealthModule.HealthState,
    onSetHealth: (Int) -> Unit,
    onSetMaxHealth: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var displayHealthDialog by remember { mutableStateOf(false) }
    var displayMaxHealthDialog by remember { mutableStateOf(false) }

    if (displayHealthDialog) {
        IntegerInputDialog(
            initialValue = healthState.health,
            onValueSubmit = {
                onSetHealth(it)
                displayHealthDialog = false
            },
            onDismissRequest = { displayHealthDialog = false }
        )
    }
    if (displayMaxHealthDialog) {
        IntegerInputDialog(
            initialValue = healthState.maxHealth,
            onValueSubmit = {
                onSetMaxHealth(it)
                displayMaxHealthDialog = false
            },
            onDismissRequest = { displayMaxHealthDialog = false }
        )
    }

    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = MaterialTheme.shapes.medium
            )
            .padding(8.dp)
    ) {
        PlayerInfo(
            playerColor = healthState.playerColor,
            playerName = healthState.playerName
        )
        Spacer(Modifier.height(8.dp))
        DialogButtons(
            health = healthState.health,
            maxHealth = healthState.maxHealth,
            onHealthEditClicked = { displayHealthDialog = true },
            onMaxHealthEditClicked = { displayMaxHealthDialog = true }
        )
        Spacer(Modifier.height(8.dp))
        HealthBar(
            health = healthState.health,
            maxHealth = healthState.maxHealth,
            percentage = healthState.percentage,
            onValueChange = onSetHealth,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        HealthControl(
            lowerEnabled = healthState.health != 0,
            upperEnabled = healthState.health != healthState.maxHealth,
            onLowerClicked = { onSetHealth(healthState.health - 1) },
            onUpperClicked = { onSetHealth(healthState.health + 1) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun DialogButtons(
    health: Int,
    maxHealth: Int,
    onHealthEditClicked: () -> Unit,
    onMaxHealthEditClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        FilledTonalIconButton(
            onClick = onHealthEditClicked
        ) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = null
            )
        }
        Text("$health / $maxHealth")
        FilledTonalIconButton(
            onClick = onMaxHealthEditClicked
        ) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = null
            )
        }
    }
}

@Composable
fun PlayerInfo(
    playerColor: Color,
    playerName: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Surface(
            color = playerColor,
            shape = CircleShape,
            modifier = modifier.size(15.dp)
        ) {}
        Spacer(Modifier.width(8.dp))
        Text(
            text = playerName,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
        )
    }
}

@Composable
fun HealthControl(
    lowerEnabled: Boolean,
    upperEnabled: Boolean,
    onLowerClicked: () -> Unit,
    onUpperClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
    ) {
        FilledTonalIconButton(
            onClick = onLowerClicked,
            enabled = lowerEnabled
        ) {
            Icon(
                imageVector = Icons.Outlined.ArrowBack,
                contentDescription = null
            )
        }
        FilledTonalIconButton(
            onClick = onUpperClicked,
            enabled = upperEnabled
        ) {
            Icon(
                imageVector = Icons.Outlined.ArrowForward,
                contentDescription = null
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthBar(
    health: Int,
    maxHealth: Int,
    percentage: Float,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val sliderColors = SliderDefaults.colors(
        activeTrackColor = Color.Transparent,
        disabledActiveTrackColor = Color.Transparent,
        inactiveTrackColor = Color.Transparent,
        disabledInactiveTrackColor = Color.Transparent,
    )

    Slider(
        value = health.toFloat(),
        valueRange = 0f.rangeTo(maxHealth.toFloat()),
        steps = (maxHealth - 1).coerceAtLeast(1),
        onValueChange = { onValueChange(it.roundToInt()) },
        colors = sliderColors,
        track = { sliderPositions ->
            SliderDefaults.Track(
                sliderPositions = sliderPositions,
                colors = sliderColors,
                modifier = Modifier
                    .fillMaxHeight()
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .border(width = 1.dp, color = Color.Black, shape = CircleShape)
            .height(45.dp)
            .drawBehind {
                val lineWidth = size.width * percentage
                drawLine(
                    brush = Brush.verticalGradient(listOf(
                        Color.Red,
                        Color(red = 50, green = 50, blue = 50)
                    )),
                    start = Offset(0f, size.height / 2),
                    end = Offset(lineWidth, size.height / 2),
                    strokeWidth = 45.dp.toPx()
                )
            }
    )
}
