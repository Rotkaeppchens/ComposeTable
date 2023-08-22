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
    private val ledColors: Array<LedColor> = Array(config.getLEDs().size) { LedColor() }

    init {
        CoroutineScope(Dispatchers.Default).launch {
            config.getLEDs().forEach {
                ledColors[it] = LedColor()
            }

            playerRepo.playerMap.collectLatest {
                it.forEach { (sideId, player) ->
                    player?.let {
                        config.getLEDsForSide(sideId).forEach {  ledId ->
                            ledColors[ledId] = it.color
                        }
                    }
                }
            }
        }
    }

    override fun calc(ledNr: Int): LedColor {
        return ledColors[ledNr]
    }
}
