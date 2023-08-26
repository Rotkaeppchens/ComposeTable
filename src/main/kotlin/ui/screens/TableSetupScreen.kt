package ui.screens

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import data.entities.Player
import org.koin.compose.koinInject
import ui.composables.PlayerList
import ui.theme.AppTheme
import ui.theme.equilateralTriangleShape
import ui.toColor
import view_models.TableSetupViewModel
import kotlin.math.abs

@Composable
fun TableSetupScreen(
    viewModel: TableSetupViewModel = koinInject(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    TableSetupScreen(
        players = uiState.players,
        playerMap = uiState.playerMap,
        modifier = modifier
    ) { sideId, playerId ->
        viewModel.setPlayerSide(sideId, playerId)
    }
}

@Composable
fun TableSetupScreen(
    players: List<Player>,
    playerMap: Map<Int, Player?>,
    modifier: Modifier = Modifier,
    onSetPlayerSide: (sideId: Int, playerId: Int?) -> Unit
) {
    val (activePlayerId, setActivePlayerId) = remember { mutableStateOf<Int?>(null) }

    Row(
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .width(200.dp)
        ) {
            PlayerList(
                players = players,
                onPlayerClick = setActivePlayerId,
                activePlayerId = activePlayerId,
                modifier = Modifier.weight(1f)
            )

            Divider(
                thickness = Dp.Hairline,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            val buttonContent: @Composable RowScope.() -> Unit = {
                Icon(
                    imageVector = Icons.Outlined.Clear,
                    contentDescription = "Clear",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(text = "Clear")
            }

            if (activePlayerId == null) {
                Button(
                    onClick = { setActivePlayerId(null) },
                    contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                    content = buttonContent,
                    modifier = Modifier
                        .padding(8.dp)
                )
            } else {
                OutlinedButton(
                    onClick = { setActivePlayerId(null) },
                    contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                    content = buttonContent,
                    modifier = Modifier
                        .padding(8.dp)
                )
            }
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            TableDisplay(
                playerMap = playerMap,
                activePlayerId = activePlayerId,
            ) { sideId ->
                onSetPlayerSide(sideId, activePlayerId)
            }
        }
    }
}

@Composable
fun TableDisplay(
    playerMap: Map<Int, Player?>,
    activePlayerId: Int?,
    modifier: Modifier = Modifier,
    onSideClick: (sideId: Int) -> Unit
) {
    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = modifier
            .width(410.dp)
            .height(400.dp)
    ) {
        playerMap.forEach { (sideId, player) ->
            val color = player?.color?.toColor() ?: Color.Transparent
            val name = player?.name ?: ""
            val selected = player != null && player.id == activePlayerId

            TablePart(
                color = color,
                name = name,
                rotationDeg = (sideId * 60f) + 180f,
                selected = selected,
                onClick = { onSideClick(sideId) }
            )
        }
    }
}

@Composable
fun TablePart(
    color: Color,
    name: String,
    rotationDeg: Float,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    val partColor: Color = if (selected) {
        infiniteTransition.animateColor(
            initialValue = color,
            targetValue = Color.White,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "Part Selected Animation"
        ).value
    } else {
        Color.Black
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .graphicsLayer {
                transformOrigin = TransformOrigin(
                    pivotFractionX = 0.5f,
                    pivotFractionY = 1f
                )

                rotationZ = rotationDeg
            }
    ) {
        Text(
            text = name,
            modifier = Modifier
                .graphicsLayer {
                    if (abs(rotationDeg - 360) > 100) {
                        rotationZ = 180f
                    }
                }
        )
        Spacer(modifier.height(4.dp))
        Surface(
            color = Color.Transparent,
            modifier = Modifier
                .width(200.dp)
                .height(175.dp)
                .clip(equilateralTriangleShape)
                .background(
                    brush = Brush.verticalGradient(listOf(
                        color,
                        partColor
                    )),
                )
                .border(1.dp, borderColor, equilateralTriangleShape)
                .clickable(onClick = onClick)
        ) {}
    }
}

@Preview
@Composable
fun TableSetupScreenPreview() {
    AppTheme {
        Surface {
            TableSetupScreen()
        }
    }
}
