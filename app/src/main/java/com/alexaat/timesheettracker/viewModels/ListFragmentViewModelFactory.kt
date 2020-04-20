package com.alexaat.timesheettracker.viewModels


import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alexaat.timesheettracker.database.WorkDayDatabaseDao

class ListFragmentViewModelFactory(private val activity: Activity, private val database: WorkDayDatabaseDao): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListFragmentViewModel::class.java)) {
            return ListFragmentViewModel(activity = activity, database = database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}