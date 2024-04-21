package example.com.taxiapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import example.com.taxiapp.R
import example.com.taxiapp.api.MyTaxiApi.Companion.HAMBURG_1
import example.com.taxiapp.api.MyTaxiApi.Companion.HAMBURG_2
import example.com.taxiapp.databinding.ActivityMainBinding
import example.com.taxiapp.items.ExpandableHeaderItem
import example.com.taxiapp.items.VehicleItem
import example.com.taxiapp.items.VehicleItem.Companion.SPANS
import example.com.taxiapp.models.Vehicle.Companion.POOLING
import example.com.taxiapp.models.Vehicle.Companion.TAXI
import example.com.taxiapp.viewmodels.MainViewModel


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private val width by lazy { resources.displayMetrics.widthPixels }
    private val height by lazy { resources.displayMetrics.heightPixels }
    private val mapPadding by lazy { (width * 0.12).toInt() }
    private val markerIcon by lazy { BitmapDescriptorFactory.fromResource(R.drawable.taxi_marker) }
    private val markerIconSelected by lazy { BitmapDescriptorFactory.fromResource(R.drawable.taxi_marker_selected) }

    private val binding by lazy { DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main) }
    private lateinit var viewModel: MainViewModel
    private val adapter = GroupAdapter<ViewHolder>().apply { spanCount = SPANS }
    private val taxiSection by lazy { Section() }
    private val poolingSection by lazy { Section() }
    private val bottomSheetBehavior by lazy { BottomSheetBehavior.from(binding.bottomSheet.container) }
    private val hamburgBounds by lazy {
        LatLngBounds.Builder().include(HAMBURG_1)
                .include(HAMBURG_2)
                .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        binding.viewModel = viewModel

        initRecyclerView()
        subscribeToChanges()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun subscribeToChanges() {
        viewModel.initialData().observe(this, Observer { vehicles -> showData(vehicles) })
        viewModel.vehicleUpdates().observe(this, Observer { taxis -> onVehicleSelection(taxis) })
    }

    private fun initRecyclerView() {
        val layoutManager = GridLayoutManager(this, adapter.spanCount)
        layoutManager.spanSizeLookup = adapter.spanSizeLookup

        with(binding.bottomSheet.recyclerView) {
            this.layoutManager = layoutManager
            adapter = this@MainActivity.adapter
        }
        adapter.setOnItemClickListener(viewModel)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isMapToolbarEnabled = false

        // Move the camera to Hamburg, Germany
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(hamburgBounds, width, height, mapPadding))
        viewModel.loadData()
    }

    private fun showData(vehiclesMap: Map<String, List<VehicleItem>>) {
        vehiclesMap[TAXI]?.let { taxis ->
            val header = ExpandableHeaderItem(TAXI, taxis.size)
            val expandableGroup = ExpandableGroup(header, true)
            taxiSection.update(taxis)
            expandableGroup.add(taxiSection)
            adapter.add(expandableGroup)
        }

        vehiclesMap[POOLING]?.let { pool ->
            val header = ExpandableHeaderItem(POOLING, pool.size)
            val expandableGroup = ExpandableGroup(header, true)
            poolingSection.update(pool)
            expandableGroup.add(poolingSection)
            adapter.add(expandableGroup)
        }

        addMarkers(vehiclesMap.values.flatten())
    }

    private fun onVehicleSelection(data: Triple<VehicleItem, List<VehicleItem>, List<VehicleItem>>) {
        val (toggledItem, taxis, pooling) = data
        taxiSection.update(taxis)
        poolingSection.update(pooling)
        addMarkers(taxis + pooling)

        if (toggledItem.isSelected) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(toggledItem.vehicle.coordinate, 12f))
        } else {
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(hamburgBounds, width, height, mapPadding))
        }
    }

    private fun addMarkers(pooling: List<VehicleItem>) {
        map.clear()
        pooling.asSequence()
                .map { item ->
                    MarkerOptions()
                            .position(item.vehicle.coordinate)
                            .title(item.vehicle.fleetType)
                            .icon(markerIconSelected.takeIf { item.isSelected } ?: markerIcon)
                            .zIndex(0.0f)
                }
                .forEach { map.addMarker(it) }
    }
}
