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

    @Query("SELECT * FROM databaserefuel where id =(:id)")
    suspend fun getRefuelRecord(id: Int):DatabaseRefuel

    @Query("select count(*) from databaserefuel")
    suspend fun getTotalRefuelRecords():Int

    @Query("select * from databaserefuel")
    suspend fun getRefuelRecords() : List<DatabaseRefuel>

    @Query("select odometerReading from databaserefuel ORDER by id DESC LIMIT 1 ")
    suspend fun getPrevKms() : Int

    @Query("select * from databaserefuel ORDER by id DESC LIMIT 1 ")
    suspend fun getLastRecord() : DatabaseRefuel

    @Query("select Quantity from databaserefuel ORDER by id DESC LIMIT 1 ")
    suspend fun getPrevQuantity() : Float

    @Query("select prev_kms from databaserefuel ORDER by id DESC LIMIT 1 ")
    suspend fun getPrevKmsvalue() : Int

    @Query("select id from databaserefuel ORDER by id DESC LIMIT 1 ")
    suspend fun getId() : Int

    @Query("UPDATE databaserefuel SET prev_kms = :prev_kms, prev_quantity = :prev_quantity WHERE id = :id")
    suspend fun updatePrevValues(id: Int, prev_kms: Int, prev_quantity: Float)

    @Query("select prev_quantity from databaserefuel ORDER by id DESC LIMIT 1 ")
    suspend fun getPrevQuantityvalue() : Float

    @Query("DELETE FROM databaserefuel WHERE id = :id")
    suspend fun deleteDbRefuelById(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRefuel(refuels: DatabaseRefuel)
}
@Dao
interface mileageDao {

    @Insert(onConflict =OnConflictStrategy.REPLACE)
    suspend fun insertMileage(mileage: DatabaseMileage)

    @Query("select mileage from databasemileage ORDER by id DESC LIMIT 1 ")
    suspend fun getPrevMileage() : Float

    @Query("select sum(mileage) from databasemileage")
    suspend fun getTotalMileages() :Float

    @Query("select sum(kmsdiff) from databasemileage")
    suspend fun getTotalKmsMileages() :Int

    @Query("select sum(litres) from databasemileage")
    suspend fun getTotalLitresMileages() :Float

    @Query("select count(*) from databasemileage")
    suspend fun getTotalMileageRecords():Int

    @Query("UPDATE databasemileage SET mileage = :mileage,litres = :litres,kmsdiff = :kmsdiff WHERE refuelId = :refuelId")
    suspend fun updateMileage(refuelId: Int,mileage: Float,litres:Float,kmsdiff:Int)


    @Query("DELETE FROM databasemileage WHERE refuelId = :refuelId")
    suspend fun deleteById(refuelId: Int)

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