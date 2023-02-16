package android.com.example.udacityfourthproject.locationreminders.reminderslist

import android.com.example.udacityfourthproject.R
import android.com.example.udacityfourthproject.base.BaseRecyclerViewAdapter


//Use data binding to show the reminder on the item
class RemindersListAdapter(callBack: (selectedReminder: ReminderDataItem) -> Unit) :
    BaseRecyclerViewAdapter<ReminderDataItem>(callBack) {
    override fun getLayoutRes(viewType: Int) = R.layout.it_reminder
}