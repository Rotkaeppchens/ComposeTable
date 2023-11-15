package koin.modules

import data.BaseConfig
import data.LedAnimationClock
import data.LedController
import data.ModuleController
import data.repositories.ModuleConfigRepository
import data.repositories.PlayerRepository
import data.repositories.TurnRepository
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.module

val dataModule = module {
    // Special bind for the list of LedModules in the module controller
    single {
        ModuleController(
            moduleList = getAll(),
            configRepo = get()
        )
    }

    singleOf(::BaseConfig)
    singleOf(::LedAnimationClock)
    singleOf(::LedController) withOptions { createdAtStart() }

    // Repositories
    singleOf(::PlayerRepository)
    singleOf(::ModuleConfigRepository)
    singleOf(::TurnRepository)
}
