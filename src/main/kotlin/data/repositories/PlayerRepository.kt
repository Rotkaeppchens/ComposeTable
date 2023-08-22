package data.repositories

import data.BaseConfig
import data.LedColor
import data.entities.Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class PlayerRepository(
    private val config: BaseConfig
) {
    private val scope = CoroutineScope(Dispatchers.Default)

    private val _players: MutableStateFlow<List<Player>> = MutableStateFlow(emptyList())
    val players: StateFlow<List<Player>> = _players.map { playerList ->
        playerList.sortedBy { it.id }
    }.stateIn(scope, SharingStarted.Eagerly, emptyList())

    private val _playerMap: MutableStateFlow<Array<Int?>> = MutableStateFlow(
        value = Array(config.getSides().size) { null }
    )

    val playerMap: StateFlow<Map<Int, Player?>> = combine(
        _players,
        _playerMap
    ) { players, playerMap ->
        playerMap.mapIndexed { sideId, playerId ->
            sideId to players.find { it.id == playerId }
        }.toMap()
    }.stateIn(scope, SharingStarted.Eagerly, emptyMap())

    private var maxId = 0

    fun addPlayer() {
        _players.update {
            val newList = it.toMutableList()
            newList.add(
                    Player(
                    id = maxId++,
                    name = "New Player",
                    color = LedColor(1.0, 1.0, 1.0, 1.0)
                )
            )
            newList
        }
    }

    fun removePlayer(playerId: Int) {
        _playerMap.update {
            val newArr = it.copyOf()

            newArr.forEachIndexed { i, id ->
                if (id == playerId) {
                    newArr[i] = null
                }
            }

            newArr
        }
        _players.update { playerList ->
            val newList = playerList.toMutableList()
            newList.removeAll { it.id == playerId }
            newList
        }
    }

    fun setPlayerSide(sideId: Int, playerId: Int?) {
        if (!config.getSides().contains(sideId)) return
        if (sideId >= _playerMap.value.size) return

        _playerMap.update {
            val newArr = it.copyOf()
            newArr[sideId] = playerId
            newArr
        }
    }

    fun setPlayerColor(playerId: Int, color: LedColor) {
        _players.update { playerList ->
            val newList = playerList.toMutableList()
            val i = newList.indexOfFirst { it.id == playerId }

            if (i != -1) {
                val player = newList[i]
                newList[i] = player.copy(
                    color = color
                )
            }

            newList
        }
    }

    fun setPlayerName(playerId: Int, name: String): Boolean {
        if (name.isBlank()) return false

        _players.update {
            val newList = it.toMutableList()
            val i = newList.indexOfFirst { it.id == playerId }

            if (i != -1) {
                val player = newList[i]
                newList[i] = player.copy(name = name)
            }

            newList
        }

        return true
    }
}
