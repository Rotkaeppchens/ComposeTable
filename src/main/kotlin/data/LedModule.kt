package data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

abstract class LedModule {
    abstract val moduleId: String

    abstract fun onUpdate(nanoTime: Long): Array<LedColor>

    val moduleScope = CoroutineScope(Dispatchers.Default)
}
