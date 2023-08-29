package data

import androidx.compose.runtime.BroadcastFrameClock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

object LedClock {
    val clock = BroadcastFrameClock()

    val animationScope: CoroutineScope = CoroutineScope(Dispatchers.Default + clock)
}
