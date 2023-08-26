package ui.composables

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
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
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(8.dp)
    ) {
        ColorDisplay(color)
        Spacer(Modifier.height(16.dp))
        ColorSliderInput(
            color = color,
            onColorChange = onColorChange
        )
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
            .drawBehind {
                val tileSize = 12f
                val tileCount = (size.width / tileSize).toInt()
                val darkColor = Color.hsl(0f, 0f, 0.8f)
                val lightColor = Color.hsl(1f, 1f, 1f)
                for (i in 0..tileCount) {
                    for (j in 0..tileCount) {
                        drawRect(
                            topLeft = Offset(i * tileSize, j * tileSize),
                            color = if ((i + j) % 2 == 0) darkColor else lightColor,
                            size = Size(tileSize, tileSize)
                        )
                    }
                }
            }
    ) {}
}

@Composable
fun ColorSliderInput(
    color: Color,
    onColorChange: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        ColorPartInput(
            value = color.red,
            onValueChange = { onColorChange(color.copy(red = it)) },
            label = "Red"
        )
        ColorPartInput(
            value = color.green,
            onValueChange = { onColorChange(color.copy(green = it)) },
            label = "Green"
        )
        ColorPartInput(
            value = color.blue,
            onValueChange = { onColorChange(color.copy(blue = it)) },
            label = "Blue"
        )
        ColorPartInput(
            value = color.alpha,
            onValueChange = { onColorChange(color.copy(alpha = it)) },
            label = "Alpha"
        )
    }
}

@Composable
fun ColorPartInput(
    value: Float,
    onValueChange: (Float) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Row {
            Text(label)
            Spacer(Modifier.weight(1f))
            Text(
                text = "%.2f".format(value)
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange
        )
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
