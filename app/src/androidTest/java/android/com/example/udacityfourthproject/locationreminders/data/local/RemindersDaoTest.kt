package android.com.example.udacityfourthproject.locationreminders.data.local

import android.com.example.udacityfourthproject.locationreminders.data.dto.ReminderDTO
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {
    private lateinit var database: RemindersDatabase

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()

    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun saveReminder_getReminderById() = runBlockingTest {
        // GIVEN - Save a reminder.
        val reminder = ReminderDTO("Reminder_one", "DESCRIPTION", "Home", 1.32324432, 1.432434)
        database.reminderDao().saveReminder(reminder)

        // WHEN - Get the reminder by ID from the database.
        val loadReminder = database.reminderDao().getReminderById(reminder.id)

        // THEN - The loaded data contains the expected values.
        assertThat(loadReminder as ReminderDTO, notNullValue())
        assertThat(loadReminder.title, `is`(reminder.title))
        assertThat(loadReminder.description, `is`(reminder.description))
        assertThat(loadReminder.location, `is`(reminder.location))
        assertThat(loadReminder.latitude, `is`(reminder.latitude))
        assertThat(loadReminder.longitude, `is`(reminder.longitude))
    }

    @Test
    fun removeAllReminder() = runBlockingTest {
        database.reminderDao().deleteAllReminders()
    }

    @Test
    fun getAllReminders_returnResult() = runBlockingTest {
        // GIVEN - Save 1 reminder.
        val moviesList = mutableListOf<ReminderDTO>()
        val reminder = ReminderDTO("Reminder_one", "DESCRIPTION", "Home", 1.32324432, 1.432434)
        moviesList.add(reminder)
        database.reminderDao().saveReminder(reminder)


        // WHEN - Get all saved reminder.
        val result = database.reminderDao().getReminders()

        // THEN - Return all saved reminder as they were.
        assertThat(result.size, `is`(moviesList.size))
    }

}