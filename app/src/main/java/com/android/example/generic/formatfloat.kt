package com.android.example.generic

import android.widget.EditText
import java.util.*

fun formatfloat( sample:String) :Float{

    return String.format("%.2f", sample.toFloat()).toFloat()
}