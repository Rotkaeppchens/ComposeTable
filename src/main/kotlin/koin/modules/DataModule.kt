package koin.modules

import data.BaseConfig
import data.LedController
import data.repositories.PlayerRepository
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.module

val dataModule = module {
    singleOf(::BaseConfig)
    singleOf(::LedController) withOptions { createdAtStart() }

    // Repositories
    singleOf(::PlayerRepository)
    singleOf(::ModuleConfigRepository)
}
