package com.android.example.mymileagetracker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.android.example.mymileagetracker.databinding.ActivitySettingsBinding
import java.lang.Thread.sleep

class SettingsActivity : AppCompatActivity() {
    lateinit var binding :ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
                val sharedPref = getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.putString("GasType", gasType!!)
                editor.apply()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            // do nothing
            }
        }

    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_delete, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                val sharedPref = getSharedPreferences("storesettings_pref", Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.putString("Year", binding.etYear.text.toString())
                editor.putString("Model",binding.etModel.text.toString())
                editor.putString("Make", binding.etMake.text.toString())
                editor.putString("LicensePlate",binding.etLicenseplate.text.toString())
                editor.putString("DistanceUnit", binding.spinner1.selectedItem.toString())
                editor.putString("FuelUnit",binding.spinner2.selectedItem.toString())
                editor.putString("CurrencyUnit", binding.spinner3.selectedItem.toString())
                editor.putString("GasType",binding.spinnerGastype.selectedItem.toString())
                editor.putString("TankCapacity",binding.etTankCapacity.text.toString())
                editor.apply()
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
        val sharedPref = getSharedPreferences("storesettings_pref", Context.MODE_PRIVATE)
       if (sharedPref!=null) {
           binding.etYear.setText(sharedPref.getString("Year", ""))
           binding.etModel.setText(sharedPref.getString("Model", ""))
           binding.etMake.setText(sharedPref.getString("Make", ""))
           binding.etLicenseplate.setText(sharedPref.getString("LicensePlate", ""))
           binding.etTankCapacity.setText(sharedPref.getString("TankCapacity",""))
           binding.spinner1.setSelection(resources.getStringArray(R.array.Distance_Unit)
               .indexOf(sharedPref.getString("DistanceUnit", "")))
           binding.spinner2.setSelection(resources.getStringArray(R.array.Fuel_Unit)
               .indexOf(sharedPref.getString("FuelUnit", "")))
           binding.spinner3.setSelection(resources.getStringArray(R.array.Currency_Unit)
               .indexOf(sharedPref.getString("CurrencyUnit", "")))
           binding.spinnerGastype.setSelection(resources.getStringArray(R.array.Gas_Type)
               .indexOf(sharedPref.getString("GasType", "")))
       }
    }

}