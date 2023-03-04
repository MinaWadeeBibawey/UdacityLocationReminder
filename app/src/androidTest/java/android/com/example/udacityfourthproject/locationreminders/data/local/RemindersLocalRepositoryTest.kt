package android.com.example.udacityfourthproject.locationreminders.data.local

import android.com.example.udacityfourthproject.locationreminders.data.dto.ReminderDTO
import android.com.example.udacityfourthproject.locationreminders.data.dto.succeeded
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import android.com.example.udacityfourthproject.locationreminders.data.dto.Result.Success
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    private lateinit var localDataSource: RemindersLocalRepository
    private lateinit var database: RemindersDatabase

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        // Using an in-memory database for testing, because it doesn't survive killing the process.
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        localDataSource =
            RemindersLocalRepository(
                database.reminderDao(),
                Dispatchers.Main
            )
    }

    @After
    fun cleanUp() {
        database.close()
    }

    @Test
    fun saveReminder_retrieveReminder() = runBlocking {
        //GIVEN - Add a new reminder to database
        val newReminder = ReminderDTO("title_one","DESCRIPTION","Work",1.3213123,34.123123)
        localDataSource.saveReminder(newReminder)

        //WHEN - Reminder retrieve is returned
        val result = localDataSource.getReminder(newReminder.id)

        //THEN - Return the above saved task
        assertThat(result.succeeded,`is`(true))
        result as Success
        assertThat(result.data.title, `is`("title_one"))
        assertThat(result.data.description, `is`("DESCRIPTION"))
        assertThat(result.data.location, `is`("Work"))
        assertThat(result.data.latitude, `is`(1.3213123))
        assertThat(result.data.longitude, `is`(34.123123))
    }

    @Test
    fun deleteAllReminders() = runBlockingTest {
        database.reminderDao().deleteAllReminders()
    }

//    TODO: Add testing implementation to the RemindersLocalRepository.kt

}