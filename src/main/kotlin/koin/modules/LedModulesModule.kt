package koin.modules

import data.LedModule
import data.modules.*
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val ledModulesModule = module {
    singleOf(::PlayerSidesModule) bind LedModule::class
    singleOf(::TimerModule) bind LedModule::class
    singleOf(::HealthModule) bind LedModule::class
    singleOf(::TurnModule) bind LedModule::class
    singleOf(::EffectsModule) bind LedModule::class
}
