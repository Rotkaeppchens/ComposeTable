package view_models

import data.entities.Player
import data.modules.TurnModule
import kotlinx.coroutines.flow.*
import view_models.base.ViewModel

class TurnViewModel(
    private val turnModule: TurnModule
): ViewModel() {
    private val _randomAnimType: MutableStateFlow<TurnModule.RandomAnimationType> = MutableStateFlow(TurnModule.RandomAnimationType.DOUBLE_INDICATOR)

    val uiState: StateFlow<UiState> = combine(
        turnModule.activePlayer,
        turnModule.playerList,
        _randomAnimType
    ) { currentPlayer, playerList, randomAnimType ->
        UiState(
            activePlayerId = currentPlayer?.id ?: 0,
            playerList = playerList,
            randomAnimType = randomAnimType
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UiState(
        activePlayerId = 0,
        playerList = emptyList(),
        randomAnimType = TurnModule.RandomAnimationType.DOUBLE_INDICATOR
    ))

    fun setPlayerActive(playerId: Int) {
        if (turnModule.activePlayer.value?.id == playerId) {
            turnModule.resetActivePlayer()
        } else {
            turnModule.setActivePlayer(playerId)
        }
    }
    fun setRandomPlayerActive() = turnModule.setRandomPlayerActive(_randomAnimType.value)
    fun setPseudoRandomActive(playerId: Int) = turnModule.startPseudoRandomAnimation(playerId, _randomAnimType.value)
    fun setNextPlayerActive() = turnModule.setNextPlayerActive()
    fun setOrderFromTable() = turnModule.setPlayerOrderFromTable()

    fun setRandomAnimType(type: TurnModule.RandomAnimationType) {
        _randomAnimType.update { type }
    }

    data class UiState(
        val activePlayerId: Int,
        val playerList: List<Player>,
        val randomAnimType: TurnModule.RandomAnimationType
    )
}
