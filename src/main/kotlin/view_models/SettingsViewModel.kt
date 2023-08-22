package view_models

import data.BaseConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import view_models.base.ViewModel

class SettingsViewModel(
    baseConfig: BaseConfig
) : ViewModel() {
    val uiState: StateFlow<UiState> = MutableStateFlow(
        UiState(
            config = baseConfig.config
        )
    )

    data class UiState (
        val config: BaseConfig.Companion.Config
    )
}
