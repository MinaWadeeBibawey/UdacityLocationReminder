package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result


//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(private val remindersList: MutableList<ReminderDTO>) : ReminderDataSource {
    private var shouldReturnError = false

    fun setReturnError(shouldReturnError: Boolean) {
        this.shouldReturnError = shouldReturnError
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        return if (shouldReturnError) {
            Result.Error("failed to get Reminders")
        } else {
            Result.Success(remindersList)
        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        remindersList.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        return if (shouldReturnError) {
            Result.Error("failed to get reminder")
        } else {
            val reminder = remindersList.find {
                it.id == id
            }
            if (reminder != null) {
                Result.Success(reminder)
            } else {
                Result.Error("Reminder not found")
            }
        }
    }


    override suspend fun deleteAllReminders() {
        remindersList.clear()
    }

    override suspend fun refreshReminders() {
        TODO("Not yet implemented")
    }
}