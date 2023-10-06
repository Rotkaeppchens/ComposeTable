package data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DmxController(
    private val config: BaseConfig
) {
    private val scope = CoroutineScope(Dispatchers.Default)

    private fun executeCmd(cmd: String) {
        if (cmd.isBlank()) return

        scope.launch {
            Runtime.getRuntime().exec(cmd)
        }
    }

    fun runStartBattle() {
        executeCmd(config.config.dmxConfig.startBattleCmd)
    }

    fun runEndBattle() {
        executeCmd(config.config.dmxConfig.endBattleCmd)
    }
}
