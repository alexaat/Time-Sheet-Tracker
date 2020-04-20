package com.alexaat.timesheettracker.database


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [WorkDay::class], version = 1, exportSchema = false)
abstract class WorkDayDatabase : RoomDatabase() {

        abstract val workDayDatabaseDao: WorkDayDatabaseDao

    companion object {

        @Volatile
        private var INSTANCE: WorkDayDatabase? = null

        fun getInstance(context: Context): WorkDayDatabase {

            synchronized(this) {

                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        WorkDayDatabase::class.java,
                        "work_day_history_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
