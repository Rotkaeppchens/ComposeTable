package view_models

import data.modules.EffectsModule
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import view_models.base.ViewModel

class EffectsViewModel(
    private val effectsModule: EffectsModule
): ViewModel() {
    val uiState: StateFlow<UiState> = effectsModule.activeEffects.map {
        UiState(
            activeEffects = it
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UiState(emptySet()))

    fun startBattle() {
        effectsModule.startBattle()
    }

    data class UiState(
        val activeEffects: Set<EffectsModule.OneShotEffects>
    )
}
