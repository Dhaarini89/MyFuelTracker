package com.android.example.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface refuelDao {

    @Update
    suspend fun updateRefuelRecord(databaseRefuel: DatabaseRefuel)

    @Query("SELECT * FROM databaserefuel where odometerReading=:odometerReading and carname=:carname")
    suspend fun getRefuelRecord(odometerReading:Int,carname: String):DatabaseRefuel

    @Query("SELECT * FROM databaserefuel where prev_kms=:prev_kms and carname=:carname")
    suspend fun getRefuelRecordbyPrevKms(prev_kms: Int,carname: String):DatabaseRefuel

    @Query("select count(*) from databaserefuel where carname=:carname")
    suspend fun getTotalRefuelRecords(carname: String):Int

    @Query("select * from databaserefuel where carname=:carname")
    suspend fun getRefuelRecords(carname: String) : List<DatabaseRefuel>

    @Query("select odometerReading from databaserefuel where carname= :carname ORDER by odometerReading DESC LIMIT 1 ")
    suspend fun getPrevKms(carname: String) : Int

    @Query("select * from databaserefuel where carname= :carname ORDER by odometerReading DESC LIMIT 1 ")
    suspend fun getLastRecord(carname: String) : DatabaseRefuel

    @Query("select Quantity from databaserefuel where carname= :carname ORDER by odometerReading DESC LIMIT 1 ")
    suspend fun getPrevQuantity(carname: String) : Float

    @Query("select prev_kms from databaserefuel where carname= :carname ORDER by odometerReading DESC LIMIT 1 ")
    suspend fun getPrevKmsvalue(carname: String) : Int

    @Query("select id from databaserefuel where carname= :carname ORDER by odometerReading DESC LIMIT 1 ")
    suspend fun getId(carname: String) : Int

    @Query("UPDATE databaserefuel SET prev_kms = :prev_kms, prev_quantity = :prev_quantity WHERE id = :id and carname= :carname")
    suspend fun updatePrevValues(id: Int, prev_kms: Int, prev_quantity: Float,carname: String)

    @Query("select prev_quantity from databaserefuel where carname= :carname ORDER by odometerReading DESC LIMIT 1 ")
    suspend fun getPrevQuantityvalue(carname: String) : Float

    @Query("DELETE FROM databaserefuel WHERE id = :id and carname = :carname")
    suspend fun deleteDbRefuelById(id: Int,carname: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRefuel(refuels: DatabaseRefuel)


    @Query("delete from databaserefuel where carname= :carname")
    suspend fun deleterefuelsvehiclename(carname: String)
}
@Dao
interface mileageDao {

    @Insert(onConflict =OnConflictStrategy.REPLACE)
    suspend fun insertMileage(mileage: DatabaseMileage)

    @Query("select mileage from databasemileage where carname=:carname ORDER by refuelId DESC LIMIT 1 ")
    suspend fun getPrevMileage(carname: String) : Float

    @Query("select sum(mileage) from databasemileage where carname=:carname")
    suspend fun getTotalMileages(carname: String) :Float

    @Query("select sum(kmsdiff) from databasemileage where carname=:carname")
    suspend fun getTotalKmsMileages(carname: String) :Int

    @Query("select sum(litres) from databasemileage where carname=:carname")
    suspend fun getTotalLitresMileages(carname: String) :Float

    @Query("select count(*) from databasemileage where carname=:carname")
    suspend fun getTotalMileageRecords(carname: String):Int


    @Query("UPDATE databasemileage SET mileage = :mileage,litres = :litres,kmsdiff = :kmsdiff WHERE refuelId = :refuelId and carname=:carname")
    suspend fun updateMileage(refuelId: Int,mileage: Float,litres:Float,kmsdiff:Int,carname: String)

    @Query("DELETE FROM databasemileage WHERE refuelId = :refuelId and carname=:carname")
    suspend fun deleteById(refuelId: Int,carname: String)

    @Query("delete from databasemileage where carname= :carname")
    suspend fun deletemileagesvehiclename(carname: String)


}
@Dao
interface VehicleDao
{
    @Insert(onConflict =OnConflictStrategy.REPLACE)
    suspend fun insertVehicle(vehicle: DatabaseVehicle)

    @Query("select * from databasevehicle")
    suspend fun getVehicleList() : List<DatabaseVehicle>

    @Query("select carname from databasevehicle")
    suspend fun getVehicleListName() : MutableList<String>

    @Update
    suspend fun updateDatabasevehicle(databaseVehicle: DatabaseVehicle)


    @Query("select * from databasevehicle where carname= :carname")
    suspend fun getVehicleByname(carname :String) :DatabaseVehicle

    @Query("delete from databasevehicle where carname= :carname")
    suspend fun deletebyvehiclename(carname: String)





}
@Database(entities = [DatabaseVehicle::class], version = 1, exportSchema = false )
abstract class VehicleDatabase :RoomDatabase() {
    abstract val vehicleDao :VehicleDao
}
@Database(entities = [DatabaseRefuel::class], version = 1, exportSchema = false)
@TypeConverters(RefueltypeConverter::class)
abstract class RefuelDatabase : RoomDatabase() {
    abstract val refuelDao : refuelDao
}

@Database(entities = [DatabaseMileage::class], version = 1, exportSchema = false)
abstract class MileageDatabase :RoomDatabase() {
    abstract val mileageDao : mileageDao
}

val migration_1_2 = object : Migration(1,2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE databasemileage ADD COLUMN prev_kms TEXT")
    }
}
val migration_2_3 = object : Migration(2,3)
{
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE databasemileage ADD COLUMN prev_quantity REAl(2)" )
    }
}