package data.modules

import data.BaseConfig
import data.LedColor
import data.LedModule
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration

class TimerModule(
    private val config: BaseConfig
) : LedModule {
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
    private var timerJob: Job? = null

    private val _timerState: MutableStateFlow<TimerState> = MutableStateFlow(TimerState.Stopped)
    val timerState: StateFlow<TimerState>
        get() = _timerState

    private val ledColors: Array<LedColor> = Array(config.config.ledService.ledCount) { LedColor() }
    private val lightLED = LedColor(1.0, 1.0, 1.0, 1.0)
    private val darkLED = LedColor()

    override fun calc(ledNr: Int): LedColor {
        return ledColors[ledNr]
    }

    private fun startTimerJob() {
        timerJob?.cancel()
        timerJob = scope.launch {
            while(_timerState.value is TimerState.Running) {
                _timerState.update { state ->
                    if (state is TimerState.Running) {
                        val now = Clock.System.now()
                        val timePassed = now - state.timerStarted
                        val timeLeft =  state.duration - timePassed

                        val timePassedMilli = timePassed.inWholeMilliseconds
                        val durationMilli = state.duration.inWholeMilliseconds

                        val percentage = if (durationMilli == 0L) 0f else timePassedMilli.toFloat() / durationMilli.toFloat()

                        val activeLEDs = config.config.ledService.ledCount.toFloat() * percentage
                        val completeLEDs = activeLEDs.toInt()
                        val fractionalLED = activeLEDs - completeLEDs

                        ledColors.forEachIndexed { i, _ ->
                            ledColors[i] = when {
                                i < completeLEDs -> lightLED
                                i == completeLEDs -> LedColor(1.0, 1.0, 1.0, fractionalLED.toDouble())
                                else -> darkLED
                            }
                        }

                        if (timeLeft.isNegative()) {
                            TimerState.Finished
                        } else {
                            state.copy(
                                timeLeft = timeLeft,
                                percentage = percentage
                            )
                        }
                    } else state
                }

                delay(config.config.tableConfig.loopSleepTime)
            }
        }
    }

    fun startTimer(duration: Duration) {
        when (val state = _timerState.value) {
            is TimerState.Running -> return
            is TimerState.Paused -> {
                _timerState.update {
                    TimerState.Running(
                        duration = state.duration,
                        timeLeft = state.timeLeft,
                        timerStarted = Clock.System.now() - (state.duration - state.timeLeft),
                        percentage = state.percentage,
                        fillType = state.fillType
                    )
                }
                startTimerJob()
            }
            else -> {
                _timerState.update {
                    TimerState.Running(
                        duration = duration,
                        timeLeft = duration,
                        timerStarted = Clock.System.now(),
                        percentage = 0.0f,
                        fillType = FillType.COMPLETE
                    )
                }
                startTimerJob()
            }
        }
    }

    fun pauseTimer() {
        _timerState.update { state ->
            if (state is TimerState.Running) {
                TimerState.Paused(
                    duration = state.duration,
                    timeLeft = state.timeLeft,
                    percentage = state.percentage,
                    fillType = state.fillType
                )
            } else {
                state
            }
        }
    }

    fun stopTimer() {
        ledColors.forEachIndexed { i, _ ->
            ledColors[i] = darkLED
        }

        _timerState.update {
            TimerState.Stopped
        }
    }

    fun resetTimer() {
        ledColors.forEachIndexed { i, _ ->
            ledColors[i] = darkLED
        }

        _timerState.update { state ->
            when (state) {
                is TimerState.Running -> {
                    TimerState.Paused(
                        duration = state.duration,
                        timeLeft = state.duration,
                        percentage = 0f,
                        fillType = state.fillType
                    )
                }
                is TimerState.Paused -> {
                    TimerState.Paused(
                        duration = state.duration,
                        timeLeft = state.duration,
                        percentage = 0f,
                        fillType = state.fillType
                    )
                }
                else -> {
                    state
                }
            }
        }
    }

    sealed class TimerState {
        data object Stopped : TimerState()
        data class Running(
            val duration: Duration,
            val timeLeft: Duration,
            val timerStarted: Instant,
            val percentage: Float,
            val fillType: FillType
        ) : TimerState()
        data class Paused(
            val duration: Duration,
            val timeLeft: Duration,
            val percentage: Float,
            val fillType: FillType
        ) : TimerState()
        data object Finished : TimerState()
    }

    enum class FillType {
        COMPLETE
    }
}
