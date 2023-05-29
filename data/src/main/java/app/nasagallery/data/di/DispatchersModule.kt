package app.nasagallery.data.di

import kotlinx.coroutines.Dispatchers
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class DispatchersModule{

    @Single
    fun dispatcher() = Dispatchers.IO
}
