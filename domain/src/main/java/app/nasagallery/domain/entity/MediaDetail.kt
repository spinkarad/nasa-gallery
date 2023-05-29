package app.nasagallery.domain.entity

import app.nasagallery.common.entity.MediaExplanation
import app.nasagallery.common.entity.MediaTitle
import app.nasagallery.common.entity.MediaUrl

data class MediaDetail(val title: MediaTitle, val url: MediaUrl, val explanation: MediaExplanation)
