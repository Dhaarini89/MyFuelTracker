package com.android.example.mymileagetracker

import android.icu.text.AlphabeticIndex
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.room.PrimaryKey
import com.android.example.database.DatabaseMileage
import com.android.example.database.DatabaseRefuel
import com.android.example.database.RefuelRepository
import com.android.example.database.refuelDao
import kotlinx.coroutines.flow.Flow
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.Calendar
import java.util.UUID

class RefuelViewModel : ViewModel(){

    private val refuelRepository =RefuelRepository.get()
    suspend fun addRefuel(refuel: DatabaseRefuel)
    {
        refuelRepository.addRefuel(refuel)
    }
    suspend fun getTotalRefuelRecords() : Int
    {
        return refuelRepository.getTotalRefuelRecords()
    }
    suspend fun getLastRecord() : DatabaseRefuel
    {
        if (refuelRepository.getTotalRefuelRecords() >= 1) {
            return refuelRepository.getLastRecord()
        }
        else
        {
            var databaseRefuel =DatabaseRefuel(0,0,"",0.0f,0.0f,0.0f,Calendar.getInstance().time,0,0.0f)
            return databaseRefuel
        }

    }
    suspend fun getPrevKms() : Int
    {
        if (refuelRepository.getTotalRefuelRecords() >= 1) {
            return refuelRepository.getPrevKms()
        }
        else
            return 0
    }
    suspend fun getPrevQuantity() : Float
    {
        if (refuelRepository.getTotalRefuelRecords() >= 1) {
            return refuelRepository.getPrevQuantity()        }
        else
            return 0.0f

    }
    suspend fun deleteDbRefuel(id:Int)
    {
        return refuelRepository.deletebyId(id)
    }

    suspend fun deleteDbMileage(position :Int,refuelId:Int)
    {
        var i_refuelId =refuelId
        if (position == 0 && refuelRepository.getTotalMileageRecords() >= 1)
        {
            i_refuelId =refuelId+1
        }
        return refuelRepository.deletebyRefuelId(i_refuelId)
    }

    suspend fun recalculateAverageMileagebyPrevRecord(deletedstatus:Int,deletedModel : DatabaseRefuel)
    {   var updateId =0
        var litres =0.0f
        var kmsdiff =0
        Log.d("AvgDeletedStatsView",deletedstatus.toString())
        if (deletedstatus ==1) {
            val Record = refuelRepository.getRefuelRecord(deletedModel.id + 1)
            if (Record != null) {
                Record.prev_kms = deletedModel.prev_kms
                Record.prev_quantity = deletedModel.prev_quantity
                refuelRepository.updateRefuelRecord(Record)
                updateId = Record.id
                litres =Record.prev_quantity
                 kmsdiff = Record.odometerReading - Record.prev_kms


            }
        }
        else if (deletedstatus == 0)
        {
            Log.d("AvgDeletedStatsViewinside",deletedstatus.toString())
            val undorecord = refuelRepository.getRefuelRecord(deletedModel.id + 1)
            undorecord.prev_kms=deletedModel.odometerReading
            undorecord.prev_quantity=deletedModel.Quantity
            refuelRepository.updateRefuelRecord(undorecord)
            updateId=undorecord.id
            litres = undorecord.prev_quantity
            kmsdiff = undorecord.odometerReading - undorecord.prev_kms

        }

        var mileage: Float = 0.0f
        mileage = kmsdiff / litres
        refuelRepository.updateMileage(updateId, mileage, litres, kmsdiff)

    }
    suspend fun calculateAverageMileagebyPreviousRecord(id:Int)
    {
        val totalrefuelrecords = refuelRepository.getTotalRefuelRecords()
        var mileage :Float=0.0f
        if (totalrefuelrecords > 1) {
            val refuelRecord =refuelRepository.getRefuelRecord(id)
            val samplekmsdiff = refuelRecord.odometerReading-refuelRecord.prev_kms
            val samplequantity = refuelRecord.prev_quantity
             mileage = samplekmsdiff / samplequantity
             val insertMileage = DatabaseMileage(refuelId =refuelRecord.id ,mileage = mileage, kmsdiff = samplekmsdiff, litres = samplequantity)
             refuelRepository.insertMileage(insertMileage)
        }

    }
    suspend fun getPrevMileage() :Float
    {
        val totalrefuelrecords = refuelRepository.getTotalRefuelRecords()
        if (totalrefuelrecords > 1) {
            return refuelRepository.getPrevMileage()
        }
        else
        {
            return 0.0f
        }
    }
    suspend fun calculateAverageMileage( ) : String
    {
        Log.d("fueltotalcall","functioncalled")
        val totalmileagerecords = refuelRepository.getTotalMileageRecords()
        var avg_mileage :Float=0.0f
        val df = DecimalFormat("#.##")
        Log.d("fueltotalrecords",totalmileagerecords.toString())
        df.roundingMode = RoundingMode.CEILING
        if (totalmileagerecords > 1) {
            val totalLitresmileage =refuelRepository.getTotalLitresMileages()
            val totalKmsmileage =refuelRepository.getTotalKmsMileages()
            // val totalmileages = refuelRepository.getTotalMileages()
            avg_mileage = totalKmsmileage / totalLitresmileage

        }
        else if (totalmileagerecords == 1)
        {
            avg_mileage = refuelRepository.getPrevMileage()
        }
        else
        {
            avg_mileage=0.0f
        }
        val stravg_mileage =df.format(avg_mileage)
        return stravg_mileage
    }

    suspend fun updateMileage(databaseRefuel: DatabaseRefuel)
    {
        if (databaseRefuel.prev_kms != 0) {
            //Update mileage for current refuelId due to odometer update
            val currreckmsdiff = databaseRefuel.odometerReading - databaseRefuel.prev_kms
            val currecquantity = databaseRefuel.prev_quantity
            val currmileage = currreckmsdiff / currecquantity
            Log.d("currmileage", currmileage.toString())
            refuelRepository.updateMileage(databaseRefuel.id,
                currmileage,
                currecquantity,
                currreckmsdiff)
        }
            // Update the next record too for new prev_kms new prev_quantity
            val recordtobeupdatednext =refuelRepository.getRefuelRecord(databaseRefuel.id + 1)
            if (recordtobeupdatednext !=null) {
                val samplekmsdiff =
                    recordtobeupdatednext.odometerReading - recordtobeupdatednext.prev_kms
                val samplequantity = recordtobeupdatednext.prev_quantity
                val mileage = samplekmsdiff / samplequantity
                refuelRepository.updateMileage(databaseRefuel.id + 1,
                    mileage,
                    samplequantity,
                    samplekmsdiff)
            }

    }
    suspend fun updateRefuelRecord(databaseRefuel: DatabaseRefuel)
    {
        refuelRepository.updateRefuelRecord(databaseRefuel)
        refuelRepository.updatePrevValues(databaseRefuel.id + 1, databaseRefuel.odometerReading, databaseRefuel.Quantity)

    }
    suspend fun getRefuelRecord(id:Int) :DatabaseRefuel
    {
        return refuelRepository.getRefuelRecord(id)
    }


    suspend fun getRefuelRecords() :List<DatabaseRefuel>
    {
        return refuelRepository.getRefuelRecords()
    }
}
