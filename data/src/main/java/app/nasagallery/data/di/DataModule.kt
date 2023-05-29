package app.nasagallery.data.di

import kotlinx.datetime.Clock
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module(includes = [NetworkModule::class, DispatchersModule::class])
@ComponentScan("app.nasagallery.data")
class DataModule {

    @Single
    fun clock(): Clock = Clock.System
}
