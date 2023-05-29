package app.nasagallery.data


import app.nasagallery.common.ConnectionError
import app.nasagallery.common.entity.MediaId
import com.skydoves.sandwich.ApiResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import org.koin.core.annotation.Singleton

@Singleton
class MediaRepository(
    private val networkService: NasaMediaService,
    private val mapper: NasaMediaDtoToNasaMedia,
    private val ioDispatcher: CoroutineDispatcher,
    private val clock: Clock,
) {

    private var cache: List<NasaMedia> = emptyList()

    suspend fun getMedia(): Result<List<NasaMedia>> =
        runCatching { fetch() }
            .mapCatching { dtoList ->
                (cache + mapper.map(dtoList)
                    .getOrThrow()
                    .sortedByDescending { it.date }).distinct()
                    .also { cache = it }
            }

    private suspend fun fetch(): List<NasaMediaDto> {
        val (startDate, endDate) = getDateRange().getOrThrow()
        return withContext(ioDispatcher) {
            when (val response = networkService.getMedia(startDate, endDate)) {
                is ApiResponse.Failure.Error -> throw InternalError()
                is ApiResponse.Failure.Exception -> throw ConnectionError()
                is ApiResponse.Success -> response.data
            }
        }
    }


    private fun getDateRange() = runCatching {
        val lastDate = if (cache.isEmpty()) {
            clock.todayIn(TimeZone.UTC)
        } else {
            cache.last().date.minusDays(1)
        }
        val startDate = lastDate
            .minusDays(PAGE_SIZE)
            .toString()
        val endDate = lastDate.toString()
        startDate to endDate
    }

    private fun LocalDate.minusDays(count: Int) = this.minus(count, DateTimeUnit.DAY)

    fun getMedia(id: MediaId): NasaMedia? =
        cache.find { it.id == id }

    companion object {
        private const val PAGE_SIZE = 20
    }

}
