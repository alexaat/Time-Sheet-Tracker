package com.alexaat.timesheettracker.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "work_day_table")
data class WorkDay(

    @PrimaryKey(autoGenerate = true)
    var id:Long = 0L,

    @ColumnInfo(name = "start_time_milli")
    var clockIn:Long = 0L,

    @ColumnInfo(name = "end_time_milli")
    var clockOut:Long = clockIn,

    @ColumnInfo(name = "description")
    var description:String = ""

)