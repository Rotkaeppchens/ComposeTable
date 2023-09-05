package view_models

import data.LedColor
import data.modules.TimerModule
import kotlinx.coroutines.flow.*
import view_models.base.ViewModel
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class TimerViewModel(
    private val timerModule: TimerModule
): ViewModel() {
    private val _inputDuration: MutableStateFlow<Duration> = MutableStateFlow(0.seconds)
    private val _inputConfig: MutableStateFlow<TimerModule.TimerConfig> = MutableStateFlow(TimerModule.TimerConfig())

    val uiState: StateFlow<UiState> = combine(
        _inputDuration,
        _inputConfig,
        timerModule.timer
    ) { inputDuration, inputConfig, state ->
        UiState(
            inputDuration = inputDuration,
            inputConfig =  inputConfig,
            currentTimer = state
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UiState(
        inputDuration = 0.seconds,
        inputConfig = TimerModule.TimerConfig(),
        currentTimer = TimerModule.Timer(
            state = TimerModule.TimerState.STOPPED,
            duration = 60.seconds,
            percentage = 0f,
            timeLeft = 60.seconds
        )
    ))

    fun setTimerDuration(duration: Duration) {
        _inputDuration.update {
            if (duration.isNegative()) 0.seconds else duration
        }
    }

    fun setTimerColor(color: LedColor) {
        _inputConfig.update {
            it.copy(
                fillColor = color
            )
        }
    }

    fun setTimerFillType(fillType: TimerModule.FillType) {
        _inputConfig.update {
            it.copy(
                fillType = fillType
            )
        }
    }

    fun setTimerTail(tailType: TimerModule.TailType) {
        _inputConfig.update {
            it.copy(
                tailType = tailType
            )
        }
    }

    fun startPauseTimer() {
        when (timerModule.timer.value.state) {
            TimerModule.TimerState.RUNNING -> timerModule.pauseTimer()
            TimerModule.TimerState.PAUSED -> timerModule.resumeTimer()
            else -> {
                timerModule.startTimer(
                    duration = _inputDuration.value,
                    timerConfig = _inputConfig.value
                )
            }
        }
    }

    fun resetTimer() {
        val timer = timerModule.timer.value

        timerModule.startTimer(
            duration = timer.duration,
            timerConfig = timer.config
        )
    }

    fun stopTimer() = timerModule.stopTimer()

    data class UiState(
        val inputDuration: Duration,
        val inputConfig: TimerModule.TimerConfig,
        val currentTimer: TimerModule.Timer
    )
}
