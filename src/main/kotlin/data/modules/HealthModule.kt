package data.modules

import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.Color
import data.BaseConfig
import data.LedAnimationClock
import data.LedColor
import data.LedModule
import data.repositories.PlayerRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ui.toColor

class HealthModule(
    private val config: BaseConfig,
    private val playerRepo: PlayerRepository,
    private val ledAnimClock: LedAnimationClock
): LedModule() {
    override val moduleId = "Health"

    private val healthChangeAnimationSpec = tween<Float>(
        durationMillis = 1000,
        easing = Ease
    )

    private val healthLowAnimationSpec = infiniteRepeatable(
        animation = keyframes {
            durationMillis = 2000

            1.0f at 0
            0.0f at 800
            0.8f at 900
            0.0f at 1000
            0.8f at 1100
            0.0f at 1200
            1.0f at 2000
        },
        repeatMode = RepeatMode.Restart
    )

    private val _healthMap: MutableStateFlow<Map<Int, Pair<Int, Int>>> = MutableStateFlow(emptyMap())

    val healthStates: StateFlow<List<HealthState>> = combine(
        playerRepo.activePlayers,
        _healthMap
    ) { playerList, healthMap ->
        playerList.map { player ->
            val (health, maxHealth) = healthMap[player.id] ?: (0 to 0)

            val percentage = if (maxHealth == 0) 1f else health.toFloat() / maxHealth.toFloat()

            HealthState(
                playerId = player.id,
                playerName = player.name,
                playerColor = player.color.toColor(),
                health = health,
                maxHealth = maxHealth,
                percentage = percentage
            )
        }
    }.stateIn(moduleScope, SharingStarted.Eagerly, emptyList())

    private val animatedHealth: MutableMap<Int, Animatable<Float, AnimationVector1D>> = mutableMapOf()
    private val animatedAlpha: MutableMap<Int, Animatable<Float, AnimationVector1D>> = mutableMapOf()

    override fun onUpdate(nanoTime: Long): Array<LedColor> {
        val playerChunks = playerRepo.playerChunks.value

        val ledArr = Array(config.config.ledService.ledCount) { LedColor() }

        playerChunks.forEach { (player, chunkList) ->
            val healthState = healthStates.value.first { it.playerId == player.id }

            chunkList.forEach { ledChunk ->
                val percentage = animatedHealth[healthState.playerId]?.value ?: healthState.percentage
                val alpha = animatedAlpha[healthState.playerId]?.value ?: 0.0f
                val activeLedBorder = (ledChunk.size) * percentage
                val activeLEDs = activeLedBorder.toInt()
                val fraction = (activeLedBorder - activeLEDs) * (1.0f - alpha)

                ledChunk.forEachIndexed { index, ledId ->
                    ledArr[ledId] = when {
                        index < activeLEDs -> LedColor(alpha = alpha.toDouble())
                        index == activeLEDs -> LedColor(alpha = 1.0 - fraction.toDouble())
                        else -> LedColor(alpha = 1.0)
                    }
                }
            }
        }

        return ledArr
    }

    fun setHealth(playerId: Int, health: Int? = null, maxHealth: Int? = null) {
        _healthMap.update {
            val newMap = it.toMutableMap()

            val oldPair = newMap[playerId] ?: (0 to 0)

            val newMaxHealth = (maxHealth ?: oldPair.second).coerceAtLeast(0)
            val newHealth = (health ?: oldPair.first).coerceIn(0, newMaxHealth)

            val percentage = if (newMaxHealth == 0) 1f else newHealth.toFloat() / newMaxHealth.toFloat()

            ledAnimClock.animationScope.launch {
                if (!animatedHealth.containsKey(playerId)) animatedHealth[playerId] = Animatable(1.0f)

                animatedHealth[playerId]?.animateTo(
                    targetValue = percentage,
                    animationSpec = healthChangeAnimationSpec
                )
            }

            ledAnimClock.animationScope.launch {
                if (percentage < 0.1f) {
                    animatedAlpha[playerId] = Animatable(0.0f)
                    animatedAlpha[playerId]?.animateTo(
                        targetValue = 1.0f,
                        animationSpec = healthLowAnimationSpec
                    )
                } else {
                    animatedAlpha[playerId]?.stop()
                    animatedAlpha.remove(playerId)
                }
            }

            newMap[playerId] = newHealth to newMaxHealth

            newMap
        }
    }

    data class HealthState(
        val playerId: Int,
        val playerName: String,
        val playerColor: Color,
        val health: Int,
        val maxHealth: Int,
        val percentage: Float
    )
}
