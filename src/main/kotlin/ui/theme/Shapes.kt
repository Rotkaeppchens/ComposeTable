package ui.theme

import androidx.compose.foundation.shape.GenericShape

val triangleShape = GenericShape { size, _ ->
    moveTo(0f, 0f)
    lineTo(size.width, 0f)
    lineTo(size.width / 2, size.height)
    lineTo(0f, 0f)
}
