package ui.theme

import androidx.compose.foundation.shape.GenericShape
import kotlin.math.sqrt

val equilateralTriangleShape = GenericShape { size, _ ->
    val triHeight = sqrt((size.width * size.width) - ((size.width / 2) * (size.width / 2)))

    moveTo(size.width / 2, size.height)
    lineTo(0f, size.height - triHeight)
    lineTo(size.width, size.height - triHeight)
    lineTo(size.width / 2, size.height)
}
