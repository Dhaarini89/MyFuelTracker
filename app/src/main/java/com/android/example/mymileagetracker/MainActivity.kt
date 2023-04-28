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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        textPrevMileage= findViewById(R.id.text_prev_mileage)
        textAvgMileage = findViewById(R.id.text_avg_mileage)

        mAddFab = findViewById(R.id.add_fab)
        mAddFab!!.setOnClickListener{
                    val intent =Intent(this,RefuelRecordActivity::class.java)
                     startActivity(intent)
               }
    }

    override fun onStart() {
        super.onStart()
        val sharedPref = getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        val storedGasType =sharedPref.getString("GasType",null)
        if (storedGasType=="Electric")
        {
            textPrevMileage!!.setText("0.0")
            textAvgMileage!!.setText("0.0")
            mAddFab!!.visibility=View.GONE
        }
        else {
            mAddFab!!.visibility = View.VISIBLE
            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.CEILING

              lifecycleScope.launch {
                 val PrevAvgMileage =df.format(viewModel.getPrevMileage()).toString()
                  val AvgMileage =viewModel.calculateAverageMileage()
                  Log.d("fueltotalwhen",AvgMileage.toString())
                  textPrevMileage!!.setText(PrevAvgMileage)
                  textAvgMileage!!.setText(AvgMileage)

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
            R.id.action_settings -> {
                val intent : Intent =Intent(this,SettingsActivity::class.java)
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

}