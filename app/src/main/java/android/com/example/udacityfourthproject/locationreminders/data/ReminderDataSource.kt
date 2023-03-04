package android.com.example.udacityfourthproject.locationreminders.data

import android.com.example.udacityfourthproject.locationreminders.data.dto.ReminderDTO
import android.com.example.udacityfourthproject.locationreminders.data.dto.Result

/**
 * Main entry point for accessing reminders data.
 */
interface ReminderDataSource {
    suspend fun getReminders(): Result<List<ReminderDTO>>
    suspend fun saveReminder(reminder: ReminderDTO)
    suspend fun getReminder(id: String): Result<ReminderDTO>
    suspend fun deleteAllReminders()
    suspend fun refreshReminders()
}