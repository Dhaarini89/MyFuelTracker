package com.android.example.mymileagetracker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope

import com.android.example.mymileagetracker.databinding.ActivityMainBinding
import com.android.example.mymileagetracker.databinding.ContentMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Thread.sleep
import java.math.RoundingMode
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    val viewModel : RefuelViewModel by viewModels()
    var mAddFab: FloatingActionButton? = null
    private lateinit var binding: ActivityMainBinding
    var textPrevMileage: TextView?=null
    var textAvgMileage:TextView?=null
    var spinner:Spinner?=null
    var vehicleList :MutableList<String>?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        textPrevMileage= findViewById(R.id.text_prev_mileage)
        textAvgMileage = findViewById(R.id.text_avg_mileage)
        spinner =findViewById<Spinner>(R.id.Head_Spinner)
        loadingSpinner()
        mAddFab = findViewById(R.id.add_fab)
        mAddFab!!.setOnClickListener{
                    val intent =Intent(this,RefuelRecordActivity::class.java)
                     startActivity(intent)
               }
    }
    fun settingSpinner()
    {
        val sharedPref = getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        val Carname=sharedPref.getString("CarName", null)
        Carname?.let { value ->
            val position = (spinner!!.adapter as ArrayAdapter<String>).getPosition(value)
            spinner!!.setSelection(position)
        }
        spinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent.getItemAtPosition(position) as String
                val editor = sharedPref.edit()
                carname =selectedItem
                calculateAvg()
                GlobalScope.launch(Dispatchers.IO) {
                    val carDetails =viewModel.getVehicleByName(carname)
                    editor.putString("CarName", carname)
                    editor.putString("CurrencySymbolTxt",carDetails.currencyUnit)
                    editor.putString("FuelUnit",carDetails.fuelUnit)
                    editor.putString("DistanceUnit", carDetails.distanceUnit)
                    editor.apply()
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }

        }
    }
    override fun onResume() {
        super.onResume()
        loadingSpinner()

    }

    fun loadingSpinner()
    {
        lifecycleScope.launch {
            vehicleList =viewModel.getVehiclesListName()
            val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, vehicleList!!)
            adapter.setDropDownViewResource(R.layout.spinner_dropdown)
            spinner!!.adapter =adapter

            val textView = findViewById<TextView>(R.id.Head_Textview)

            if (vehicleList!!.isEmpty())
            {
                calculateAvg()
                textView.visibility=View.VISIBLE
                spinner!!.visibility=View.INVISIBLE

            }
            else
            {
                textView.visibility=View.INVISIBLE
                spinner!!.visibility =View.VISIBLE
            }
            settingSpinner()

        }
    }
    override fun onStart() {
        super.onStart()
    }

   fun calculateAvg() {
       lifecycleScope.launch {
           var carDetail = viewModel.getVehicleByName(carname)

           if (carDetail != null) {

               if (carDetail.gasType == "Electric") {
                   textPrevMileage!!.setText("0.0")
                   textAvgMileage!!.setText("0.0")
                   mAddFab!!.visibility = View.GONE
               } else {
                   mAddFab!!.visibility = View.VISIBLE
                   val df = DecimalFormat("#.##")
                   df.roundingMode = RoundingMode.CEILING
                   lifecycleScope.launch {
                       val PrevAvgMileage = df.format(viewModel.getPrevMileage(carname)).toString()
                       val AvgMileage = viewModel.calculateAverageMileage(carname)
                       Log.d("fueltotalwhen", AvgMileage.toString())
                       textPrevMileage!!.setText(PrevAvgMileage)
                       textAvgMileage!!.setText(AvgMileage)

                   }
               }
           }
           else{
               textPrevMileage!!.setText("0.0")
               textAvgMileage!!.setText("0.0")
               mAddFab!!.visibility = View.GONE
           }
       }
   }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_vehicles -> {
                val intent : Intent =Intent(this,VehiclesActivity::class.java)
                startActivity(intent)
                true
            }

            R.id.action_exit ->
            {
                val builder = AlertDialog.Builder(this,R.style.MyAlertDialogStyle)
                builder.setTitle("Exit Application")
                // Create a TextView to set the text color
                val message = TextView(this)
                message.text = "\n         Are you sure you want to exit?"
                message.setTextColor(ContextCompat.getColor(this, R.color.white))
                builder.setView(message)
                builder.setPositiveButton("Yes") { dialog, _ ->
                    // code to exit the application
                    finishAffinity()
                }
                builder.setNegativeButton("No") { dialog, _ ->
                    // code to handle canceling the exit
                    dialog.dismiss()
                }
                val dialog = builder.create()
                dialog.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    companion object {
        var updateFlag :Int?=0
        var carname =""
        var context :Context?=null

    }

}

