package view_models

import data.modules.HealthModule
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import view_models.base.ViewModel

class HealthViewModel(
    private val healthModule: HealthModule
) : ViewModel() {
    val uiState: StateFlow<UiState> = healthModule.healthStates.map {
        UiState(
            playerList = it
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UiState(emptyList()))

    fun setHealth(playerId: Int, health: Int? = null, maxHealth: Int? = null) = healthModule.setHealth(playerId, health, maxHealth)

    data class UiState(
        val playerList: List<HealthModule.HealthState>
    )
}
