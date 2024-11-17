package app.nasagallery.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class NasaMediaDto(
    val date: String,
    val explanation: String,
    @SerialName("hdurl")
    val hdUrl: String? = null,
    @SerialName("media_type")
    val mediaType: MediaType,
    @SerialName("thumbnail_url")
    val thumbnailUrl: String? = null,
    val title: String,
    val url: String? = null,
)

enum class MediaType {
    @SerialName("video")
    VIDEO,

    @SerialName("image")
    IMAGE,

    @SerialName("other")
    OTHER
}
