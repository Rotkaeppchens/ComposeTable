package ui.off_screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import data.modules.HealthModule
import org.koin.compose.koinInject
import view_models.HealthViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HealthOffScreen(
    viewModel: HealthViewModel = koinInject(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    val playerList = remember(uiState.playerList) {
        uiState.playerList.filter { it.maxHealth != 0 }
    }

    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        items(
            items = playerList,
            key = { it.playerId }
        ) { healthState ->
            HealthCard(
                state = healthState,
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItemPlacement()
            )
        }
    }
}

@Composable
fun HealthCard(
    state: HealthModule.HealthState,
    modifier: Modifier = Modifier
) {
    val playerBrush = remember(state.playerColor) {
        Brush.verticalGradient(listOf(state.playerColor, Color.DarkGray))
    }

    val animatedHealth by animateFloatAsState(state.percentage)

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .clip(CircleShape)
            .border(
                color = Color.Black,
                width = 1.dp,
                shape = CircleShape
            )
            .drawWithCache {
                onDrawBehind {
                    val breakPoint = size.width * animatedHealth

                    drawRect(Color.Black)
                    drawRect(
                        brush = playerBrush,
                        topLeft = Offset.Zero,
                        size = Size(
                            height = size.height,
                            width = breakPoint
                        )
                    )
                }
            }
            .padding(16.dp)
    ) {
        Text(state.playerName)
        Text("${state.health} / ${state.maxHealth}")
    }
}
