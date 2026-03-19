package dk.soerensen.garbagev1.data.remote

import dk.soerensen.garbagev1.data.RecyclingFeature
import retrofit2.http.GET

interface RecyclingStationApiService {

    @GET("genbrugsstationer")
    suspend fun getRecyclingStations(): List<RecyclingFeature>
}