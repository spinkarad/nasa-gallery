package app.nasagallery.data

import com.skydoves.sandwich.ApiResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NasaMediaService {

    @GET("/planetary/apod?api_key=lpUUk8oiNMul7KLrkOj68TV7Z9QgROkj4jefnCGD")
    suspend fun getMedia(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("thumbs") thumbs: Boolean = true,
    ): ApiResponse<List<NasaMediaDto>>
}

