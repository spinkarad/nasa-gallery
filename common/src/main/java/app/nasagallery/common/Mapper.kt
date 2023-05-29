package app.nasagallery.common

fun interface Mapper<F, T> {
    suspend fun map(from: F): T
}
