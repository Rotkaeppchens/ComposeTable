package data.animation

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animate
import data.LedClock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LedAlphaAnimation (
    private val initialValue: Float,
    targetValue: Float,
    animationSpec: AnimationSpec<Float>,
) {
    private val _stateFlow: MutableStateFlow<Float> = MutableStateFlow(0.0f)

    val stateFlow: StateFlow<Float>
        get() = _stateFlow

    init {
        LedClock.animationScope.launch {
            animate(
                initialValue = initialValue,
                targetValue = targetValue,
                animationSpec = animationSpec
            ) { value, _ ->
                _stateFlow.update { value }
            }
        }
    }
}
