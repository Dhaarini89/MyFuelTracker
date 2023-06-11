package com.android.example

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.android.example.database.RefuelRepository
import com.android.example.mymileagetracker.RefuelViewModel

class MileageApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        RefuelRepository.initalize(this)

    }
}