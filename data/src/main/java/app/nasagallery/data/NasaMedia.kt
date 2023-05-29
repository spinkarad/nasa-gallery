package app.nasagallery.data

import app.nasagallery.common.entity.MediaExplanation
import app.nasagallery.common.entity.MediaId
import app.nasagallery.common.entity.MediaTitle
import app.nasagallery.common.entity.MediaUrl
import kotlinx.datetime.LocalDate


sealed interface NasaMedia {
    val id: MediaId
    val date: LocalDate
    val url: MediaUrl
    val title: MediaTitle
    val explanation: MediaExplanation

    class Image(
        override val id: MediaId,
        override val date: LocalDate,
        override val url: MediaUrl,
        override val title: MediaTitle,
        override val explanation: MediaExplanation,
    ) : NasaMedia

    class Video(
        override val id: MediaId,
        override val date: LocalDate,
        override val url: MediaUrl,
        override val title: MediaTitle,
        override val explanation: MediaExplanation,
        val thumbnailUrl: MediaUrl,
    ) : NasaMedia
}
