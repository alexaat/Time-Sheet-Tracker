package com.alexaat.timesheettracker.viewModels


import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.alexaat.timesheettracker.R
import com.alexaat.timesheettracker.database.WorkDay
import com.alexaat.timesheettracker.database.WorkDayDatabaseDao
import com.alexaat.timesheettracker.formatWorkDays
import kotlinx.coroutines.*


class ListFragmentViewModel(val activity: Activity, val database: WorkDayDatabaseDao) :
    ViewModel() {

    val BUTTON_ENABLED: Float = 1.0F
    val BUTTON_DISABLED: Float = 0.3F


    private var today: WorkDay? = null
    private var workDayList = database.getAllWorkDays()
    val workDaysString = Transformations.map(workDayList) {
        formatWorkDays(it, this.activity.resources)
    }


    var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    // encapsulated enabled property of ClockIn button
    private var _isClockInEnabled = MutableLiveData<Boolean>()
    val isClockInEnabled: LiveData<Boolean>
        get() = _isClockInEnabled


    // encapsulated enabled property of ClockOut button
    private var _isClockOutEnabled = MutableLiveData<Boolean>()
    val isClockOutEnabled: LiveData<Boolean>
        get() = _isClockOutEnabled


    // encapsulated enabled property of ClockIn opacity
    private var _clockInAlpha = MutableLiveData<Float>()
    val clockInAlpha: LiveData<Float>
        get() = _clockInAlpha


    // encapsulated enabled property of ClockOut opacity
    private var _clockOutAlpha = MutableLiveData<Float>()
    val clockOutAlpha: LiveData<Float>
        get() = _clockOutAlpha


    // encapsulated navigation property
    private var _navigateToWorkEndFragment = MutableLiveData<Long>()
    val navigateToWorkEndFragment: LiveData<Long>
        get() = _navigateToWorkEndFragment

    // encapsulated show snack bar event
    private var _showSnackBar = MutableLiveData<String>("")
    val showSnackBar: LiveData<String>
        get() = _showSnackBar

    // encapsulated isTrackingOn
    private var _isTrackingOn = MutableLiveData<Boolean>(false)
    val isTrackingOn: LiveData<Boolean>
        get() = _isTrackingOn

    fun snackBarIsDisplayed(){
        _showSnackBar.value = ""
    }


    fun onResume(){
        _navigateToWorkEndFragment.value = -1
        _isTrackingOn.value = true
        disableButtons()
        uiScope.launch {
            getToday()
            setButtons()
        }
    }



    private fun disableButtons() {
        _isClockInEnabled.value = false
        _clockInAlpha.value = BUTTON_DISABLED
        _isClockOutEnabled.value = false
        _clockOutAlpha.value = BUTTON_DISABLED
    }

    fun onClockInClick() {
        _isTrackingOn.value = true
        _isClockInEnabled.value = false
        _clockInAlpha.value = BUTTON_DISABLED
        today = WorkDay(clockIn = System.currentTimeMillis())

        uiScope.launch {
            insert(today!!)
            getToday()
            _isClockOutEnabled.value = true
            _clockOutAlpha.value = BUTTON_ENABLED
        }

    }

    fun onClockOutClick() {
        disableButtons()
        today?.clockOut = System.currentTimeMillis()
        uiScope.launch {
            update(today!!)
            _navigateToWorkEndFragment.value = today?.id
            //_isTrackingOn.value = false
            //_isClockInEnabled.value = true
            //_clockInAlpha.value = BUTTON_ENABLED
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun deleteAllFromDb() {
        if (!isTrackingOn.value!!) {
            disableButtons()
            uiScope.launch {
                deleteAll()
                setButtons()
                _showSnackBar.value = activity.resources.getString(R.string.all_data_is_deleted)
            }
        }else{
            _showSnackBar.value = activity.resources.getString(R.string.cannot_delete)
        }
    }

    fun navigationDone() {
        _navigateToWorkEndFragment.value = -1
    }


    /////////// suspend functions///////////////////////////
    private suspend fun insert(workDay: WorkDay) = withContext(Dispatchers.IO) {
        database.insert(workDay)
    }

    private suspend fun deleteAll() {
        withContext(Dispatchers.IO) {
            database.clear()
        }

    }

    private suspend fun getToday() {
        withContext(Dispatchers.IO) {
            today = database.getToday()
        }
    }

    private suspend fun setButtons() = withContext(Dispatchers.Main) {



        if (today == null) {
            _isTrackingOn.value = false
            _isClockInEnabled.value = true
            _clockInAlpha.value = BUTTON_ENABLED
            _isClockOutEnabled.value = false
            _clockOutAlpha.value = BUTTON_DISABLED

        } else if (today!!.clockIn == today!!.clockOut) {
            _isTrackingOn.value = true
            _isClockInEnabled.value = false
            _clockInAlpha.value = BUTTON_DISABLED
            _isClockOutEnabled.value = true
            _clockOutAlpha.value = BUTTON_ENABLED

        } else {
            _isTrackingOn.value = false
            _isClockInEnabled.value = true
            _clockInAlpha.value = BUTTON_ENABLED
            _isClockOutEnabled.value = false
            _clockOutAlpha.value = BUTTON_DISABLED

        }

    }

    private suspend fun update(workDay: WorkDay) {
        withContext(Dispatchers.IO) {
            database.update(workDay)
        }
    }

}
