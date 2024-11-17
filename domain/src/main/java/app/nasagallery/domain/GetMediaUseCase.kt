package app.nasagallery.domain

import app.nasagallery.data.MediaRepository
import app.nasagallery.data.NasaMedia
import app.nasagallery.domain.entity.Day
import app.nasagallery.domain.entity.NasaMediaDomain
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import org.koin.core.annotation.Factory

@Factory
class GetMediaUseCase(private val repository: MediaRepository) {

    suspend operator fun invoke(): Result<List<NasaMediaDomain>> =
        repository.getMedia().mapCatching { list ->
            list.map(::getDomainMedia)
        }

    private fun getDomainMedia(from: NasaMedia): NasaMediaDomain {
        val todayDate = Clock.System.todayIn(TimeZone.UTC)
        val dayDiff = with(from.date - todayDate) { months + days }
        val day = when (dayDiff) {
            0 -> Day.Today
            -1 -> Day.Yesterday
            else -> Day.Other("${from.date}")
        }
        val imgUrl = when (from) {
            is NasaMedia.Image -> from.url
            is NasaMedia.Video -> from.thumbnailUrl
            is NasaMedia.Other -> null
        }
        return NasaMediaDomain(from.id, day, imgUrl, from.title, from is NasaMedia.Video)
    }
}
