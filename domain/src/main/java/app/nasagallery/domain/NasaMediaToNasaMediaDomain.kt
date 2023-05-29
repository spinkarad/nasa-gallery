package app.nasagallery.domain

import app.nasagallery.common.Mapper
import app.nasagallery.data.NasaMedia
import app.nasagallery.domain.entity.Day
import app.nasagallery.domain.entity.NasaMediaDomain
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import org.koin.core.annotation.Factory

@Factory
class NasaMediaToNasaMediaDomain : Mapper<NasaMedia, NasaMediaDomain> {
    override suspend fun map(from: NasaMedia): NasaMediaDomain {
        val currentMillis = TimeZone.UTC
        val diff = (from.date - Clock.System.todayIn(currentMillis)).run { months + days }
        val day = when (diff) {
            0 -> Day.Today
            -1 -> Day.Yesterday
            else -> Day.Other("${from.date}")
        }
        val imgUrl = when (from) {
            is NasaMedia.Image -> from.url
            is NasaMedia.Video -> from.thumbnailUrl
        }
        return NasaMediaDomain(from.id, day, imgUrl, from.title, from is NasaMedia.Video)
    }
}
