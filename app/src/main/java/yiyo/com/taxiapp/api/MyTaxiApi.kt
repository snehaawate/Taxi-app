package example.com.taxiapp.api

import com.google.android.gms.maps.model.LatLng
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import example.com.taxiapp.models.VehiclesResponse

interface MyTaxiApi {

    @GET("/")
    fun getCars(
        @Query(P1_LAT) p1Lat: Double = HAMBURG_1.latitude,
        @Query(P1_LON) p1Lon: Double = HAMBURG_1.longitude,
        @Query(P2_LAT) p2Lat: Double = HAMBURG_2.latitude,
        @Query(P2_LON) p2Lon: Double = HAMBURG_2.longitude
    ): Observable<VehiclesResponse>

    companion object {
        const val P1_LAT = "p1Lat"
        const val P1_LON = "p1Lon"
        const val P2_LAT = "p2Lat"
        const val P2_LON = "p2Lon"
        val HAMBURG_1 = LatLng(53.694865, 9.757589)
        val HAMBURG_2 = LatLng(53.394655, 10.099891)
    }
}