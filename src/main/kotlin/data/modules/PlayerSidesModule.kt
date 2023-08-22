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
    private val ledColors: MutableMap<Int, LedColor> = mutableMapOf()

    init {
        CoroutineScope(Dispatchers.Default).launch {
            playerRepo.playerMap.collectLatest {
                ledColors.clear()

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
        return ledColors[ledNr] ?: LedColor()
    }
}
