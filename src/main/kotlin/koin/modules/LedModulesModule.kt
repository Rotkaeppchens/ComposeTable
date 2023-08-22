package koin.modules

import data.modules.PlayerSidesModule
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val ledModulesModule = module {
    singleOf(::PlayerSidesModule)
}
