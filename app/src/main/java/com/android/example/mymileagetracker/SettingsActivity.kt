package com.android.example.mymileagetracker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.android.example.database.DatabaseVehicle
import com.android.example.mymileagetracker.MainActivity.Companion.updateFlag
import com.android.example.mymileagetracker.databinding.ActivitySettingsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Thread.sleep

class SettingsActivity : AppCompatActivity() {
    lateinit var binding :ActivitySettingsBinding
    private val refuelViewModel : RefuelViewModel by viewModels()
    var updateId=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (updateFlag ==1)
        {
            val carName = intent.getStringExtra("carName")
            lifecycleScope.launch {
               val values= refuelViewModel.getVehicleByName(carName!!)
                updateId=values.id
                Log.d("value",values.toString())
                binding.etCarname.setText(values.carname)
                binding.etYear.setText(values.year)
                binding.etModel.setText(values.model)
                binding.etMake.setText(values.make)
                binding.etLicenseplate.setText(values.licenseplate)
                binding.etTankCapacity.setText(values.capacity)
                binding.spinner1.setSelection(resources.getStringArray(R.array.Distance_Unit)
                    .indexOf(values.distanceUnit))
                binding.spinner2.setSelection(resources.getStringArray(R.array.Fuel_Unit)
                    .indexOf(values.fuelUnit))
                binding.spinner3.setSelection(resources.getStringArray(R.array.Currency_Unit)
                    .indexOf(values.currencyUnit))
                binding.spinnerGastype.setSelection(resources.getStringArray(R.array.Gas_Type)
                    .indexOf(values.gasType))

            }

        }
        val currencyarray =resources.getStringArray(R.array.Currency_Unit)
        var currencySymbol :Int?=0
        var currencySymbolTxt :String?=null
        val gasTypearray = resources.getStringArray(R.array.Gas_Type)
        var gasType :String?=null
        val adapter1 = ArrayAdapter.createFromResource(
            this,
            R.array.Distance_Unit,
            R.layout.spinner_dropdown
        )
        adapter1.setDropDownViewResource(R.layout.spinner_dropdown)
        binding.spinner1.adapter=adapter1
        val adapter2 = ArrayAdapter.createFromResource(
            this,
            R.array.Fuel_Unit,
            R.layout.spinner_dropdown
        )
        adapter2.setDropDownViewResource(R.layout.spinner_dropdown)
        binding.spinner2.adapter=adapter2
        val adapter3 = ArrayAdapter.createFromResource(
            this,
            R.array.Currency_Unit,
            R.layout.spinner_dropdown
        )
        adapter3.setDropDownViewResource(R.layout.spinner_dropdown)
        binding.spinner3.adapter=adapter3
        binding.spinner3.onItemSelectedListener =object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                when (selectedItem) {
                    currencyarray[0] -> {
                        currencySymbolTxt="$"
                        currencySymbol =R.drawable.ic_price
                    }
                    currencyarray[1] ->
                    {
                        currencySymbolTxt="€"
                       currencySymbol =R.drawable.euros
                    }
                    currencyarray[2] -> {
                        currencySymbolTxt="₹"
                       currencySymbol = R.drawable.indianrupee
                    }
                    currencyarray[3] ->
                    {
                        currencySymbolTxt="£"
                        currencySymbol = R.drawable.ukpound
                    }

                }
                val sharedPref = getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.putInt("CurrencySymbol",currencySymbol!!)
                editor.putString("CurrencySymbolTxt",currencySymbolTxt!!)
                editor.apply()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // do nothing
            }
        }
        val adapter4 = ArrayAdapter.createFromResource(
            this,
            R.array.Gas_Type,
            R.layout.spinner_dropdown
        )
        adapter4.setDropDownViewResource(R.layout.spinner_dropdown)
        binding.spinnerGastype.adapter=adapter4
        binding.spinnerGastype.onItemSelectedListener =object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                when (selectedItem) {
                    gasTypearray[0] -> {
                            binding.etTankCapacity.visibility=View.GONE
                            binding.TankCapacity.visibility=View.GONE
                            gasType = gasTypearray[0]
                    }
                    gasTypearray[1] -> {
                        binding.etTankCapacity.visibility=View.VISIBLE
                        binding.TankCapacity.visibility=View.VISIBLE
                        gasType = gasTypearray[1]
                    }
                    gasTypearray[2] -> {
                        binding.etTankCapacity.visibility=View.VISIBLE
                        binding.TankCapacity.visibility=View.VISIBLE
                        gasType = gasTypearray[2]
                    }
                }

            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            // do nothing
            }
        }

    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_save, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                 var recordvehicle =DatabaseVehicle(
                  carname =  convertToString(binding.etCarname),
                  make =  convertToString(binding.etMake),
                  model =  convertToString(binding.etModel),
                  year =  convertToString(binding.etYear),
                   licenseplate = convertToString(binding.etLicenseplate),
                   distanceUnit = binding.spinner1.selectedItem.toString(),
                   fuelUnit = binding.spinner2.selectedItem.toString(),
                   currencyUnit = binding.spinner3.selectedItem.toString(),
                   gasType =binding.spinnerGastype.selectedItem.toString(),
                   capacity = convertToString(binding.etTankCapacity))

                GlobalScope.launch(Dispatchers.Main) {
                    Log.d("VehRecord","yes")
                    if (updateFlag == 0) {
                        Log.d("VehInsert","yes")
                        refuelViewModel.insertVehicles(recordvehicle)
                    }
                    else
                    {
                        Log.d("Vehupdate","yes")
                        recordvehicle.id=updateId
                        refuelViewModel.updateDatabasevehicle(recordvehicle)
                        updateFlag =0
                    }
                }
                Toast.makeText(applicationContext,"Your details are saved successfully",Toast.LENGTH_LONG).show()
                sleep(200)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()

    }

    fun convertToString(textView:TextView):String
    {
        val textValue = textView.text.toString()
        return textValue.takeIf { it.isNotEmpty() } ?: " "
    }

    override fun onBackPressed() {
        onBackPressedDispatcher.onBackPressed()
        updateFlag=0

    }
}