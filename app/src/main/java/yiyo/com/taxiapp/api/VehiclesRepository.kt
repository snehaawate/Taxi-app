package example.com.taxiapp.api

import io.reactivex.Observable
import example.com.taxiapp.models.VehiclesResponse

class VehiclesRepository {

    fun getVehicles(): Observable<VehiclesResponse> {
        val service = RetrofitBuilder.createService(MyTaxiApi::class.java)
        return service.getCars()
    }
}