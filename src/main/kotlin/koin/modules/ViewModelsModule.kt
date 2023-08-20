package koin.modules

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import view_models.*

val viewModelsModule = module {
    factoryOf(::StatusViewModel)
}
