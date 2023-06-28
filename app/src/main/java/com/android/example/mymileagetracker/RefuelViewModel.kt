package com.android.example.mymileagetracker

import android.icu.text.AlphabeticIndex
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.room.PrimaryKey
import com.android.example.database.DatabaseMileage
import com.android.example.database.DatabaseRefuel
import com.android.example.database.DatabaseVehicle
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
    suspend fun getTotalRefuelRecords(carname: String) : Int
    {
        return refuelRepository.getTotalRefuelRecords(carname)
    }

    suspend fun insertVehicles(databaseVehicle: DatabaseVehicle)
    {
        return refuelRepository.addVehicle(databaseVehicle)
    }
    suspend fun getVehiclesList():List<DatabaseVehicle>
    {
        return refuelRepository.getVehicles()
    }
    suspend fun getVehiclesListName():MutableList<String>
    {
        return refuelRepository.getVehiclesName()
    }
    suspend fun updateDatabasevehicle(databaseVehicle: DatabaseVehicle)
    {
        refuelRepository.updateDatabasevehicle(databaseVehicle)
    }
    suspend fun deletebyvehiclename(carname: String)
    {
        refuelRepository.deletebyvehiclename(carname)
    }

    suspend fun getVehicleByName(carname :String):DatabaseVehicle
    {
        return refuelRepository.getVehiclebyName(carname)
    }
    suspend fun getLastRecord(carname: String) : DatabaseRefuel
    {
        if (refuelRepository.getTotalRefuelRecords(carname) >= 1) {
            return refuelRepository.getLastRecord(carname)
        }
        else
        {
            var databaseRefuel =DatabaseRefuel(0,0,"",0.0f,0.0f,0.0f,Calendar.getInstance().time,0,0.0f,carname)
            return databaseRefuel
        }

    }
    suspend fun getPrevKms(carname: String) : Int
    {
        if (refuelRepository.getTotalRefuelRecords(carname) >= 1) {
            return refuelRepository.getPrevKms(carname)
        }
        else
            return 0
    }
    suspend fun getPrevQuantity(carname: String) : Float
    {
        if (refuelRepository.getTotalRefuelRecords(carname) >= 1) {
            return refuelRepository.getPrevQuantity(carname)        }
        else
            return 0.0f

    }
    suspend fun deleteDbRefuel(id:Int,carname: String)
    {
        return refuelRepository.deletebyId(id,carname)
    }

    suspend fun deleteDbMileage(position :Int,refuel:DatabaseRefuel,carname: String)
    {
        var i_refuelId =refuel.id
        if (position == 0 )
        {
            val record =refuelRepository.getRefuelRecordbyPrevKms(refuel.odometerReading,carname)
            if (record !=null) {
                i_refuelId = record.id
            }
            Log.d("record",i_refuelId.toString())
        }
        return refuelRepository.deletebyRefuelId(i_refuelId,carname)
    }

    suspend fun recalculateAverageMileagebyPrevRecord(deletedstatus:Int,deletedModel : DatabaseRefuel,carname: String)
    {   var updateId =0
        var litres =0.0f
        var kmsdiff =0
        Log.d("AvgDeletedStatsView",deletedstatus.toString())
        if (deletedstatus ==1) {
            val Record = refuelRepository.getRefuelRecordbyPrevKms(deletedModel.odometerReading,carname)
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
            val undorecord = refuelRepository.getRefuelRecordbyPrevKms(deletedModel.prev_kms,carname)
            Log.d("Deltedmodel",undorecord.id.toString())
            undorecord.prev_kms=deletedModel.odometerReading
            undorecord.prev_quantity=deletedModel.Quantity
            refuelRepository.updateRefuelRecord(undorecord)
            updateId=undorecord.id
            litres = undorecord.prev_quantity
            kmsdiff = undorecord.odometerReading - undorecord.prev_kms
            addRefuel(deletedModel)
            calculateAverageMileagebyPreviousRecord(deletedModel.odometerReading, carname)


        }

        var mileage: Float = 0.0f
         mileage = kmsdiff / litres
         refuelRepository.updateMileage(updateId, mileage, litres, kmsdiff,carname)

    }
    suspend fun calculateAverageMileagebyPreviousRecord(odometerReading: Int,carname: String)
    {
        val totalrefuelrecords = refuelRepository.getTotalRefuelRecords(carname)
        var mileage :Float=0.0f
        if (totalrefuelrecords > 1) {
            val refuelRecord =refuelRepository.getRefuelRecord(odometerReading,carname)
            val samplekmsdiff = refuelRecord.odometerReading-refuelRecord.prev_kms
            val samplequantity = refuelRecord.prev_quantity
             mileage = samplekmsdiff / samplequantity
             val insertMileage = DatabaseMileage(carname = carname, refuelId =refuelRecord.id ,mileage = mileage, kmsdiff = samplekmsdiff, litres = samplequantity)
             refuelRepository.insertMileage(insertMileage)
        }
    }
    suspend fun getPrevMileage(carname: String) :Float
    {
        val totalrefuelrecords = refuelRepository.getTotalRefuelRecords(carname)
        if (totalrefuelrecords > 1) {
            return refuelRepository.getPrevMileage(carname)
        }
        else
        {
            return 0.0f
        }
    }
    suspend fun calculateAverageMileage( carname: String) : String
    {
        Log.d("fueltotalcall","functioncalled")
        val totalmileagerecords = refuelRepository.getTotalMileageRecords(carname)
        var avg_mileage :Float=0.0f
        val df = DecimalFormat("#.##")
        Log.d("fueltotalrecords",totalmileagerecords.toString())
        df.roundingMode = RoundingMode.CEILING
        if (totalmileagerecords > 1) {
            val totalLitresmileage =refuelRepository.getTotalLitresMileages(carname)
            val totalKmsmileage =refuelRepository.getTotalKmsMileages(carname)
            avg_mileage = totalKmsmileage / totalLitresmileage

        }
        else if (totalmileagerecords == 1)
        {
            avg_mileage = refuelRepository.getPrevMileage(carname)
        }
        else
        {
            avg_mileage=0.0f
        }
        val stravg_mileage =df.format(avg_mileage)
        return stravg_mileage
    }

    suspend fun updateMileage(databaseRefuel: DatabaseRefuel,carname: String)
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
                currreckmsdiff, carname)
        }
            // Update the next record too for new prev_kms new prev_quantity
            val recordtobeupdatednext =refuelRepository.getRefuelRecordbyPrevKms(databaseRefuel.odometerReading,carname)
            if (recordtobeupdatednext !=null) {
                val samplekmsdiff =
                    recordtobeupdatednext.odometerReading - recordtobeupdatednext.prev_kms
                val samplequantity = recordtobeupdatednext.prev_quantity
                val mileage = samplekmsdiff / samplequantity
                refuelRepository.updateMileage(recordtobeupdatednext.id,
                    mileage,
                    samplequantity,
                    samplekmsdiff,carname)
            }

    }
    suspend fun updateRefuelRecord(databaseRefuel: DatabaseRefuel,carname: String)
    {
        refuelRepository.updateRefuelRecord(databaseRefuel)
        refuelRepository.updatePrevValues(databaseRefuel.id + 1, databaseRefuel.odometerReading, databaseRefuel.Quantity,carname)

    }
    suspend fun getRefuelRecord(odometerReading: Int,carname: String) :DatabaseRefuel
    {
        return refuelRepository.getRefuelRecord(odometerReading,carname)
    }


    suspend fun getRefuelRecords(carname: String) :List<DatabaseRefuel>
    {
        return refuelRepository.getRefuelRecords(carname)
    }
}
