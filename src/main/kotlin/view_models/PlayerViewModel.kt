package view_models

import androidx.compose.ui.graphics.Color
import data.entities.Player
import data.modules.PlayerSidesModule
import data.repositories.PlayerRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ui.toLedColor
import view_models.base.ViewModel

class PlayerViewModel(
    private val playerRepo: PlayerRepository,
    private val module: PlayerSidesModule
): ViewModel() {
    val uiState: StateFlow<UiState> = playerRepo.players.map {
        UiState(
            players = it
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UiState(
        players = emptyList()
    ))

    fun setPlayerName(playerId: Int, name: String) = playerRepo.setPlayerName(playerId, name)
    fun setPlayerColor(playerId: Int, color: Color) = playerRepo.setPlayerColor(playerId, color.toLedColor())

    fun addPlayer() = playerRepo.addPlayer()

    fun removePlayer(playerId: Int) = playerRepo.removePlayer(playerId)

    data class UiState(
        val players: List<Player>
    )
}
