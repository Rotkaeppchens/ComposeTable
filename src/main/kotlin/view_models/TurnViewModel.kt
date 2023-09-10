package view_models

import data.entities.Player
import data.modules.TurnModule
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import view_models.base.ViewModel

class TurnViewModel(
    private val turnModule: TurnModule
): ViewModel() {
    val uiState: StateFlow<UiState> = combine(
        turnModule.activePlayer,
        turnModule.playerList,
    ) { currentPlayer, playerList ->
        UiState(
            activePlayerId = currentPlayer?.id ?: 0,
            playerList = playerList
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UiState(
        activePlayerId = 0,
        playerList = emptyList()
    ))

    fun setPlayerActive(playerId: Int) = turnModule.setActivePlayer(playerId)
    fun setRandomPlayerActive() = turnModule.setRandomPlayerActive()
    fun setPseudoRandomActive(playerId: Int) = turnModule.startPseudoRandomAnimation(playerId)
    fun setNextPlayerActive() = turnModule.setNextPlayerActive()
    fun setOrderFromTable() = turnModule.setPlayerOrderFromTable()

    data class UiState(
        val activePlayerId: Int,
        val playerList: List<Player>
    )
}
