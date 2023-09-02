package ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
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
import view_models.StatusViewModel

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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = modifier
            .fillMaxSize()
    ) {
        TableDisplay(
            colorMap = colorMap
        )
    }
}

@Composable
fun TableDisplay(
    colorMap: Map<Int, List<Color>>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
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
