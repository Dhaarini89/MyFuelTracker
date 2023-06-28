package com.android.example.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class DatabaseMileage constructor(
    @PrimaryKey(autoGenerate = true)
    val id :Int =0,
    val refuelId :Int,
    val mileage :Float,
    val kmsdiff :Int,
    val litres :Float,
    var carname :String
)
