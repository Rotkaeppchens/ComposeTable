package view_models

import data.entities.Player
import data.repositories.PlayerRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import view_models.base.ViewModel

class TableSetupViewModel(
    private val playerRepo: PlayerRepository
): ViewModel() {
    val uiState: StateFlow<UiState> = combine(
        playerRepo.players,
        playerRepo.playerMap
    ) { players, playerMap ->
        UiState(
            players = players,
            playerMap = playerMap
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UiState(
        players = emptyList(),
        playerMap = emptyMap()
    ))

    fun setPlayerSide(sideId: Int, playerId: Int?) {
        playerRepo.setPlayerSide(sideId, playerId)
    }

    data class UiState(
        val players: List<Player>,
        val playerMap: Map<Int, Player?>
    )
}
