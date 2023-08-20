package ui

import androidx.compose.ui.graphics.Color
import data.LedColor

fun Color.toLedColor(): LedColor = LedColor(
    red = red.toDouble(),
    blue = blue.toDouble(),
    green = green.toDouble(),
    alpha = alpha.toDouble()
)

fun LedColor.toColor(): Color = Color(
    red = red.toFloat(),
    green = green.toFloat(),
    blue = blue.toFloat(),
    alpha = alpha.toFloat()
)
