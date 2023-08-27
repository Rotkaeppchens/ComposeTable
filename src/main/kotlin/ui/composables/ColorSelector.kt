package ui.composables

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ui.theme.AppTheme

@Composable
fun ColorSelector(
    color: Color,
    onColorChange: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        ColorDisplay(
            color = color,
            modifier = Modifier.padding(16.dp)
        )
        ColorRGBInput(color, onColorChange)
    }
}

@Composable
fun ColorDisplay(
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = color,
        modifier = modifier
            .size(50.dp)
            .clip(MaterialTheme.shapes.small)
            .drawWithCache {
                onDrawBehind {
                    drawCheckerPattern()
                }
            }
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.small
            )
    ) {}
}

@Composable
fun ColorRGBInput(
    color: Color,
    onColorChange: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .padding(horizontal = 8.dp)
    ) {
        ColorSliderInput(
            value = color.red,
            onValueChange = { onColorChange(color.copy(red = it)) },
            gradientColors = listOf(
                color.copy(red = 0.0f),
                color.copy(red = 1.0f)
            )
        )
        ColorSliderInput(
            value = color.green,
            onValueChange = { onColorChange(color.copy(green = it)) },
            gradientColors = listOf(
                color.copy(green = 0.0f),
                color.copy(green = 1.0f)
            )
        )
        ColorSliderInput(
            value = color.blue,
            onValueChange = { onColorChange(color.copy(blue = it)) },
            gradientColors = listOf(
                color.copy(blue = 0.0f),
                color.copy(blue = 1.0f)
            )
        )
        ColorSliderInput(
            value = color.alpha,
            onValueChange = { onColorChange(color.copy(alpha = it)) },
            gradientColors = listOf(
                color.copy(alpha = 0.0f),
                color.copy(alpha = 1.0f)
            )
        )
    }
}

@Composable
fun ColorSliderInput(
    value: Float,
    onValueChange: (Float) -> Unit,
    gradientColors: List<Color>,
    valueRange: ClosedFloatingPointRange<Float> = 0.0f..1.0f,
    modifier: Modifier = Modifier
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        valueRange = valueRange,
        colors = SliderDefaults.colors(
            activeTrackColor = Color.Transparent,
            disabledActiveTrackColor = Color.Transparent,
            disabledInactiveTrackColor = Color.Transparent,
            inactiveTrackColor = Color.Transparent
        ),
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .drawWithCache {
                onDrawBehind {
                    drawCheckerPattern()
                }
            }
            .background(Brush.horizontalGradient(gradientColors))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.medium
            )
    )
}

fun DrawScope.drawCheckerPattern(
    tileSizeDp: Dp = 6.dp,
    topLeft: Offset = Offset(0f, 0f),
    bottomRight: Size = Size(size.width, size.height),
    darkColor: Color = Color.hsl(0f, 0f, 0.8f),
    lightColor: Color = Color.hsl(1f, 1f, 1f)
) {
    val tileSize: Float = tileSizeDp.toPx()

    clipRect(
        left = topLeft.x,
        top = topLeft.y,
        right = bottomRight.width,
        bottom = bottomRight.height
    ) {
        val tilesHorizontal = 0..(bottomRight.width / tileSize).toInt()
        val tilesVertical = 0..(bottomRight.height / tileSize).toInt()

        tilesHorizontal.forEach { i ->
            tilesVertical.forEach { j ->
                drawRect(
                    topLeft = Offset(i * tileSize, j * tileSize),
                    color = if ((i + j) % 2 == 0) darkColor else lightColor,
                    size = Size(tileSize, tileSize)
                )
            }
        }
    }
}

@Preview
@Composable
fun ColorSelectorPreview() {
    AppTheme {
        ColorSelector(
            color = Color(
                1.0f,
                0.0f,
                0.0f,
                0.7f
            ),
            onColorChange = {}
        )
    }
}
