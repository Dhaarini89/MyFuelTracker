package com.android.example.mymileagetracker

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.icu.text.AlphabeticIndex
import android.os.Build.VERSION_CODES.M
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.example.database.DatabaseRefuel
import com.android.example.database.RefuelRecordAdapter
import com.android.example.generic.formatfloat
import com.android.example.mymileagetracker.databinding.ActivityRefuelBinding
import com.android.example.mymileagetracker.databinding.ActivityRefuelRecordBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Thread.sleep
import java.nio.file.Files.delete
import java.text.SimpleDateFormat
import java.util.*
import java.util.Collections.shuffle

class RefuelRecordActivity : AppCompatActivity()
{
    private var actionMode: ActionMode? = null
    var updateFlag :Int?=0
    var DBid: Int?=0
    var dateString :String?=null
    var prev_kms :Int?=0
    var prev_kms_db :Int?=0
    var prev_qunatity :Float?=0.0f
    var et_date : EditText? =null
    var flag : Int =0
    var updatedRecord :DatabaseRefuel?=null
    //   val inflater = LayoutInflater.from(this)
    // val view = inflater.inflate(R.layout.activity_refuel, null)
    private lateinit var binding: ActivityRefuelBinding
    private lateinit var Mainbinding : ActivityRefuelRecordBinding
    private val refuelViewModel : RefuelViewModel by viewModels()
        var refuelsRecord :List<DatabaseRefuel>? =null
        var adapter :RefuelRecordAdapter?=null
       fun  loadingRecyclerview()
       {
           GlobalScope.launch(Dispatchers.Main) {
               refuelsRecord = refuelViewModel.getRefuelRecords()
               refuelsRecord?.let { it ->
                   Log.d("AvgFuel","Came")
                   Mainbinding.refuelRecordsRecyclerView.adapter = RefuelRecordAdapter(it)
                   { id ->
                       onRefuelRecordClicked(id)

                   }
                   enableSwipe()
               }
           }
       }
        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mainbinding =ActivityRefuelRecordBinding.inflate(layoutInflater)
        setContentView(Mainbinding.root)
        Mainbinding.refuelRecordsRecyclerView.layoutManager = LinearLayoutManager(this)
            populaddrefueldialog(id=0)
            loadingRecyclerview()
    }

    fun enableSwipe()
    {
        val simpleItemTouchCallBack = object :ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT ) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) =false
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int)
            {
                    val position = viewHolder.adapterPosition
                    if (direction == ItemTouchHelper.LEFT) {
                        Log.d("Position",position.toString())
                       val deletedModel = refuelsRecord!![position]
                       // Mainbinding.refuelRecordsRecyclerView.adapter!!.removeItem(position)
                        GlobalScope.launch(Dispatchers.IO) {
                            val deletedstatus =1
                            refuelViewModel.deleteDbRefuel(deletedModel.id)
                            refuelViewModel.deleteDbMileage(position, deletedModel.id)
                            refuelViewModel.recalculateAverageMileagebyPrevRecord(deletedstatus,deletedModel)
                            Log.d("AvgPositionDelete",position.toString())
                        }
                        GlobalScope.launch(Dispatchers.Main) {
                            refuelsRecord = refuelViewModel.getRefuelRecords()
                            refuelsRecord?.let { it ->
                                Mainbinding.refuelRecordsRecyclerView.adapter =
                                    RefuelRecordAdapter(it)
                                    { id ->
                                        onRefuelRecordClicked(id)
                                    }

                            }
                        }
                        // showing snack bar with Undo option
                        val snackbar = Snackbar.make(Mainbinding.refuelRecordsRecyclerView, " removed from Recyclerview!", Snackbar.LENGTH_LONG)
                        snackbar.setAction("UNDO") {
                            // undo is selected, restore the deleted item
                           //  adapter!!.restoreItem(deletedModel, position)
                            GlobalScope.launch(Dispatchers.IO) {
                                val deletedstatus =0
                                Log.d("AvgDeletedStats",deletedstatus.toString())
                                refuelViewModel.addRefuel(deletedModel)
                                refuelViewModel.calculateAverageMileagebyPreviousRecord(deletedModel.id)
                                refuelViewModel.recalculateAverageMileagebyPrevRecord(deletedstatus,deletedModel)

                            }

                            GlobalScope.launch(Dispatchers.Main) {
                                refuelsRecord = refuelViewModel.getRefuelRecords()
                                refuelsRecord?.let { it ->
                                    Mainbinding.refuelRecordsRecyclerView.adapter =
                                        RefuelRecordAdapter(it)
                                        { id ->
                                            onRefuelRecordClicked(id)
                                        }

                                }
                            }
                        }
                        snackbar.setActionTextColor(Color.YELLOW)
                        snackbar.show()
                    }
                }
            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean)
            {

                val icon: Bitmap
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                   val p=Paint()
                    val itemView = viewHolder.itemView
                    val height = itemView.bottom.toFloat() - itemView.top.toFloat()
                    val width = height / 3

                    if (dX > 0) {
                        p.color =Color.parseColor("#388E3C")
                        val background =
                            RectF(itemView.left.toFloat(), itemView.top.toFloat(), dX, itemView.bottom.toFloat())
                        c.drawRect(background, p)
                        icon = BitmapFactory.decodeResource(resources, R.drawable.ic_delete)
                        val icon_dest = RectF(
                            itemView.left.toFloat() + width,
                            itemView.top.toFloat() + width,
                            itemView.left.toFloat() + 2 * width,
                            itemView.bottom.toFloat() - width
                        )
                        c.drawBitmap(icon, null, icon_dest, p)
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
        val itemTouchHelper =ItemTouchHelper(simpleItemTouchCallBack)
        itemTouchHelper.attachToRecyclerView( Mainbinding.refuelRecordsRecyclerView)
    }

    fun populaddrefueldialog(id: Int)
    {
        val builder = AlertDialog.Builder(this)
        val binding =ActivityRefuelBinding.inflate(LayoutInflater.from(this))
        builder.setView(binding.root)
        if (updateFlag == 1) {
            GlobalScope.launch(Dispatchers.IO) {
                updatedRecord = refuelViewModel.getRefuelRecord(id)
            }
            GlobalScope.launch(Dispatchers.Main) {
                DBid =id
                prev_kms = updatedRecord!!.prev_kms
                prev_qunatity = updatedRecord!!.prev_quantity
                binding.addButton.setText("Update")
                binding.etPriceL.setText(updatedRecord!!.price.toString())
                binding.etGasL.setText(updatedRecord!!.Quantity.toString())
                binding.etTotalcost.setText(updatedRecord!!.totalCost.toString())
                binding.etOdometer.setText(updatedRecord!!.odometerReading.toString())
                val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                binding.etDate.setText(format.format(updatedRecord!!.dateOfRefuel))
                binding.customGasType.setText(updatedRecord!!.gasType)
            }
        }
       // SetupUI()
        val sharedPref = getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        val storedCurrencySymbol =sharedPref.getInt("CurrencySymbol",0)
        val storedGasType =sharedPref.getString("GasType",null)
        if (storedCurrencySymbol!=0)
        {
            binding.price.setImageResource(storedCurrencySymbol)
        }
        var resourceType :Int?=0
        if (storedGasType =="Gasoline")
        {
            resourceType =R.array.Gas_SubTypeGasoline
        }
        else{
            resourceType =R.array.Gas_SubTypeDiesel
        }

        val adapter1 = ArrayAdapter.createFromResource(
            this, resourceType!!,
            R.layout.spinner_dropdown
        )
        adapter1.setDropDownViewResource(R.layout.spinner_dropdown)
        binding.customGasType.setAdapter(adapter1)
        // Format the date as a string
        val today = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val dateString = formatter.format(today)
        // Set the text of the AutoCompleteTextView to the formatted date string
        binding.etDate.setText(dateString)
        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointBackward.now())
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setCalendarConstraints(constraintsBuilder.build())
            .setTheme(R.style.CustomMaterialDatePicker)
            .build()
            datePicker.addOnPositiveButtonClickListener { selectedDate ->
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.timeInMillis = selectedDate
            val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val dateString = formatter.format(calendar.time)
                binding.etDate!!.setText(dateString)
        }
        binding.etDate.setOnClickListener {
            datePicker.show(supportFragmentManager,"datePicker")
        }
        binding.etOdometer.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                binding.tilOdometer.error=null
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
        binding.etTotalcost.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
               if (binding.etTotalcost.text!!.isNotEmpty()) {
                    val convertCost = p0.toString().toFloat()
                    if (binding.etGasL.text!!.isNotEmpty()) {
                        val litres = binding.etGasL.text.toString().toFloat()
                        val priceperl = convertCost / litres
                        binding.etPriceL.setText(priceperl!!.toString())
                    }
                }
                else
                {
                    binding.etPriceL.setText("0.0")

                }
            }

            override fun afterTextChanged(p0: Editable?) {
                binding.tilTotalcost.error=null
            }
        })

            binding.etGasL.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.etGasL.text!!.isNotEmpty()) {
                    val litres = p0.toString().toFloat()
                    if (binding.etTotalcost.text!!.isNotEmpty()) {
                        val convertCost = binding.etTotalcost.text.toString().toFloat()
                        val priceperl = convertCost / litres
                        binding.etPriceL.setText(priceperl!!.toString())
                    }
                }
                else
                {
                    binding.etPriceL.setText("0.0")

                }

            }
            override fun afterTextChanged(p0: Editable?) {
                binding.tilGasL.error=null
            }

        })
        builder.setTitle("Add Refuel")
        builder.create()
        val dialog : AlertDialog =builder.create()
        val window = dialog.window
        window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this@RefuelRecordActivity, R.color.light_gray)))
        dialog.show()
        binding.addButton.setOnClickListener {
            Log.d("AvgFuel", "Step 1")
            var added :Int =1
            GlobalScope.launch(Dispatchers.Main) {
                Log.d("AvgFuel", "Step 2")
                prev_kms_db = 0
                prev_kms_db = withContext(Dispatchers.IO) {
                    Log.d("AvgFuel", "Step 3")
                    refuelViewModel.getLastRecord().odometerReading
                }
                if (binding.etOdometer.text!!.isEmpty() || ((Integer.parseInt(binding.etOdometer.text!!.toString()) <= prev_kms_db!!) && updateFlag == 0)) {
                    flag = 0
                    Log.d("AvgFuel", "Step 4" + "validation")
                    binding.tilOdometer.setError("Please enter valid km in numbers,more than " + prev_kms_db.toString() + "Kms")

                } else if (binding.etTotalcost.text!!.isEmpty()) {
                    flag = 0
                    binding.tilTotalcost.setError("Please enter total cost of refueled")

                } else if (binding.etGasL.text!!.isEmpty()) {
                    flag = 0
                    binding.tilGasL.setError("Please enter total litres refueled")
                } else {
                    Log.d("AvgFuel", "Step 5 after validation")
                    prev_kms_db = 0
                    binding.tilTotalcost.setError(null)
                    binding.tilGasL.setError(null)
                    binding.tilOdometer.setError(null)
                    flag = 1
                }
                Log.d("AvgFuel", "Step 6")
                if (flag == 1) {
                    Log.d("AvgFuel", "Step 7")
                     added=addRefuelToDb(binding)
                }
                if (added == 0) {
                    loadingRecyclerview()
                    dialog.dismiss()
                }
            }

        }

        binding.cancelButton.setOnClickListener {
            dialog.dismiss()
        }

    }

    fun onRefuelRecordClicked(id:Int)
    {
        updateFlag=1
      populaddrefueldialog(id)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.menu_delete, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        super.onContextItemSelected(item)
        return handleContextMenuItem(item)
    }

    private fun handleContextMenuItem(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.action_delete -> {
                true
            }
            else -> false
        }
    }

    fun addRefuelToDb(binding: ActivityRefuelBinding) :Int
    {
        GlobalScope.launch(Dispatchers.IO) {
        Log.d("AvgFuel", "Step 8")
            val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val datestring = binding.etDate.text.toString()
            val date = formatter.parse(datestring)
            if (updateFlag == 1) {
                var newRefuel = DatabaseRefuel(
                    id = DBid!!,
                    odometerReading = Integer.parseInt(binding.etOdometer.text.toString()),
                    gasType = binding.customGasType.text.toString(),
                    Quantity = formatfloat(binding.etGasL.text.toString()),
                    totalCost = formatfloat(binding.etTotalcost.text.toString()),
                    price = formatfloat(binding.etPriceL.text.toString()),
                    dateOfRefuel = date, prev_kms = prev_kms!!, prev_quantity = prev_qunatity!!)
                Log.d("UpdateRecord", newRefuel.toString())
                    refuelViewModel.updateRefuelRecord(databaseRefuel = newRefuel)
                    refuelViewModel.updateMileage(newRefuel)
                    updateFlag = 0
                }
            else {
                    var newRefuel = DatabaseRefuel(
                        odometerReading = Integer.parseInt(binding.etOdometer.text.toString()),
                        gasType = binding.customGasType.text.toString(),
                        Quantity = formatfloat(binding.etGasL.text.toString()),
                        totalCost = formatfloat(binding.etTotalcost.text.toString()),
                        price = formatfloat(binding.etPriceL.text.toString()),
                        dateOfRefuel = date,
                        prev_kms = 0, prev_quantity = 0.0f)
                    Log.d("AvgFuel", "Step 9")
                    newRefuel.prev_kms = refuelViewModel.getLastRecord().odometerReading
                    newRefuel.prev_quantity = refuelViewModel.getLastRecord().Quantity
                    refuelViewModel.addRefuel(newRefuel)
                    refuelViewModel.calculateAverageMileagebyPreviousRecord(refuelViewModel.getLastRecord().id)
                    Log.d("AvgFuel", "Step 11")
                }
        }
     return 0

    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_refuel, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> {
                populaddrefueldialog(id=0)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}