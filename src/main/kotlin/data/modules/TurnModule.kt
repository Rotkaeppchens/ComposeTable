package data.modules

import androidx.compose.animation.core.*
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
import kotlin.math.sin

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

    private val ledFilter = Array(config.ledCount) { LedColor.Transparent }

    private var randomJob: Job? = null
    private var randomAnimation: RandomAnimation? = null

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
                ledFilter.fill(LedColor.Transparent)

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

        return when(val anim = randomAnimation) {
            is RandomAnimation.DoubleIndicator -> {
                if (anim.runAnimation != null) {
                    val currentActiveLed = anim.runAnimation.value.roundToInt() % config.ledCount
                    val activeLEDs = currentActiveLed - 5 ..currentActiveLed + 5

                    Array(config.ledCount) { ledId ->
                        if (ledId in activeLEDs) {
                            val subLedId = ledId - activeLEDs.first
                            val alphaPos = subLedId.toDouble() / 10.0

                            LedColor.Full.copy(
                                alpha = sin(Math.PI * alphaPos)
                            )
                        }
                        else LedColor.Transparent
                    }
                } else if (anim.fillAnimation != null) {
                    val percent = anim.fillAnimation.value

                    val midwayPoint = anim.targetChunk.size / 2
                    val activeLedCnt = (anim.targetChunk.size * percent / 2).roundToInt()
                    val firstHalf = anim.targetChunk.subList((midwayPoint - activeLedCnt).coerceAtLeast(0), midwayPoint).reversed()
                    val secondHalf = anim.targetChunk.subList(midwayPoint, (midwayPoint + activeLedCnt).coerceAtMost(anim.targetChunk.size))

                    Array(config.ledCount) { ledId ->
                        if (ledId in firstHalf || ledId in secondHalf) LedColor.Full
                        else LedColor.Transparent
                    }
                } else {
                    Array(config.ledCount) { LedColor.Transparent }
                }
            }
            is RandomAnimation.FlashSides -> {
                val activePlayerIndex = anim.activeSideAnim.value.roundToInt()
                val activePlayer = playerList[activePlayerIndex % playerList.size]

                val ledList = playerRepo.playerChunks.value[activePlayer]?.flatten()?.toSet()

                Array(config.ledCount) { ledId ->
                    if (ledList?.contains(ledId) == true) LedColor.Full
                    else LedColor.Transparent
                }
            }
            null -> ledFilter
        }
    }

    fun setPlayerOrder(newOrder: List<Int>) {
        if (!newOrder.containsAll(_playerOrder.value) || !_playerOrder.value.containsAll(newOrder)) return

        _playerOrder.update { newOrder }
    }

    fun setPlayerOrderFromTable() {
        val map = playerRepo.playerMap.value
        setPlayerOrder(map.mapNotNull { it.value?.id })
    }

    fun setNextPlayerActive(forward: Boolean) {
        val playerOrder = playerList.value

        if (playerOrder.isEmpty()) return

        val activePlayerIndex = playerOrder.indexOf(activePlayer.value)
        val nextPlayerId = if (forward) {
            playerOrder.getOrNull(activePlayerIndex + 1) ?: playerOrder.first()
        } else {
            playerOrder.getOrNull(activePlayerIndex - 1) ?: playerOrder.last()
        }

        setActivePlayer(nextPlayerId.id)
    }

    fun resetActivePlayer() {
        setActivePlayer(0)
    }

    fun setActivePlayer(playerId: Int) {
        _activePlayerId.update { playerId }
    }

    fun setRandomPlayerActive(type: RandomAnimationType) {
        val list = playerList.value

        if (list.isEmpty()) return

        startPseudoRandomAnimation(list.random().id, type)
    }

    fun startPseudoRandomAnimation(playerId: Int, type: RandomAnimationType) {
        randomJob?.cancel()
        randomJob = ledAnimClock.animationScope.launch {
            setActivePlayer(0)

            val players = playerList.value
            val targetPlayer = players.find { it.id == playerId } ?: return@launch

            when(type) {
                RandomAnimationType.FLASH_SIDES -> {
                    val targetVal = (players.size * 10f) + players.indexOf(targetPlayer)
                    val animatable = Animatable(0f)

                    randomAnimation = RandomAnimation.FlashSides(
                        activeSideAnim = animatable
                    )
                    animatable.animateTo(
                        targetValue = targetVal,
                        animationSpec = tween(
                            durationMillis = 10000,
                            easing = EaseOut
                        )
                    )
                    randomAnimation = null
                }
                RandomAnimationType.DOUBLE_INDICATOR -> {
                    val targetPlayerChunks = playerRepo.playerChunks.value[targetPlayer] ?: return@launch
                    val targetChunk = targetPlayerChunks.first()
                    val targetVal = (config.ledCount * 10f) + targetChunk[targetChunk.size / 2]

                    val runAnim = Animatable(0.0f)
                    val fillAnim = Animatable(0.0f)

                    randomAnimation = RandomAnimation.DoubleIndicator(
                        runAnimation = runAnim,
                        fillAnimation = null,
                        targetChunk = targetChunk
                    )
                    runAnim.animateTo(
                        targetValue = targetVal,
                        animationSpec = tween(
                            durationMillis = 10000,
                            easing = EaseOut
                        )
                    )
                    randomAnimation = RandomAnimation.DoubleIndicator(
                        runAnimation = null,
                        fillAnimation = fillAnim,
                        targetChunk = targetChunk
                    )
                    fillAnim.animateTo(
                        targetValue = 1.0f,
                        animationSpec = tween(
                            durationMillis = 1000,
                            easing = Ease
                        )
                    )
                    randomAnimation = null
                }
            }

            setActivePlayer(playerId)
        }
    }

    enum class RandomAnimationType {
        FLASH_SIDES,
        DOUBLE_INDICATOR
    }

    sealed class RandomAnimation {
        data class FlashSides(
            val activeSideAnim: Animatable<Float, AnimationVector1D>
        ) : RandomAnimation()

        data class DoubleIndicator(
            val runAnimation: Animatable<Float, AnimationVector1D>?,
            val fillAnimation: Animatable<Float, AnimationVector1D>?,
            val targetChunk: List<Int>
        ) : RandomAnimation()
    }
}
