package app.nasagallery.di

import app.nasagallery.domain.di.DomainModule
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module(includes = [DomainModule::class])
@ComponentScan("app.nasagallery")
class AppModule
