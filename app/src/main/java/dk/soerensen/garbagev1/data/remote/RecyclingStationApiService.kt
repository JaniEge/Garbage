package dk.soerensen.garbagev1.data.remote

import dk.soerensen.garbagev1.data.RecyclingStationsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RecyclingStationApiService {

    @GET("k101/ows")
    suspend fun getRecyclingStations(
        @Query("service") service: String = "WFS",
        @Query("version") version: String = "1.0.0",
        @Query("request") request: String = "GetFeature",
        @Query("typeName") typeName: String = "k101:genbrugsstation",
        @Query("outputFormat") outputFormat: String = "json",
        @Query("SRSNAME") srsName: String = "EPSG:4326"
    ): RecyclingStationsResponse
}