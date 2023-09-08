package data

import androidx.compose.runtime.BroadcastFrameClock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class LedAnimationClock {
    val clock = BroadcastFrameClock()

    val animationScope: CoroutineScope = CoroutineScope(Dispatchers.Default + clock)
}
