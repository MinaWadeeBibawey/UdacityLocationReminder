package android.com.example.udacityfourthproject.locationreminders.reminderslist

import android.app.Application
import android.com.example.udacityfourthproject.R
import android.com.example.udacityfourthproject.base.BaseViewModel
import android.com.example.udacityfourthproject.locationreminders.data.ReminderDataSource
import android.com.example.udacityfourthproject.locationreminders.data.dto.ReminderDTO
import android.com.example.udacityfourthproject.locationreminders.data.dto.Result
import android.com.example.udacityfourthproject.utils.Event

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class RemindersListViewModel(
    private val dataSource: ReminderDataSource
) : BaseViewModel() {
    // list that holds the reminder data to be displayed on the UI
    val remindersList = MutableLiveData<List<ReminderDataItem>>()

    /**
     * Get all the reminders from the DataSource and add them to the remindersList to be shown on the UI,
     * or show error if any
     */
    fun loadReminders() {
        showLoading.value = true
        viewModelScope.launch {
            //interacting with the dataSource has to be through a coroutine
            val result = dataSource.getReminders()
            showLoading.postValue(false)
            when (result) {
                is Result.Success<*> -> {
                    val dataList = ArrayList<ReminderDataItem>()
                    dataList.addAll((result.data as List<ReminderDTO>).map { reminder ->
                        //map the reminder data from the DB to the be ready to be displayed on the UI
                        ReminderDataItem(
                            reminder.title,
                            reminder.description,
                            reminder.location,
                            reminder.latitude,
                            reminder.longitude,
                            reminder.id
                        )
                    })
                    remindersList.value = dataList
                }
                is Result.Error ->
                    showSnackBar.value = result.message
            }

            //check if no data has to be shown
            invalidateShowNoData()
        }
    }

    /**
     * Inform the user that there's not any data if the remindersList is empty
     */
    private fun invalidateShowNoData() {
        showNoData.value = remindersList.value == null || remindersList.value!!.isEmpty()
    }
}