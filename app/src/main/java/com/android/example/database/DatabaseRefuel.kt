package com.android.example.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class DatabaseRefuel constructor(
    @PrimaryKey(autoGenerate = true) val  id:Int =0,
    val odometerReading :Int,
    val gasType:String,
    val Quantity:Float,
    val totalCost:Float,
    val price:Float, val dateOfRefuel :Date,
    var prev_kms:Int, var prev_quantity :Float
    )


