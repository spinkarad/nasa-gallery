package app.nasagallery.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.nasagallery.R
import app.nasagallery.common.ConnectionError
import app.nasagallery.common.ImageResource
import app.nasagallery.common.StringHolder
import app.nasagallery.domain.GetMediaUseCase
import app.nasagallery.domain.entity.Day
import app.nasagallery.domain.entity.NasaMediaDomain
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class HomeViewModel(
    private val getMedia: GetMediaUseCase
) : ViewModel() {

    private val mediaLoadingCount = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val viewState: StateFlow<HomeUIState> = mediaLoadingCount.flatMapLatest {
        flow {
            emit(getLoadingState())
            emit(getMedia().fold(::onSuccess, ::onFailure))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUIState.Loading.Initial)

    fun onScrollPositionChange(lastVisible: Int?) {
        val state = (viewState.value as? HomeUIState.Success) ?: return
        val shouldGetNextData =
            (lastVisible ?: 0) + ITEMS_TO_END_FETCH_THRESHOLD >= state.items.lastIndex
        if (shouldGetNextData) requestData()
    }

    fun requestData() = mediaLoadingCount.update { it + 1 }

    private fun getLoadingState(): HomeUIState.Loading {
        val currentState = viewState.value
        return when (currentState) {
            HomeUIState.Loading.Initial,
            is HomeUIState.Error.Initial -> HomeUIState.Loading.Initial

            is HomeUIState.Error.Sequential,
            is HomeUIState.Loading.Sequential,
            is HomeUIState.Success -> {
                val newItems = currentState.items.filterMedia() + GalleryItem.Skeleton()
                HomeUIState.Loading.Sequential(newItems)
            }
        }
    }

    private fun onFailure(error: Throwable): HomeUIState {
        val items = viewState.value.items.filterMedia()
        val loadingState =
            viewState.value as? HomeUIState.Loading ?: return HomeUIState.Success(items)
        val canTryAgain = error is ConnectionError
        return when (loadingState) {
            is HomeUIState.Loading.Initial -> {
                HomeUIState.Error.Initial(
                    StringHolder.Resource(R.string.unknown_error), canTryAgain
                )
            }

            is HomeUIState.Loading.Sequential -> {
                val stringHolder = StringHolder.Resource(R.string.unknown_error)
                val errorItem = GalleryItem.Error(stringHolder, canTryAgain)
                HomeUIState.Error.Sequential(items + errorItem)
            }
        }
    }

    private fun onSuccess(data: List<NasaMediaDomain>): HomeUIState.Success =
        HomeUIState.Success(
            data.map { media ->
                val dateStringHolder = when (val day = media.day) {
                    Day.Today -> StringHolder.Resource(R.string.today)
                    Day.Yesterday -> StringHolder.Resource(R.string.yesterday)
                    is Day.Other -> StringHolder.Value(day.date)
                }

                with(media) {
                    val imageResource = imageUrl?.value
                        ?.let(ImageResource::Url)
                        ?: ImageResource.Drawable(R.drawable.ic_planet)
                    GalleryItem.Media(
                        id,
                        day is Day.Today,
                        dateStringHolder,
                        title,
                        imageResource,
                        isVideo,
                    )
                }
            }
        )

    private fun List<GalleryItem>.filterMedia() = filterIsInstance<GalleryItem.Media>()

    companion object {
        const val ITEMS_TO_END_FETCH_THRESHOLD = 5
    }
}
