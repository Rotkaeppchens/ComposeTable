package view_models

import androidx.compose.ui.graphics.Color
import data.BaseConfig
import data.LedColor
import data.LedController
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ui.toColor
import view_models.base.ViewModel

class StatusViewModel(
    private val config: BaseConfig,
    ledController: LedController
): ViewModel() {
    val uiState: StateFlow<UiState> = ledController.ledState.map { ledList ->
        UiState(
            colorMap = splitSides(ledList),
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UiState(
        colorMap = emptyMap()
    ))

    private fun splitSides(colors: List<LedColor>): Map<Int, List<Color>> {
        val resultMap: MutableMap<Int, List<Color>> = mutableMapOf()

        config.getSides().forEach { side ->
            resultMap[side] = config.getLEDsForSide(side).map { ledId ->
                colors[ledId].toColor()
            }
        }

        return resultMap
    }

    data class UiState(
        val colorMap: Map<Int, List<Color>>
    )
}
