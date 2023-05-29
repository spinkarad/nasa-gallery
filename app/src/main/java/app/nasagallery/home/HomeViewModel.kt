package app.nasagallery.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.nasagallery.R
import app.nasagallery.common.ConnectionError
import app.nasagallery.common.StringHolder
import app.nasagallery.data.MediaRepository
import app.nasagallery.domain.GetMediaUseCase
import app.nasagallery.domain.entity.Day
import app.nasagallery.domain.entity.NasaMediaDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class HomeViewModel(
    private val getMedia: GetMediaUseCase
) : ViewModel() {

    init {
        getData()
    }

    private val _viewState: MutableStateFlow<HomeUIState> =
        MutableStateFlow(HomeUIState.Loading.Initial)
    val viewState = _viewState.asStateFlow()

    fun onScrollPositionChange(lastVisible: Int?) {
        val state = (viewState.value as? HomeUIState.Success) ?: return
        val shouldGetNextData =
            (lastVisible ?: 0) + ITEMS_TO_END_FETCH_THRESHOLD >= state.items.lastIndex
        if (shouldGetNextData) requestData()
    }

    fun requestData() {
        when (viewState.value) {
            is HomeUIState.Error.Initial -> setInitialLoading()
            is HomeUIState.Error.Sequential -> setSequentialLoading()
            is HomeUIState.Success -> setSequentialLoading()
            is HomeUIState.Loading -> {}
        }
        getData()
    }

    private fun setInitialLoading() = _viewState.update { HomeUIState.Loading.Initial }

    private fun setSequentialLoading() = _viewState.update {
        val newItems = it.items.filterIsInstance<MediaItem.Image>() + MediaItem.Skeleton()
        HomeUIState.Loading.Sequential(newItems)
    }

    private fun getData() =
        viewModelScope.launch {
            getMedia()
                .onFailure(::onFailure)
                .onSuccess(::onSuccess)
        }

    private fun onFailure(error: Throwable) {
        val loadingState = _viewState.value as? HomeUIState.Loading ?: return
        val canTryAgain = error is ConnectionError
        when (loadingState) {
            is HomeUIState.Loading.Initial -> _viewState.update {
                HomeUIState.Error.Initial(
                    StringHolder.Resource(R.string.unknown_error), canTryAgain
                )
            }

            is HomeUIState.Loading.Sequential -> _viewState.update {
                val stringHolder = StringHolder.Resource(R.string.unknown_error)
                val errorItem = MediaItem.Error(stringHolder, canTryAgain)
                val newItems = it.items.filterIsInstance<MediaItem.Image>() + errorItem
                HomeUIState.Error.Sequential(newItems)
            }
        }
    }

    private fun onSuccess(data: List<NasaMediaDomain>) {
        _viewState.update {
            HomeUIState.Success(
                data.map { media ->
                    val dateStringHolder = when (val day = media.day) {
                        Day.Today -> StringHolder.Resource(R.string.today)
                        Day.Yesterday -> StringHolder.Resource(R.string.yesterday)
                        is Day.Other -> StringHolder.Value(day.date)
                    }
                    with(media) {
                        MediaItem.Image(
                            id,
                            day is Day.Today,
                            dateStringHolder,
                            title,
                            imageUrl,
                            isVideo,
                        )
                    }
                })
        }
    }

    companion object {
        const val ITEMS_TO_END_FETCH_THRESHOLD = 5
    }
}
