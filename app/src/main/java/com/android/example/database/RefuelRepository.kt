package com.android.example.database

import android.content.Context
import android.util.Log
import androidx.room.Room
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class RefuelRepository private constructor(context: Context){
    private val refuelDatabase : RefuelDatabase = Room.databaseBuilder(
        context.applicationContext,
        RefuelDatabase::class.java,
        "databaserefuel"
    )
        .addMigrations(migration_1_2, migration_2_3)
        .build()

    private val mileageDatabase : MileageDatabase = Room.databaseBuilder(
        context.applicationContext,
        MileageDatabase::class.java,
        "databasemileage"
    ).build()

    private val vehicleDatabase : VehicleDatabase = Room.databaseBuilder(
        context.applicationContext,
        VehicleDatabase::class.java,
        "databasevehicle"
    ).build()

    suspend fun getRefuelRecord(odometerReading:Int,carname: String) : DatabaseRefuel
    {
        return refuelDatabase.refuelDao.getRefuelRecord(odometerReading, carname )
    }
    suspend fun getRefuelRecordbyPrevKms(prev_kms: Int,carname: String) : DatabaseRefuel
    {
        return refuelDatabase.refuelDao.getRefuelRecordbyPrevKms(prev_kms, carname )
    }
     suspend fun getRefuelRecords(carname: String) : List<DatabaseRefuel>
    {
        return refuelDatabase.refuelDao.getRefuelRecords(carname)
    }
     suspend fun addRefuel(databaseRefuel: DatabaseRefuel)
    {
         refuelDatabase.refuelDao.insertRefuel(databaseRefuel)
    }

    suspend fun addVehicle(databaseVehicle: DatabaseVehicle)
    {
        vehicleDatabase.vehicleDao.insertVehicle(databaseVehicle)
    }

    suspend fun getVehicles() :List<DatabaseVehicle>
    {
        return vehicleDatabase.vehicleDao.getVehicleList()
    }


    suspend fun getVehiclesName() :MutableList<String>
    {
        return vehicleDatabase.vehicleDao.getVehicleListName()
    }
    suspend fun getVehiclebyName(carname :String) :DatabaseVehicle
    {
        return vehicleDatabase.vehicleDao.getVehicleByname(carname)
    }

    suspend fun deletebyvehiclename(carname: String)
    {
        vehicleDatabase.vehicleDao.deletebyvehiclename(carname)
        refuelDatabase.refuelDao.deleterefuelsvehiclename(carname)
        mileageDatabase.mileageDao.deletemileagesvehiclename(carname)
    }

    suspend fun updateDatabasevehicle(databaseVehicle: DatabaseVehicle)
    {
        vehicleDatabase.vehicleDao.updateDatabasevehicle(databaseVehicle)
    }

    suspend fun deletebyId(id:Int,carname: String)
    {
        refuelDatabase.refuelDao.deleteDbRefuelById(id,carname)
    }

    suspend fun deletebyRefuelId(refuelId :Int,carname: String)
    {
        mileageDatabase.mileageDao.deleteById(refuelId,carname)
    }
    suspend fun updateRefuelRecord(databaseRefuel: DatabaseRefuel)
    {
        refuelDatabase.refuelDao.updateRefuelRecord(databaseRefuel)
    }

    suspend fun updatePrevValues(id:Int,prev_kms:Int,preV_quantity:Float,carname: String)
    {
        refuelDatabase.refuelDao.updatePrevValues(id,prev_kms,preV_quantity,carname)
    }

    suspend fun getId(carname: String):Int
    {
        return refuelDatabase.refuelDao.getId(carname)
    }

    suspend fun updateMileage(refuelId :Int,mileage:Float,litres:Float,kmsdiff:Int,carname: String)
    {
        mileageDatabase.mileageDao.updateMileage(refuelId,mileage,litres,kmsdiff,carname)
    }

    suspend fun getTotalRefuelRecords(carname: String) :Int
    {
        val value= refuelDatabase.refuelDao.getTotalRefuelRecords(carname)
        Log.d("I came here",value.toString())
        return value

    }
    suspend fun getPrevKms(carname: String) :Int
    {
        return refuelDatabase.refuelDao.getPrevKms(carname)
    }

    suspend fun getTotalMileageRecords(carname: String) : Int
    {
        return mileageDatabase.mileageDao.getTotalMileageRecords(carname)
    }
    suspend fun getPrevQuantity(carname: String) :Float
    {
        return refuelDatabase.refuelDao.getPrevQuantity(carname)
    }

    suspend fun getLastRecord(carname: String) :DatabaseRefuel
    {
        return refuelDatabase.refuelDao.getLastRecord(carname)
    }

    suspend fun getPrevQuantityvalue(carname: String) :Float
    {
        return refuelDatabase.refuelDao.getPrevQuantityvalue(carname)
    }

    suspend fun getPrevMileage(carname: String) :Float
    {
        return mileageDatabase.mileageDao.getPrevMileage(carname)
    }

    suspend fun getPrevKmsvalue(carname: String) :Int
    {
        return refuelDatabase.refuelDao.getPrevKmsvalue(carname)
    }

    suspend fun getTotalMileages(carname: String) :Float
    {
        return mileageDatabase.mileageDao.getTotalMileages(carname)
    }
    suspend fun getTotalKmsMileages(carname: String) :Int
    {
        return mileageDatabase.mileageDao.getTotalKmsMileages(carname)
    }
    suspend fun getTotalLitresMileages(carname: String) :Float
    {
        return mileageDatabase.mileageDao.getTotalLitresMileages(carname)
    }

    suspend fun insertMileage(mileage: DatabaseMileage)
    {
        mileageDatabase.mileageDao.insertMileage(mileage)
    }

    companion object {
        private var INSTANCE : RefuelRepository? = null
        fun initalize(context: Context)
        {
            if (INSTANCE == null)
            {
                INSTANCE = RefuelRepository(context)
            }
        }

        fun get() : RefuelRepository {
            return INSTANCE ?:
            throw IllegalStateException("Refuel Repository must be initalized")
        }
    }
}