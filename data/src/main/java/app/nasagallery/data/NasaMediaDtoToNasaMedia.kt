package app.nasagallery.data

import app.nasagallery.common.Mapper
import app.nasagallery.common.entity.MediaExplanation
import app.nasagallery.common.entity.MediaId
import app.nasagallery.common.entity.MediaTitle
import app.nasagallery.common.entity.MediaUrl
import kotlinx.datetime.LocalDate
import org.koin.core.annotation.Single

@Single
class NasaMediaDtoToNasaMedia : Mapper<List<NasaMediaDto>, Result<List<NasaMedia>>> {
    override suspend fun map(from: List<NasaMediaDto>) = runCatching {
        from.map { dto ->
            with(dto) {
                val url = MediaUrl(url)
                val title = MediaTitle(title)
                val explanation = MediaExplanation(explanation)
                val date = LocalDate.parse(date)
                when (mediaType) {
                    MediaType.VIDEO -> NasaMedia.Video(
                        MediaId(date.toString()),
                        date,
                        url,
                        title,
                        explanation,
                        MediaUrl(thumbnailUrl.orEmpty())
                    )

                    MediaType.IMAGE -> NasaMedia.Image(
                        MediaId(date.toString()),
                        date,
                        url,
                        title,
                        explanation
                    )
                }
            }
        }
    }
}
