package app.nasagallery.domain.entity

import app.nasagallery.common.entity.MediaId
import app.nasagallery.common.entity.MediaTitle
import app.nasagallery.common.entity.MediaUrl

data class NasaMediaDomain(
    val id: MediaId,
    val day: Day,
    val imageUrl: MediaUrl?,
    val title: MediaTitle,
    val isVideo: Boolean,
)

sealed interface Day {
    object Today : Day
    object Yesterday : Day

    @JvmInline
    value class Other(val date: String) : Day
}
