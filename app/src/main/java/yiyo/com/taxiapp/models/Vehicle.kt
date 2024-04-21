package example.com.taxiapp.models

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName

data class Vehicle(
    @SerializedName("id") val id: Int,
    @SerializedName("coordinate") val coordinate: LatLng,
    @SerializedName("fleetType") val fleetType: String,
    @SerializedName("heading") val heading: Double
) {

    fun label() = if (fleetType == TAXI) "Taxi" else "Match"

    fun seats() = if (fleetType == TAXI) "4 seats" else "1 - 2 seats"

    companion object {
        const val TAXI = "TAXI"
        const val POOLING = "POOLING"
    }
}