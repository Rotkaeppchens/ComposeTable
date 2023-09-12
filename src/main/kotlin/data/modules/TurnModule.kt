package data.modules

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import data.BaseConfig
import data.LedAnimationClock
import data.LedColor
import data.LedModule
import data.entities.Player
import data.repositories.PlayerRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class TurnModule(
    private val config: BaseConfig,
    private val playerRepo: PlayerRepository,
    private val ledAnimClock: LedAnimationClock
): LedModule() {
    override val moduleId: String = "Turn"

    private val _activePlayerId: MutableStateFlow<Int> = MutableStateFlow(0)
    private val _playerOrder: MutableStateFlow<List<Int>> = MutableStateFlow(emptyList())

    val activePlayer: StateFlow<Player?> = combine(
        playerRepo.activePlayers,
        _activePlayerId
    ) { players, currentPlayerId ->
        players.find { it.id == currentPlayerId }
    }.stateIn(moduleScope, SharingStarted.Eagerly, null)

    val playerList: StateFlow<List<Player>> = combine(
        playerRepo.activePlayers,
        _playerOrder
    ) { activePlayers, playerOrder ->
        activePlayers.sortedBy { player ->
            playerOrder.indexOf(player.id)
        }
    }.stateIn(moduleScope, SharingStarted.Eagerly, emptyList())

    private val ledFilter = Array(config.config.ledService.ledCount) { LedColor.Dark }

    private var randomJob: Job? = null
    private var randomAnimation: Animatable<Float, AnimationVector1D>? = null

    init {
        moduleScope.launch {
            playerRepo.activePlayers.collectLatest { playerSet ->
                _playerOrder.update { order ->
                    val newOrder = order.toMutableList()

                    playerSet.forEach { player ->
                        if (!newOrder.contains(player.id)) newOrder.add(player.id)
                    }

                    newOrder.removeIf { orderId ->
                        playerSet.none { it.id == orderId }
                    }

                    newOrder
                }
            }
        }

        moduleScope.launch {
            activePlayer.collectLatest {
                ledFilter.fill(LedColor.Dark)

                it?.let { player ->
                    playerRepo.playerChunks.value[player]?.forEach { chunk ->
                        chunk.forEachIndexed { i, ledId ->
                            if (i % 2 == 0) {
                                ledFilter[ledId] = LedColor(alpha = 0.8)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onUpdate(nanoTime: Long): Array<LedColor> {
        val playerList = playerList.value
        val activePlayerIndex = randomAnimation?.value?.roundToInt()

        return activePlayerIndex?.let { i ->
            val activePlayer = playerList[i % playerList.size]

            val ledList = playerRepo.playerChunks.value[activePlayer]?.flatten()?.toSet()

            Array(config.config.ledService.ledCount) { ledId ->
                if (ledList?.contains(ledId) == true) LedColor.Full
                else LedColor.Dark
            }
        } ?: ledFilter
    }

    fun setPlayerOrder(newOrder: List<Int>) {
        if (!newOrder.containsAll(_playerOrder.value) || !_playerOrder.value.containsAll(newOrder)) return

        _playerOrder.update { newOrder }
    }

    fun setPlayerOrderFromTable() {
        val map = playerRepo.playerMap.value
        setPlayerOrder(map.mapNotNull { it.value?.id })
    }

    fun setNextPlayerActive() {
        val playerOrder = playerList.value

        if (playerOrder.isEmpty()) return

        val activePlayerIndex = playerOrder.indexOf(activePlayer.value)
        val nextPlayerId = playerOrder.getOrNull(activePlayerIndex + 1) ?: playerOrder.first()

        setActivePlayer(nextPlayerId.id)
    }

    fun resetActivePlayer() {
        setActivePlayer(0)
    }

    fun setActivePlayer(playerId: Int) {
        _activePlayerId.update { playerId }
    }

    fun setRandomPlayerActive() {
        val list = playerList.value

        if (list.isEmpty()) return

        startPseudoRandomAnimation(list.random().id)
    }

    fun startPseudoRandomAnimation(playerId: Int) {
        randomJob?.cancel()
        randomJob = ledAnimClock.animationScope.launch {
            setActivePlayer(0)

            val players = playerList.value
            val targetVal = (players.size * 10f) + players.indexOfFirst { it.id == playerId }

            randomAnimation = Animatable(0f)
            randomAnimation?.animateTo(
                targetValue = targetVal,
                animationSpec = tween(
                    durationMillis = 10000,
                    easing = EaseOut
                )
            )
            randomAnimation = null
            setActivePlayer(playerId)
        }
    }
}
