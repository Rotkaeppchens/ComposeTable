package koin.modules

import data.LedModule
import data.modules.HealthModule
import data.modules.PlayerSidesModule
import data.modules.TimerModule
import data.modules.TurnModule
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val ledModulesModule = module {
    singleOf(::PlayerSidesModule) bind LedModule::class
    singleOf(::TimerModule) bind LedModule::class
    singleOf(::HealthModule) bind LedModule::class
    singleOf(::TurnModule) bind LedModule::class
}
