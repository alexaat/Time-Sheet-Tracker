package com.alexaat.timesheettracker.database


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface WorkDayDatabaseDao{

    @Insert
    fun insert(workDay:WorkDay)

    @Update
    fun update(workDay:WorkDay)

    @Query("DELETE from work_day_table")
    fun clear()

    @Query("SELECT * from work_day_table WHERE id = :key")
    fun get(key:Long):WorkDay

    @Query("SELECT * from work_day_table ORDER BY id DESC")
    fun getAllWorkDays(): LiveData<List<WorkDay>>

    @Query("SELECT * from work_day_table ORDER BY id DESC LIMIT 1")
    fun getToday():WorkDay?

    @Query("UPDATE work_day_table SET end_time_milli = start_time_milli WHERE id = (SELECT id FROM work_day_table ORDER BY id DESC LIMIT 1)")
    fun cancelLastClockOut()



}