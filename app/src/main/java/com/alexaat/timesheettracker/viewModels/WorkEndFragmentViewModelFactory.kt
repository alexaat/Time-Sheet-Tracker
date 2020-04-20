package com.alexaat.timesheettracker.viewModels

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alexaat.timesheettracker.database.WorkDayDatabaseDao

class WorkEndFragmentViewModelFactory(private val id: Long, private val db: WorkDayDatabaseDao): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkEndFragmentViewModel::class.java)) {
            return WorkEndFragmentViewModel(id, db) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}