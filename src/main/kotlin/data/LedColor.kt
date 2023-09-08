package data

import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.TwoWayConverter
import kotlin.math.roundToInt

data class LedColor(
    val red: Double = 0.0,
    val green: Double = 0.0,
    val blue: Double = 0.0,
    val alpha: Double = 0.0
) {
    val finalRed: Int = (red * alpha * 255).roundToInt()
    val finalGreen: Int = (green * alpha * 255).roundToInt()
    val finalBlue: Int = (blue * alpha * 255).roundToInt()

    fun blend(color: LedColor): LedColor = LedColorMixer::blend.invoke(this, color)

    companion object {
        val Dark: LedColor = LedColor(red = 0.0, green = 0.0, blue = 0.0, alpha = 0.0)
        val Full: LedColor = LedColor(red = 1.0, green = 1.0, blue = 1.0, alpha = 1.0)
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
