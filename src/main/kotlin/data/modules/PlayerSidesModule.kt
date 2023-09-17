package data.modules

import androidx.compose.animation.core.*
import data.BaseConfig
import data.LedAnimationClock
import data.LedColor
import data.LedModule
import data.repositories.PlayerRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PlayerSidesModule(
    private val config: BaseConfig,
    private val playerRepo: PlayerRepository,
    ledAnimClock: LedAnimationClock
) : LedModule() {
    override val moduleId = "Player Sides"

    private val animSpec: AnimationSpec<LedColor> = tween(
        durationMillis = 750,
        easing = Ease
    )

    private val sideColors: Array<Animatable<LedColor, AnimationVector4D>?> = Array(config.getSides().size) { null }

    init {
        moduleScope.launch {
            if (sideColors[0] == null) sideColors.forEachIndexed { i, _ ->
                sideColors[i] = Animatable(
                    initialValue = LedColor.Transparent,
                    typeConverter = LedColor.VectorConverter()
                )
            }

            playerRepo.playerMap.collectLatest { playerMap ->
                playerMap.forEach { (sideId, player) ->
                    val targetColor = player?.color ?: LedColor.Transparent

                    ledAnimClock.animationScope.launch {
                        sideColors[sideId]?.animateTo(
                            targetValue = targetColor,
                            animationSpec = animSpec
                        )
                    }
                }
            }
        }
    }

    override fun onUpdate(nanoTime: Long): Array<LedColor> {
        val colors = Array(config.config.ledService.ledCount) { LedColor.Transparent }

         config.getSides().forEach { sideId ->
            val sideColor = sideColors[sideId]?.value ?: LedColor.Transparent

            config.getLEDsForSide(sideId).forEach { colors[it] = sideColor }
        }

        return colors
    }
}
