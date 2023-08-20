package view_models.base

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

abstract class ViewModel {
    protected val viewModelScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
}
