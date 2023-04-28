package com.android.example.database

import android.content.Context
import android.graphics.ColorSpace
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.android.example.mymileagetracker.databinding.ListRefuelRecordsBinding
import java.text.SimpleDateFormat
import java.util.*

class RefuelRecordHolder(val binding: ListRefuelRecordsBinding,val currencysymbol:String,val fuelUnit:String,val DistanceUnit:String) : RecyclerView.ViewHolder(binding.root)
{

    fun bind(refuel : DatabaseRefuel,onRefuelClicked: (id: Int) -> Unit) {
        binding.tvTotalCost.text ="TotalCost:" +currencysymbol + refuel.totalCost.toString()
        val formattedDateString = SimpleDateFormat("dd MM yyyy", Locale.getDefault()).format(refuel.dateOfRefuel)
        binding.tvDate.text = formattedDateString
        binding.tvKmReading.text = refuel.odometerReading.toString() +DistanceUnit
       binding.tvText.text = refuel.Quantity.toString() +fuelUnit+"->" +currencysymbol +refuel.price.toString() + "/" +fuelUnit +"(" + refuel.gasType + ")"
        binding.root.setOnClickListener {
            onRefuelClicked(refuel.id)
        }

    }
}


class RefuelRecordAdapter(private val refuels: List<DatabaseRefuel>,
                       private val onRefuelClicked:(id: Int) -> Unit
) : RecyclerView.Adapter<RefuelRecordHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RefuelRecordHolder {
        val inflater = LayoutInflater.from(parent.context)
        val share_pref =parent.context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        val valuefromshare_Pref=share_pref.getString("CurrencySymbolTxt","#")
        Log.d("valuefromshare",valuefromshare_Pref.toString())
        val sharedPref = parent.context.getSharedPreferences("storesettings_pref", Context.MODE_PRIVATE)
        val fuelUnit=sharedPref.getString("FuelUnit","L")
        val DistanceUnit=sharedPref.getString("DistanceUnit", "Kms")

        val binding = ListRefuelRecordsBinding.inflate(inflater,parent,false)
        return RefuelRecordHolder(binding,valuefromshare_Pref!!,fuelUnit!!,DistanceUnit!!)
    }

    override fun onBindViewHolder(holder: RefuelRecordHolder, position: Int) {
        val refuelrecord = refuels[position]
        Log.d("refuelrecordAverage",refuelrecord.toString())
         holder.bind(refuelrecord, onRefuelClicked)
        //holder.bind.setOn
    }

    override fun getItemCount() = refuels.size

}