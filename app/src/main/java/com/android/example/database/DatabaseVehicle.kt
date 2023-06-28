package com.android.example.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class DatabaseVehicle constructor(
    @PrimaryKey(autoGenerate = true) var  id:Int =0,
    var carname :String,
    var make :String?,
    var model:String?,
    var year:String?,
    var licenseplate:String?,
    var distanceUnit:String?, var fuelUnit : String?,
    var currencyUnit:String?, var gasType :String,
    var capacity :String?
)