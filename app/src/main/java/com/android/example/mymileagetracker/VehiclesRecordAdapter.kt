package com.android.example.database


import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.example.mymileagetracker.databinding.ListVehiclesRecordsBinding

class VehiclesRecordHolder(val binding: ListVehiclesRecordsBinding) :
    RecyclerView.ViewHolder(binding.root)
{
    fun bind(vehicle: DatabaseVehicle,onVehiclesClicked: (carname: String) -> Unit,onVehiclesLongClicked: (carname: String) -> Unit) {
        binding.txtCarname.text=vehicle.carname
         binding.root.setOnClickListener {
            onVehiclesClicked(vehicle.carname)
        }
        binding.root.setOnLongClickListener {
          onVehiclesLongClicked(vehicle.carname)
            return@setOnLongClickListener true
        }

    }
}


class VehiclesRecordAdapter(private val vehicles: List<DatabaseVehicle>,
                       private val onVehiclesClicked:(carname:String) -> Unit,
                            private val onVehiclesLongClicked: (carname: String) -> Unit
) : RecyclerView.Adapter<VehiclesRecordHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehiclesRecordHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListVehiclesRecordsBinding.inflate(inflater,parent,false)
        Log.d("Vehicles","yesadapater")
        return VehiclesRecordHolder(binding)

    }

    override fun onBindViewHolder(holder: VehiclesRecordHolder, position: Int) {
        val vehiclesrecord = vehicles[position]
        Log.d("VehiclesAdapter",vehiclesrecord.carname)
        holder.bind(vehiclesrecord, onVehiclesClicked,onVehiclesLongClicked)
    }

    override fun getItemCount() = vehicles.size

}