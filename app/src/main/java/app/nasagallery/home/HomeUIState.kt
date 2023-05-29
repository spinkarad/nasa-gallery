package app.nasagallery.home

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.dp
import app.nasagallery.common.MaterialColors
import app.nasagallery.common.StringHolder
import app.nasagallery.common.entity.MediaId
import app.nasagallery.common.entity.MediaTitle
import app.nasagallery.common.entity.MediaUrl
import app.nasagallery.common.get
import app.nasagallery.home.HomeUIState.Loading.Initial.items


sealed class HomeUIState(open val items: List<MediaItem>) {

    val isNotLoading get() = this !is Loading
    sealed class Loading(items: List<MediaItem>) : HomeUIState(items) {
        object Initial : Loading(skeletonItems)
        class Sequential(items: List<MediaItem>) : Loading(items)
    }

    class Success(override val items: List<MediaItem.Image>) : HomeUIState(items)
    sealed class Error(items: List<MediaItem>) : HomeUIState(items) {
        class Initial(val text: StringHolder, val canTryAgain: Boolean) : Error(emptyList())
        class Sequential(items: List<MediaItem>) : Error(items)
    }
}

private val skeletonItems = listOf(
    MediaItem.Skeleton(true),
    MediaItem.Skeleton(),
    MediaItem.Skeleton(),
    MediaItem.Skeleton(),
)

sealed class MediaItem(private val isToday: Boolean) {

    @Immutable
    data class Image(
        val id: MediaId,
        private val isToday: Boolean,
        val date: StringHolder,
        val title: MediaTitle,
        val imgUrl: MediaUrl,
        val isVideo: Boolean,
    ) : MediaItem(isToday)

    class Skeleton(isToday: Boolean = false) : MediaItem(isToday)
    class Error(val text: StringHolder, val canTryAgain: Boolean) : MediaItem(false)

    val height
        get() = if (isToday) TODAY_ITEM_HEIGHT else COMMON_ITEM_HEIGHT
    val highLightColor
        get() = if (isToday) TODAY_ITEM_HIGHLIGHT_COLOR else COMMON_ITEM_HIGHLIGHT_COLOR

    companion object {
        val TODAY_ITEM_HEIGHT = 220.dp
        val COMMON_ITEM_HEIGHT = 150.dp
        private val TODAY_ITEM_HIGHLIGHT_COLOR = MaterialColors.DeepPurple[300].copy(alpha = 0.4f)
        private val COMMON_ITEM_HIGHLIGHT_COLOR = MaterialColors.Gray[900]
    }
}


