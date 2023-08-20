package data

object LedColorMixer {
    fun blend(color0: LedColor, color1: LedColor): LedColor {
        val alpha = color0.alpha + color1.alpha * (1 - color0.alpha)

        if (alpha == 0.0) {
            return LedColor()
        }

        val red = addColors(color0.red, color0.alpha, color1.red, color1.alpha) / alpha
        val green = addColors(color0.green, color0.alpha, color1.green, color1.alpha) / alpha
        val blue = addColors(color0.blue, color0.alpha, color1.blue, color1.alpha) / alpha

        return LedColor(red, green, blue, alpha)
    }

    private fun addColors(c0: Double, a0: Double, c1: Double, a1: Double): Double {
        return c0 * a0 + c1 * a1 * (1 - a0)
    }
}
