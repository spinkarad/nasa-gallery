package app.nasagallery.domain.di

import app.nasagallery.data.di.DataModule
import app.nasagallery.data.di.DispatchersModule
import app.nasagallery.data.di.NetworkModule
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import java.time.Clock

@Module(includes = [DataModule::class])
@ComponentScan("app.nasagallery.domain")
class DomainModule

