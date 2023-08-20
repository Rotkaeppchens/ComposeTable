package data

import kotlin.math.roundToInt

data class LedColor(
    val red: Double = 0.0,
    val green: Double = 0.0,
    val blue: Double = 0.0,
    val alpha: Double = 0.0
) {
    val finalRed: Int
        get() = (red * alpha * 255).roundToInt()
    val finalGreen: Int
        get() = (green * alpha * 255).roundToInt()
    val finalBlue: Int
        get() = (blue * alpha * 255).roundToInt()

    fun blend(color: LedColor): LedColor = LedColorMixer::blend.invoke(this, color)
}
