package app.nasagallery.common

import androidx.compose.foundation.lazy.LazyListState

val LazyListState.lastVisibleItem: Int?
    get() = layoutInfo.visibleItemsInfo.lastOrNull()?.index
