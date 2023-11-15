package data.repositories

import androidx.compose.ui.graphics.Color
import data.entities.Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import ui.toColor

class TurnRepository(
    private val playerRepo: PlayerRepository
) {
    private val scope = CoroutineScope(Dispatchers.Default)

    private val _activeSlot: MutableStateFlow<Int> = MutableStateFlow(0)

    private val _teamList: MutableStateFlow<List<Team>> = MutableStateFlow(listOf(
        Team(
            teamId = 0,
            teamName = "Players",
            teamColor = Color.Green,
            canBeOnDeck = true
        ),
        Team(
            teamId = 1,
            teamName = "Enemies",
            teamColor = Color.Red,
            canBeOnDeck = false
        ),
        Team(
            teamId = 2,
            teamName = "NPC's",
            teamColor = Color.Gray,
            canBeOnDeck = false
        )
    ))

    val teamList: StateFlow<List<Team>>
        get() = _teamList

    private val _slotList: MutableStateFlow<List<Slot>> = MutableStateFlow(listOf(
        Slot(
            slotId = 0,
            name = "Character 1",
            color = Color.Green,
            playerId = 1,
            teamId = 0,
            initiativeValue = 12
        ),
        Slot(
            slotId = 0,
            name = "Minion of Character 1",
            color = Color.Green,
            playerId = 1,
            teamId = 0,
            initiativeValue = 12
        ),
        Slot(
            slotId = 1,
            name = "Enemy 1",
            color = Color.Red,
            playerId = null,
            teamId = 1,
            initiativeValue = 66
        ),
        Slot(
            slotId = 2,
            name = "Character 2",
            color = Color.Green,
            playerId = null,
            teamId = 0,
            initiativeValue = 78
        ),
        Slot(
            slotId = 2,
            name = "Minion of Character 2",
            color = Color.Green,
            playerId = null,
            teamId = 0,
            initiativeValue = 78
        ),
        Slot(
            slotId = 3,
            name = "Enemy 2",
            color = Color.Red,
            playerId = null,
            teamId = 1,
            initiativeValue = 78
        ),
        Slot(
            slotId = 4,
            name = "Random NPC",
            color = Color.Gray,
            playerId = null,
            teamId = 2,
            initiativeValue = 99
        )
    ))

    val turnList: StateFlow<List<TurnSlot>> = combine(
        _activeSlot,
        _slotList,
        _teamList,
        playerRepo.activePlayers
    ) { activeSlot, slotList, teamList, playerList ->
        val lastActiveIndex = slotList.indexOfLast { it.slotId == activeSlot }
        val onDeckSlot = (
            slotList.subList(lastActiveIndex.coerceAtLeast(0), slotList.size) +
            slotList.subList(0, lastActiveIndex.coerceAtLeast(0))
        ).firstOrNull { slot ->
            val team = teamList.find { it.teamId == slot.teamId }

            team?.canBeOnDeck == true && slot.slotId != activeSlot
        }?.slotId

        slotList.map { slot ->
            val team = teamList.firstOrNull { it.teamId == slot.teamId }
            val player = playerList.firstOrNull { it.id == slot.playerId }

            TurnSlot(
                slotId = slot.slotId,
                name = slot.name,
                color = player?.color?.toColor() ?: slot.color,
                player = player,
                team = team,
                active = activeSlot == slot.slotId,
                onDeck = onDeckSlot == slot.slotId,
                initiativeValue = slot.initiativeValue
            )
        }
    }.stateIn(scope, SharingStarted.WhileSubscribed(), emptyList())

    fun resetActive() = _activeSlot.update { 0 }

    fun setNextActive() = _activeSlot.update { it + 1 }

    data class TurnSlot(
        val slotId: Int,
        val name: String,
        val color: Color,
        val player: Player?,
        val team: Team?,
        val active: Boolean,
        val onDeck: Boolean,
        val initiativeValue: Int,
    )

    data class Team(
        val teamId: Int,
        val teamName: String,
        val teamColor: Color,
        val canBeOnDeck: Boolean
    )

    private data class Slot(
        val slotId: Int,
        val name: String,
        val color: Color,
        val playerId: Int?,
        val teamId: Int?,
        val initiativeValue: Int
    )
}
