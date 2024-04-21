package example.com.taxiapp.models

import com.google.gson.annotations.SerializedName

class VehiclesResponse(@SerializedName("poiList") val vehicles: List<Vehicle>)