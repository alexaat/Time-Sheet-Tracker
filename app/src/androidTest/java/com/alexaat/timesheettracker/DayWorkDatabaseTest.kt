package com.alexaat.timesheettracker

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.alexaat.timesheettracker.database.WorkDay
import com.alexaat.timesheettracker.database.WorkDayDatabase
import com.alexaat.timesheettracker.database.WorkDayDatabaseDao
import org.junit.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


@RunWith(AndroidJUnit4::class)
class DayWorkDatabaseTest {

    private lateinit var workDayDao: WorkDayDatabaseDao
    private lateinit var db: WorkDayDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        db = Room.inMemoryDatabaseBuilder(context, WorkDayDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        workDayDao = db.workDayDatabaseDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAllWorkDays() {
        val workDay = WorkDay()
        workDayDao.insert(workDay)
        val today = workDayDao.getToday()
        assertEquals(today?.description, "nothing")
    }
}