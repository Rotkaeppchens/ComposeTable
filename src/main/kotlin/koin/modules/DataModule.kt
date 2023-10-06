package koin.modules

import data.*
import data.repositories.ModuleConfigRepository
import data.repositories.PlayerRepository
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
    singleOf(::UsbController)
    singleOf(::DmxController)

    // Repositories
    singleOf(::PlayerRepository)
    singleOf(::ModuleConfigRepository)
}
