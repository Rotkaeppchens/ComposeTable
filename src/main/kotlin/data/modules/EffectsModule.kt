package data.modules

import androidx.compose.animation.core.*
import data.BaseConfig
import data.LedAnimationClock
import data.LedColor
import data.LedModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EffectsModule(
    private val config: BaseConfig,
    private val animationClock: LedAnimationClock
): LedModule() {
    override val moduleId: String = "Effects"

    private val _activeEffects: MutableStateFlow<Set<OneShotEffects>> = MutableStateFlow(emptySet())

    val activeEffects: StateFlow<Set<OneShotEffects>>
        get() = _activeEffects

    override fun onUpdate(nanoTime: Long): Array<LedColor> {
        val effectLEDs = _activeEffects.value.map { effect ->
            when(effect) {
                is OneShotEffects.BattleStart -> {
                    val alpha = effect.alphaAnim.value.toDouble()
                    val color = effect.colorAnim.value.copy(alpha = alpha)
                    val position = effect.positionAnim.value
                    val filling = position <= 1f

                    val normalizedPosition = if (position > 1f) position - 1f else position

                    val middlePoint = config.ledCount / 2

                    val firstHalfBreakPoint = middlePoint.toFloat() * normalizedPosition
                    val firstHalf = Array(middlePoint) {
                        when {
                            filling && it < firstHalfBreakPoint -> color
                            filling && it > firstHalfBreakPoint -> LedColor.Transparent
                            it < firstHalfBreakPoint -> LedColor.Transparent
                            else -> color
                        }
                    }

                    val secondHalfCnt = config.ledCount - middlePoint
                    val secondHalfBreakPoint =  secondHalfCnt * normalizedPosition
                    val secondHalf = Array(secondHalfCnt) {
                        when {
                            filling && it < secondHalfBreakPoint -> color
                            filling && it > secondHalfBreakPoint -> LedColor.Transparent
                            it < secondHalfBreakPoint -> LedColor.Transparent
                            else -> color
                        }
                    }

                    firstHalf + secondHalf
                }
            }
        }

        return effectLEDs.fold(
            initial = Array(config.ledCount) { LedColor.Transparent }
        ) { baseColors, colors ->
            baseColors.mapIndexed { i, color ->
                color.blend(colors.getOrElse(i) { LedColor.Transparent })
            }.toTypedArray()
        }
    }

    fun startBattle() {
        battleAnim(LedColor.White, LedColor(1.0, 0.0, 0.0, 1.0))
    }

    fun endBattle() {
        battleAnim(LedColor(1.0, 0.0, 0.0, 1.0), LedColor.White)
    }

    private fun battleAnim(initialColor: LedColor, targetColor: LedColor) {
        if (_activeEffects.value.none { it is OneShotEffects.BattleStart }) {
            _activeEffects.update {
                val effectSet = it.toMutableSet()

                val startEffect = OneShotEffects.BattleStart(initialColor = initialColor)

                val duration = 5000

                animationClock.animationScope.launch {
                    startEffect.colorAnim.animateTo(
                        targetValue = targetColor,
                        animationSpec = tween(
                            durationMillis = duration,
                            easing = EaseInExpo
                        )
                    )
                }
                animationClock.animationScope.launch {
                    startEffect.positionAnim.animateTo(
                        targetValue = 1.0f,
                        animationSpec = keyframes {
                            durationMillis = duration

                            0.0f at 0
                            2.0f at 2500
                            0.0f at 2501
                            1.0f at 5000 with EaseOutExpo
                        }
                    )

                    startEffect.alphaAnim.animateTo(
                        targetValue = 0.0f,
                        animationSpec = tween(
                            durationMillis = 2500,
                            easing = Ease
                        )
                    )

                    _activeEffects.update { oldSet ->
                        val newSet = oldSet.toMutableSet()
                        newSet.remove(startEffect)
                        newSet
                    }
                }

                effectSet.add(startEffect)

                effectSet
            }
        }
    }

    sealed class OneShotEffects(
        val name: String
    ) {
        data class BattleStart(
            val initialColor: LedColor,
            val colorAnim: Animatable<LedColor, AnimationVector4D> = Animatable(
                initialValue =  initialColor,
                typeConverter = LedColor.VectorConverter()
            ),
            val positionAnim: Animatable<Float, AnimationVector1D> = Animatable(
                initialValue = 0.0f
            ),
            val alphaAnim: Animatable<Float, AnimationVector1D> = Animatable(
                initialValue = 1.0f
            )
        ) : OneShotEffects("Battle Start")
    }
}
