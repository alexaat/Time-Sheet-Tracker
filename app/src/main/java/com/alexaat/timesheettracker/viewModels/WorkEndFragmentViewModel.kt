package com.alexaat.timesheettracker.viewModels



import androidx.lifecycle.*
import com.alexaat.timesheettracker.database.WorkDay
import com.alexaat.timesheettracker.database.WorkDayDatabaseDao
import com.alexaat.timesheettracker.format
import kotlinx.coroutines.*



class WorkEndFragmentViewModel(private val id:Long, private val database: WorkDayDatabaseDao): ViewModel() {


    val BUTTON_ENABLED:Float = 1.0F
    val BUTTON_DISABLED:Float = 0.3F


    var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private lateinit var today: WorkDay

    // encapsulated clockIn date string
    private var _clockIn = MutableLiveData<String>()
    val clockIn: LiveData<String>
        get() = _clockIn

    // encapsulated clockOut date string
    private var _clockOut = MutableLiveData<String>()
    val clockOut: LiveData<String>
        get() = _clockOut


    // encapsulated navigation property
    private var _navigateToListFragment = MutableLiveData<Boolean>()
    val navigateToListFragment:LiveData<Boolean>
        get() =  _navigateToListFragment



    // encapsulated enable property of Save button
    private var _isSaveButtonEnabled = MutableLiveData<Boolean>()
    val isSaveButtonEnabled:LiveData<Boolean>
        get() = _isSaveButtonEnabled


    // encapsulated enabled property of Save buton opacity
    private var _saveButtonAlpha = MutableLiveData<Float>()
    val saveButtonAlpha:LiveData<Float>
        get() = _saveButtonAlpha

    // encapsulated show clockOut not saved snackBar event
    private var _showClockOutNotSavedSnackBar = MutableLiveData<Boolean>(false)
    val showClockOutNotSavedSnackBar:LiveData<Boolean>
        get() = _showClockOutNotSavedSnackBar

    // encapsulated show clockOut saved snackBar event
    private var _showClockOutSavedSnackBar = MutableLiveData<Boolean>(false)
    val showClockOutSavedSnackBar:LiveData<Boolean>
        get() = _showClockOutSavedSnackBar

    init {
        _isSaveButtonEnabled.value = false
        _saveButtonAlpha.value = BUTTON_DISABLED
        _navigateToListFragment.value = false
        uiScope.launch{
            get(id)
            _clockIn.value = format(today.clockIn)
            _clockOut.value = format(today.clockOut)
            _isSaveButtonEnabled.value = true
            _saveButtonAlpha.value = BUTTON_ENABLED
        }
    }

    fun onSaveButtonPressed(description:String){
        today.description = description
        uiScope.launch{
            update(today)
            _showClockOutSavedSnackBar.value = true
            _navigateToListFragment.value = true
        }
    }


    fun doneNavigating(){
        _navigateToListFragment.value = false
    }

    fun cancelClockOut(){
        _showClockOutNotSavedSnackBar.value = true
        uiScope.launch {
            cancelLastClockOut()

        }
    }

    fun showClockOutNotSavedSnackBarComplete(){
        _showClockOutNotSavedSnackBar.value = false
    }

    fun showClockOutSavedSnackBarComplete(){
        _showClockOutSavedSnackBar.value = false
    }


    private suspend fun get(id:Long)= withContext(Dispatchers.IO){
         today = database.get(id)
    }

    private suspend fun update(workDay: WorkDay){
        withContext(Dispatchers.IO){
            database.update(workDay)
        }
    }

    private suspend fun cancelLastClockOut(){
        withContext(Dispatchers.IO){
           database.cancelLastClockOut()
        }
    }


}

