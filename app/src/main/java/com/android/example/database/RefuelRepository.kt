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

    suspend fun getRefuelRecord(id:Int) : DatabaseRefuel
    {
        return refuelDatabase.refuelDao.getRefuelRecord(id)
    }
     suspend fun getRefuelRecords() : List<DatabaseRefuel>
    {
        return refuelDatabase.refuelDao.getRefuelRecords()
    }
     suspend fun addRefuel(databaseRefuel: DatabaseRefuel)
    {
         refuelDatabase.refuelDao.insertRefuel(databaseRefuel)
    }

    suspend fun deletebyId(id:Int)
    {
        refuelDatabase.refuelDao.deleteDbRefuelById(id)
    }

    suspend fun deletebyRefuelId(refuelId :Int)
    {
        mileageDatabase.mileageDao.deleteById(refuelId)
    }
    suspend fun updateRefuelRecord(databaseRefuel: DatabaseRefuel)
    {
        refuelDatabase.refuelDao.updateRefuelRecord(databaseRefuel)
    }

    suspend fun updatePrevValues(id:Int,prev_kms:Int,preV_quantity:Float)
    {
        refuelDatabase.refuelDao.updatePrevValues(id,prev_kms,preV_quantity)
    }

    suspend fun getId():Int
    {
        return refuelDatabase.refuelDao.getId()
    }

    suspend fun updateMileage(refuelId :Int,mileage:Float,litres:Float,kmsdiff:Int)
    {
        mileageDatabase.mileageDao.updateMileage(refuelId,mileage,litres,kmsdiff)
    }

    suspend fun getTotalRefuelRecords() :Int
    {
        val value= refuelDatabase.refuelDao.getTotalRefuelRecords()
        Log.d("I came here",value.toString())
        return value

    }
    suspend fun getPrevKms() :Int
    {
        return refuelDatabase.refuelDao.getPrevKms()
    }

    suspend fun getTotalMileageRecords() : Int
    {
        return mileageDatabase.mileageDao.getTotalMileageRecords()
    }
    suspend fun getPrevQuantity() :Float
    {
        return refuelDatabase.refuelDao.getPrevQuantity()
    }

    suspend fun getLastRecord() :DatabaseRefuel
    {
        return refuelDatabase.refuelDao.getLastRecord()
    }

    suspend fun getPrevMileage() :Float
    {
        return mileageDatabase.mileageDao.getPrevMileage()
    }

    suspend fun getPrevKmsvalue() :Int
    {
        return refuelDatabase.refuelDao.getPrevKmsvalue()
    }

    suspend fun getTotalMileages() :Float
    {
        return mileageDatabase.mileageDao.getTotalMileages()
    }
    suspend fun getTotalKmsMileages() :Int
    {
        return mileageDatabase.mileageDao.getTotalKmsMileages()
    }
    suspend fun getTotalLitresMileages() :Float
    {
        return mileageDatabase.mileageDao.getTotalLitresMileages()
    }
    suspend fun getPrevQuantityvalue() :Float
    {
        return refuelDatabase.refuelDao.getPrevQuantityvalue()
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