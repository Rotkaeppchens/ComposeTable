package data.repositories

import data.BaseConfig
import data.LedColor
import data.database.tables.PlayerTable
import data.entities.Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class PlayerRepository(
    private val config: BaseConfig
) {
    private val scope = CoroutineScope(Dispatchers.Default)

    private val _players: MutableStateFlow<List<Player>> = MutableStateFlow(emptyList())
    val players: StateFlow<List<Player>>
        get() = _players

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

    val playerSides: StateFlow<Map<Player, List<Int>>> = playerMap.map {
        val playerSides: MutableMap<Player, List<Int>> = mutableMapOf()

        it.forEach { (sideId, player) ->
            if (player != null) {
                val sides = playerSides[player] ?: emptyList()
                playerSides[player] = sides + sideId
            }
        }

        playerSides
    }.stateIn(scope, SharingStarted.Eagerly, emptyMap())

    val playerChunks: StateFlow<Map<Player, List<List<Int>>>> = playerSides.map { playerSides ->
        playerSides.map { (player, sidesList) ->
            val sidesCnt = config.getSides().size
            val firstGap = config.getSides().firstOrNull { !sidesList.contains(it) }

            val chunkList = if (firstGap == null) {
                listOf(config.getLEDs())
            } else {
                val ledLists: MutableList<List<Int>> = mutableListOf()
                val insertionList: MutableList<Int> = mutableListOf()

                config.getSides().forEach {
                    val index = (firstGap + it) % sidesCnt

                    if (sidesList.contains(index)) {
                        insertionList.addAll(config.getLEDsForSide(index))
                    } else if (insertionList.isNotEmpty()) {
                        ledLists.add(insertionList.toList())
                        insertionList.clear()
                    }
                }

                if (insertionList.isNotEmpty()) {
                    ledLists.add(insertionList.toList())
                    insertionList.clear()
                }

                ledLists
            }

            player to chunkList
        }.toMap()
    }.stateIn(scope, SharingStarted.Eagerly, emptyMap())

    val activePlayers: StateFlow<Set<Player>> = playerSides.map {
        it.keys
    }.stateIn(scope, SharingStarted.Eagerly, emptySet())

    init {
        scope.launch {
            loadPlayersFromDatabase()
        }
    }

    private fun loadPlayersFromDatabase() {
        transaction {
            _players.update {
                PlayerTable.selectAll().map {
                    Player(
                        id = it[PlayerTable.id],
                        name = it[PlayerTable.name],
                        color = LedColor(
                            red = it[PlayerTable.colorRed],
                            green = it[PlayerTable.colorGreen],
                            blue = it[PlayerTable.colorBlue],
                            alpha = it[PlayerTable.colorAlpha]
                        )
                    )
                }
            }
        }
    }

    fun addPlayer() {
        scope.launch {
            transaction {
                PlayerTable.insert {
                    it[name] = "New Player"
                    it[colorRed] = 1.0
                    it[colorGreen] = 1.0
                    it[colorBlue] = 1.0
                    it[colorAlpha] = 1.0
                }
            }

            loadPlayersFromDatabase()
        }
    }

    fun removePlayer(playerId: Int) {
        scope.launch {
            transaction {
                PlayerTable.deleteWhere { id eq playerId }
            }

            loadPlayersFromDatabase()
        }
    }

    fun setPlayerColor(playerId: Int, color: LedColor) {
        scope.launch {
            transaction {
                PlayerTable.update({ PlayerTable.id eq playerId }) {
                    it[colorRed] = color.red
                    it[colorGreen] = color.green
                    it[colorBlue] = color.blue
                    it[colorAlpha] = color.alpha
                }
            }

            loadPlayersFromDatabase()
        }
    }

    fun setPlayerName(playerId: Int, name: String): Boolean {
        if (name.isBlank()) return false

        scope.launch {
            transaction {
                PlayerTable.update({ PlayerTable.id eq playerId }) {
                    it[this.name] = name
                }
            }

            loadPlayersFromDatabase()
        }

        return true
    }

    fun setPlayerSide(sideId: Int, playerId: Int?) {
        if (!config.getSides().contains(sideId)) return

        _playerMap.update {
            val newArr = it.copyOf()
            newArr[sideId] = playerId
            newArr
        }
    }
}
