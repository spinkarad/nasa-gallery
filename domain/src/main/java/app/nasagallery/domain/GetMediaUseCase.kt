package app.nasagallery.domain

import android.util.Range
import app.nasagallery.data.MediaRepository
import app.nasagallery.domain.entity.NasaMediaDomain
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import org.koin.core.annotation.Factory

@Factory
class GetMediaUseCase(
    private val repository: MediaRepository,
    private val mapper: NasaMediaToNasaMediaDomain,
) {

    suspend operator fun invoke(): Result<List<NasaMediaDomain>> =
        runCatching {
            repository
                .getMedia()
                .getOrThrow()
                .map { mapper.map(it) }
        }
}
