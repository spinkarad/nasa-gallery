package app.nasagallery

import android.app.Application
import app.nasagallery.di.AppModule
import org.koin.android.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber
import org.koin.ksp.generated.*


class NasaGalleryApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
        initTimber()
    }

    private fun initKoin() = startKoin {
        androidLogger()
        androidContext(this@NasaGalleryApplication)
        modules(AppModule().module)
    }

    private fun initTimber(){
        if(BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }
    }
}
