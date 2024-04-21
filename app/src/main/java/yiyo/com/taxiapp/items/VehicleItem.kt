package example.com.taxiapp.items

import com.xwray.groupie.Item
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.OnItemLongClickListener
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.databinding.ViewHolder
import example.com.taxiapp.R
import example.com.taxiapp.databinding.ItemVehicleBinding
import example.com.taxiapp.models.Vehicle
import example.com.taxiapp.models.Vehicle.Companion.TAXI

data class VehicleItem(val vehicle: Vehicle, val isSelected: Boolean = false) : BindableItem<ItemVehicleBinding>() {

    override fun bind(viewBinding: ItemVehicleBinding, position: Int) {
        viewBinding.vehicle = vehicle
        viewBinding.isSelected = isSelected

        viewBinding.imageViewLogo.setImageResource(
            R.drawable.taxi.takeIf { vehicle.fleetType == TAXI } ?: R.drawable.pooling)
    }

    override fun bind(
        holder: ViewHolder<ItemVehicleBinding>,
        position: Int,
        payloads: MutableList<Any>,
        onItemClickListener: OnItemClickListener?,
        onItemLongClickListener: OnItemLongClickListener?
    ) {
        super.bind(holder, position, payloads, onItemClickListener, onItemLongClickListener)
        holder.binding.container.setOnClickListener { onItemClickListener?.onItemClick(this, holder.itemView) }
    }

    override fun isSameAs(other: Item<*>): Boolean {
        return when (other) {
            is VehicleItem -> vehicle.heading == other.vehicle.heading
            else -> false
        }
    }

    override fun getLayout(): Int = R.layout.item_vehicle

    override fun getSpanSize(spanCount: Int, position: Int): Int = spanCount / SPANS

    companion object {
        const val SPANS = 2
    }
}