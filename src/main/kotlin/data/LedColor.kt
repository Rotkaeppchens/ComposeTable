package data

import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.TwoWayConverter
import com.github.mbelling.ws281x.Color
import kotlin.math.roundToInt

data class LedColor(
    val red: Double = 0.0,
    val green: Double = 0.0,
    val blue: Double = 0.0,
    val alpha: Double = 0.0
) {
    fun toStripColor(): Color {
        return Color(
            /* red = */ (red * alpha * 255).roundToInt(),
            /* green = */ (green * alpha * 255).roundToInt(),
            /* blue = */ (blue * alpha * 255).roundToInt()
        )
    }

    fun blend(color: LedColor): LedColor = LedColorMixer.blend(this, color)

    companion object {
        val Full: LedColor = LedColor(red = 1.0, green = 1.0, blue = 1.0, alpha = 1.0)
        val Transparent: LedColor = LedColor(red = 0.0, green = 0.0, blue = 0.0, alpha = 0.0)
    }

    class VectorConverter: TwoWayConverter<LedColor, AnimationVector4D> {
        override val convertFromVector: (AnimationVector4D) -> LedColor = {
            LedColor(
                red = it.v1.toDouble(),
                green = it.v2.toDouble(),
                blue = it.v3.toDouble(),
                alpha = it.v4.toDouble(),
            )
        }

        override val convertToVector: (LedColor) -> AnimationVector4D = {
            AnimationVector4D(
                v1 = it.red.toFloat(),
                v2 = it.green.toFloat(),
                v3 = it.blue.toFloat(),
                v4 = it.alpha.toFloat(),
            )
        }

    }
}
