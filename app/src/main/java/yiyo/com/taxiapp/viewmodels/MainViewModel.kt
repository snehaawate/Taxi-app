package example.com.taxiapp.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xwray.groupie.Item
import com.xwray.groupie.OnItemClickListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import example.com.taxiapp.api.VehiclesRepository
import example.com.taxiapp.helpers.plusAssign
import example.com.taxiapp.items.VehicleItem
import example.com.taxiapp.models.Vehicle.Companion.POOLING
import example.com.taxiapp.models.Vehicle.Companion.TAXI

class MainViewModel : ViewModel(), OnItemClickListener {

    private val repository by lazy { VehiclesRepository() }
    private val compositeDisposable by lazy { CompositeDisposable() }

    private val vehicles by lazy { HashMap<String, List<VehicleItem>>() }
    private val groupedVehicles by lazy { MutableLiveData<Map<String, List<VehicleItem>>>() }
    private val vehicleUpdates by lazy { MutableLiveData<Triple<VehicleItem, List<VehicleItem>, List<VehicleItem>>>() }

    fun loadData() {
        compositeDisposable += repository.getVehicles()
            .map { data -> data.vehicles.map { VehicleItem(it) }.groupBy { it.vehicle.fleetType } }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { data ->
                vehicles.clear()
                vehicles.putAll(data)
                groupedVehicles.value = data
            }
    }

    fun initialData(): LiveData<Map<String, List<VehicleItem>>> = groupedVehicles

    fun vehicleUpdates(): MutableLiveData<Triple<VehicleItem, List<VehicleItem>, List<VehicleItem>>> = vehicleUpdates

    override fun onItemClick(item: Item<*>, view: View) {
        if (item is VehicleItem) {
            val toggledItem = item.copy(isSelected = !item.isSelected)
            val taxis =
                vehicles[TAXI]?.map { vehicle -> vehicle.copy(isSelected = (vehicle == item) && toggledItem.isSelected) }
            val pooling =
                vehicles[POOLING]?.map { vehicle -> vehicle.copy(isSelected = (vehicle == item) && toggledItem.isSelected) }
            vehicleUpdates.value = Triple(toggledItem, taxis.orEmpty(), pooling.orEmpty())
        }
    }
}