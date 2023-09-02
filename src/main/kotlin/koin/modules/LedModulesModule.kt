package koin.modules

import data.modules.HealthModule
import data.modules.PlayerSidesModule
import data.modules.TimerModule
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val ledModulesModule = module {
    singleOf(::PlayerSidesModule)
    singleOf(::TimerModule)
    singleOf(::HealthModule)
}
