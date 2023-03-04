package android.com.example.udacityfourthproject

import android.com.example.udacityfourthproject.locationreminders.data.dto.ReminderDTO
import android.com.example.udacityfourthproject.locationreminders.reminderslist.ReminderDataItem

object FakeReminders {

    fun getRemindersDto(): ReminderDTO {
        return ReminderDTO(
            "Fake_Reminder", "FAKE_DESCRIPTION", "FAKE_LOCATION", 1.32324432, 1.432434
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
