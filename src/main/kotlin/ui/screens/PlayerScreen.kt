package ui.screens

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.PersonRemove
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import data.LedColor
import data.entities.Player
import org.koin.compose.koinInject
import ui.composables.ColorSelector
import ui.composables.PlayerList
import ui.theme.AppTheme
import ui.toColor
import view_models.PlayerViewModel

@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel = koinInject(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    PlayerScreen(
        playerList = uiState.players,
        setPlayerName = { id, name -> viewModel.setPlayerName(id, name) },
        setPlayerColor = { id, color -> viewModel.setPlayerColor(id, color) },
        addPlayerClicked = { viewModel.addPlayer() },
        removePlayerClicked = { viewModel.removePlayer(it) },
        modifier = modifier
    )
}

@Composable
fun PlayerScreen(
    playerList: List<Player>,
    setPlayerName: (playerId: Int, name: String) -> Unit,
    setPlayerColor: (playerId: Int, color: Color) -> Unit,
    addPlayerClicked: () -> Unit,
    removePlayerClicked: (playerId: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val (activePlayerId, setActivePlayerId) = remember { mutableStateOf<Int?>(null) }
    val activePlayer = remember(activePlayerId) { playerList.find { it.id == activePlayerId } }

    Row(
        modifier = modifier
    ) {
        PlayerListWithIcons(
            playerList = playerList,
            activePlayerId = activePlayerId,
            setActivePlayerId = setActivePlayerId,
            addPlayerClicked = addPlayerClicked,
            removePlayerClicked = {
                if (activePlayerId != null) {
                    removePlayerClicked(activePlayerId)
                    setActivePlayerId(null)
                }
            }
        )
        Spacer(modifier = Modifier.width(8.dp))
        if (activePlayer != null) {
            val (selectedColor, setSelectedColor) = remember(activePlayer.color) {
                mutableStateOf(activePlayer.color.toColor())
            }

            CurrentPlayerInput(
                player = activePlayer,
                setPlayerName = {
                    setPlayerName(activePlayer.id, it)
                    setPlayerColor(activePlayer.id, selectedColor)
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
                    .fillMaxHeight()
            )
            ColorSelector(
                color = selectedColor,
                onColorChange = setSelectedColor,
                modifier = Modifier
                    .width(250.dp)
                    .fillMaxHeight()
            )
        }
    }
}

@Composable
fun PlayerListWithIcons(
    playerList: List<Player>,
    activePlayerId: Int?,
    setActivePlayerId: (Int) -> Unit,
    addPlayerClicked: () -> Unit,
    removePlayerClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.width(200.dp)
    ) {
        PlayerList(
            players = playerList,
            onPlayerClick = setActivePlayerId,
            activePlayerId = activePlayerId,
            modifier = Modifier.weight(1f)
        )
        Divider(
            thickness = Dp.Hairline,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            FilledIconButton(
                onClick = addPlayerClicked
            ) {
                Icon(
                    imageVector = Icons.Outlined.PersonAdd,
                    contentDescription = null
                )
            }
            FilledIconButton(
                enabled = activePlayerId != null,
                onClick = removePlayerClicked
            ) {
                Icon(
                    imageVector = Icons.Outlined.PersonRemove,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun CurrentPlayerInput(
    player: Player,
    setPlayerName: (name: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val (playerNameInput, setPlayerNameInput) = remember(player.name) { mutableStateOf(player.name) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        OutlinedTextField(
            value = playerNameInput,
            onValueChange = setPlayerNameInput,
            label = {
                Text("Player Name")
            },
            maxLines = 1
        )
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = { setPlayerName(playerNameInput) },
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding
        ) {
            Icon(
                imageVector = Icons.Outlined.Save,
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text("Save")
        }
    }
}

@Preview
@Composable
fun PlayerScreenPreview() {
    AppTheme {
        PlayerScreen(
            playerList = listOf(
                Player(
                    1, "Test", LedColor()
                ),
                Player(
                    2, "HelloWorld", LedColor(1.0, 0.0, 0.0, 1.0)
                )
            ),
            setPlayerName = { _, _ ->  },
            setPlayerColor = { _, _ ->},
            addPlayerClicked = {},
            removePlayerClicked = {}
        )
    }
}
