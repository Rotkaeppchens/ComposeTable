package ui.screens

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import ui.theme.AppTheme
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
    val dotSize = remember { 12.dp }

    Column(
        modifier = modifier
    ) {
        LazyVerticalGrid(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            columns = GridCells.Adaptive(dotSize)
        ) {
            colorMap.forEach { (side, colors) ->
                items(colors) { color ->
                    LedLight(
                        color = color,
                        dotSize = dotSize
                    )
                }
                item(span = {
                    GridItemSpan(maxCurrentLineSpan)
                }) {  }
            }
        }
    }
}

@Composable
fun LedLight(
    color: Color,
    dotSize: Dp,
    modifier: Modifier = Modifier
) {
    Surface (
        shape = CircleShape,
        color = color,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface),
        modifier = modifier
            .size(dotSize)
            .drawBehind {
                drawCircle(Color.Black)
            }
    ) {  }
}

@Preview
@Composable
fun StatusScreenPreview() {
    AppTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            StatusScreen(
                colorMap = mapOf(
                    0 to listOf(
                        Color.Black,
                        Color.Blue,
                        Color.Magenta,
                        Color.Cyan
                    ),
                    1 to listOf(
                        Color.Black,
                        Color.Blue,
                        Color.Magenta,
                        Color.Cyan
                    ),
                    2 to listOf(
                        Color.Black,
                        Color.Blue,
                        Color.Magenta,
                        Color.Cyan
                    ),
                    3 to listOf(
                        Color.Black,
                        Color.Blue,
                        Color.Magenta,
                        Color.Cyan
                    )
                )
            )
        }
    }
}
