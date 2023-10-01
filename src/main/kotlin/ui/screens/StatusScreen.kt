package ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import ui.theme.equilateralTriangleShape
import view_models.EffectsViewModel
import view_models.StatusViewModel
import view_models.TurnViewModel

@Composable
fun StatusScreen(
    viewModel: StatusViewModel = koinInject(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    StatusScreen(
        colorMap = uiState.colorMap,
        modifier = modifier.padding(8.dp)
    )
}

@Composable
fun StatusScreen(
    colorMap: Map<Int, List<Color>>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxSize()
    ) {
        ScreenCardDisplay(
            modifier = Modifier
                .width(200.dp)
                .fillMaxHeight()
        )
        Divider(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .width(1.dp)
                .fillMaxHeight()
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            TableDisplay(
                colorMap = colorMap
            )
        }
    }
}

@Composable
fun TableDisplay(
    colorMap: Map<Int, List<Color>>,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = modifier.size(width = 400.dp, height = 350.dp)
    ) {
        colorMap.forEach { (side, colors) ->
            SideDisplay(
                colors = colors,
                rotationDeg = (60 * side) + 180f
            )
        }
    }
}

@Composable
fun SideDisplay(
    colors: List<Color>,
    rotationDeg: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(200.dp)
            .height(175.dp)
            .graphicsLayer {
                transformOrigin = TransformOrigin(
                    pivotFractionX = 0.5f,
                    pivotFractionY = 1f
                )

                rotationZ = rotationDeg
            }
            .clip(equilateralTriangleShape)
    ) {
        LedStrip(colors)
    }
}

@Composable
fun LedStrip(
    colors: List<Color>,
    strokeWidth: Dp = 10.dp,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(strokeWidth)
            .drawWithCache {
                onDrawBehind {
                    drawRect(Color.Black)
                }
            }
            .drawWithContent {
                drawLine(
                    brush = Brush.horizontalGradient(colors),
                    start = Offset(0f, size.height / 2),
                    end = Offset(size.width, size.height / 2),
                    strokeWidth = strokeWidth.toPx()
                )
            }
    ) {  }
}

@Composable
fun ScreenCardDisplay(
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        item {
            ScreenCard(
                title = "Effects Module",
                content = { EffectsCard() }
            )
        }
        item {
            ScreenCard(
                title = "Turn Module",
                content = { TurnCard() }
            )
        }
    }
}

@Composable
fun ScreenCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable (() -> Unit)
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(top = 8.dp)
                .align(Alignment.CenterHorizontally)
        )
        content()
    }
}

@Composable
fun EffectsCard(
    viewModel: EffectsViewModel = koinInject(),
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(8.dp)
    ) {
        Button(
            onClick = { viewModel.startBattle() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Start Battle")
        }
        Button(
            onClick = { viewModel.endBattle() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("End Battle")
        }
    }
}

@Composable
fun TurnCard(
    viewModel: TurnViewModel = koinInject(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .padding(8.dp)
    ) {
        Button(
            onClick = { viewModel.setNextPlayerActive(true) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Next Player")
        }
        Button(
            onClick = { viewModel.setNextPlayerActive(false) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Last Player")
        }
        TextButton(
            onClick = { viewModel.setAlphaAnimActive(!uiState.alphaAnimIsActive) },
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
        ) {
            Switch(
                checked = uiState.alphaAnimIsActive,
                onCheckedChange = null,
            )
            Spacer(Modifier.width(ButtonDefaults.IconSpacing))
            Text("Alpha Animation")
        }
    }
}
