package data.modules

import data.BaseConfig
import data.LedColor
import data.LedModule
import data.repositories.PlayerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PlayerSidesModule(
    config: BaseConfig,
    playerRepo: PlayerRepository
) : LedModule {
    override val moduleId = "Player Sides"

    private val ledColors: Array<LedColor> = Array(config.getLEDs().size) { LedColor() }

    init {
        CoroutineScope(Dispatchers.Default).launch {
            playerRepo.playerMap.collectLatest { playerMap ->
                config.getLEDs().forEach {
                    ledColors[it] = LedColor()
                }

                playerMap.forEach { (sideId, player) ->
                    player?.let {
                        config.getLEDsForSide(sideId).forEach {  ledId ->
                            ledColors[ledId] = it.color
                        }
                    }
                }
            }
        }
    }

    override fun onUpdate(nanoTime: Long): Array<LedColor> {
        return ledColors
    }
}
