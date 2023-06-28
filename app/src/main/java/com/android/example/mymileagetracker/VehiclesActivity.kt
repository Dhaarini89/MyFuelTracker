package com.android.example.mymileagetracker

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.ActionMode
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.example.database.DatabaseRefuel
import com.android.example.database.DatabaseVehicle
import com.android.example.database.RefuelRecordAdapter
import com.android.example.database.VehiclesRecordAdapter
import com.android.example.generic.formatfloat
import com.android.example.mymileagetracker.MainActivity.Companion.context
import com.android.example.mymileagetracker.MainActivity.Companion.updateFlag
import com.android.example.mymileagetracker.databinding.ActivityMainBinding
import com.android.example.mymileagetracker.databinding.ActivityRefuelBinding
import com.android.example.mymileagetracker.databinding.ActivityRefuelRecordBinding
import com.android.example.mymileagetracker.databinding.ActivityVehiclesBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class VehiclesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVehiclesBinding
    private val refuelViewModel : RefuelViewModel by viewModels()
    var flag : Int =0
    var updatedRecord : DatabaseRefuel?=null
    //   val inflater = LayoutInflater.from(this)
    // val view = inflater.inflate(R.layout.activity_refuel, null)
    //private lateinit var binding: ActivityRefuelBinding
    private lateinit var Mainbinding : ActivityRefuelRecordBinding
    var vehiclesRecord :List<DatabaseVehicle>? =null
    var adapter : VehiclesRecordAdapter?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =ActivityVehiclesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadingRecyclerview()

    }
        fun  loadingRecyclerview()
        {
            GlobalScope.launch(Dispatchers.Main) {
                vehiclesRecord = refuelViewModel.getVehiclesList()
                Log.d("vehicles",vehiclesRecord.toString())
              //  Toast.makeText(this,vehiclesRecord.toString(),Toast.LENGTH_LONG
                vehiclesRecord?.let { it ->
                    Log.d("AvgVehicles", "Came")
                    binding.vehiclesRecordsRecyclerView.layoutManager = LinearLayoutManager(this@VehiclesActivity)
                    context=this@VehiclesActivity
                    binding.vehiclesRecordsRecyclerView.adapter = VehiclesRecordAdapter(it,{
                            carname ->
                        onVehiclesRecordClicked(carname)
                    })
                    { carname ->
                        onVehiclesLongClicked(carname)
                    }
                }
            }
        }

        fun onVehiclesRecordClicked(carname :String)
        {
            val intent = Intent(this,SettingsActivity::class.java)
            intent.putExtra("carName",carname)
            startActivity(intent)
            updateFlag=1
        }

    fun onVehiclesLongClicked(carname :String)
    {

    val builder = context?.let { it1 ->
        androidx.appcompat.app.AlertDialog.Builder(it1, R.style.MyAlertDialogStyle)
    }
    builder!!.setTitle("Delete a Vehicle")
    // Create a TextView to set the text color
    val message = TextView(context)
    message.text = "\n         Are you sure you want to delete?"
    message.setTextColor(ContextCompat.getColor(context!!, R.color.white))
    builder.setView(message)
    builder.setPositiveButton("Yes") { dialog, _ ->
        // code to exit the application
        GlobalScope.launch(Dispatchers.IO) {
            refuelViewModel.deletebyvehiclename(carname)
            loadingRecyclerview()
        }
        dialog.dismiss()

    }
    builder.setNegativeButton("No") { dialog, _ ->
        // code to handle canceling the exit
        dialog.dismiss()
    }
    val dialog = builder.create()
    dialog.show()


    }

        override fun onCreateOptionsMenu(menu: Menu): Boolean {
            // Inflate the menu; this adds items to the action bar if it is present.
            menuInflater.inflate(R.menu.menu_refuel, menu)
            return true
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.action_add -> {
                    val intent = Intent(this,SettingsActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
        }

    override fun onResume() {
        super.onResume()
        loadingRecyclerview()
        Log.d("Resume","yes")
    }
    }

