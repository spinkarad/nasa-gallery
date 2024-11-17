package app.nasagallery.home

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.dp
import app.nasagallery.common.ImageResource
import app.nasagallery.common.MaterialColors
import app.nasagallery.common.StringHolder
import app.nasagallery.common.alpha10
import app.nasagallery.common.alpha20
import app.nasagallery.common.alpha30
import app.nasagallery.common.alpha50
import app.nasagallery.common.alpha60
import app.nasagallery.common.alpha70
import app.nasagallery.common.alpha80
import app.nasagallery.common.entity.MediaId
import app.nasagallery.common.entity.MediaTitle
import app.nasagallery.common.get


sealed class HomeUIState(open val items: List<GalleryItem>) {

    val isNotLoading get() = this !is Loading
    sealed class Loading(items: List<GalleryItem>) : HomeUIState(items) {
        object Initial : Loading(skeletonItems)
        class Sequential(items: List<GalleryItem>) : Loading(items)
    }

    class Success(override val items: List<GalleryItem.Media>) : HomeUIState(items)
    sealed class Error(items: List<GalleryItem>) : HomeUIState(items) {
        class Initial(val text: StringHolder, val canTryAgain: Boolean) : Error(emptyList())
        class Sequential(items: List<GalleryItem>) : Error(items)
    }
}

private val skeletonItems = listOf(
    GalleryItem.Skeleton(true),
    GalleryItem.Skeleton(),
    GalleryItem.Skeleton(),
    GalleryItem.Skeleton(),
)

sealed class GalleryItem(private val isToday: Boolean) {

    @Immutable
    data class Media(
        val id: MediaId,
        private val isToday: Boolean,
        val date: StringHolder,
        val title: MediaTitle,
        val imageResource: ImageResource,
        val isVideo: Boolean,
    ) : GalleryItem(isToday)

    class Skeleton(isToday: Boolean = false) : GalleryItem(isToday)
    class Error(val text: StringHolder, val canTryAgain: Boolean) : GalleryItem(false)

    val height
        get() = if (isToday) TODAY_ITEM_HEIGHT else COMMON_ITEM_HEIGHT
    val highLightColor
        get() = if (isToday) TODAY_ITEM_HIGHLIGHT_COLOR else COMMON_ITEM_HIGHLIGHT_COLOR

    companion object {
        val TODAY_ITEM_HEIGHT = 220.dp
        val COMMON_ITEM_HEIGHT = 150.dp
        private val TODAY_ITEM_HIGHLIGHT_COLOR = MaterialColors.DeepPurple[600].alpha70
        private val COMMON_ITEM_HIGHLIGHT_COLOR = MaterialColors.DeepPurple[200].alpha20
    }
}


