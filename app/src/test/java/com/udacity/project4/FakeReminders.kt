package com.udacity.project4

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

object FakeReminders {

    fun getRemindersDto(): ReminderDataItem {
        return ReminderDataItem(
            "Reminder_one", "DESCRIPTION_ONE", "Home", 1.32324432, 1.432434
        )
    }

    fun getRemindersList(): MutableList<ReminderDTO> {
        return mutableListOf(
            ReminderDTO("Reminder_one", "DESCRIPTION_ONE", "Home", 1.32324432, 1.432434),
            ReminderDTO("Reminder_two", "DESCRIPTION_TWO", "Work", 1.656434256, 1.523555342),
            ReminderDTO("Reminder_three", "DESCRIPTION_THREE", "Club", 1.34534346, 1.656352425)
        )
    }
}
