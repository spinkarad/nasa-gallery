package app.nasagallery.detail

import androidx.core.util.PatternsCompat
import androidx.lifecycle.ViewModel
import app.nasagallery.common.ImageResource
import app.nasagallery.common.entity.MediaId
import app.nasagallery.data.MediaRepository
import app.nasagallery.data.NasaMedia
import app.nasagallery.R
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class DetailViewModel(
    private val id: MediaId,
    private val repository: MediaRepository,
) : ViewModel() {
    val media: DetailUIState?
        get() = repository.getMedia(id)?.run {
            val url = when (this) {
                is NasaMedia.Image -> url
                is NasaMedia.Video -> thumbnailUrl
                is NasaMedia.Other -> null
            }
            DetailUIState(title, url, explanation)
        }
}
