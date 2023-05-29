package app.nasagallery.detail

import app.nasagallery.common.entity.MediaExplanation
import app.nasagallery.common.entity.MediaTitle
import app.nasagallery.common.entity.MediaUrl

data class DetailUIState(
    val title: MediaTitle,
    val url: MediaUrl,
    val explanation: MediaExplanation,
)
