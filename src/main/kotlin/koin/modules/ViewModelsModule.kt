package koin.modules

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import view_models.*

val viewModelsModule = module {
    factoryOf(::PlayerViewModel)
    factoryOf(::SettingsViewModel)
    factoryOf(::StatusViewModel)
    factoryOf(::TableSetupViewModel)
    factoryOf(::TimerViewModel)
    factoryOf(::HealthViewModel)
    factoryOf(::TurnViewModel)
    factoryOf(::EffectsViewModel)
}
