package com.udacity.project4.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.udacity.project4.FakeReminders
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.R
import com.udacity.project4.data.FakeDataSource
import com.udacity.project4.getOrAwaitValue
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.DelayController
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get
import kotlin.coroutines.ContinuationInterceptor

@ExperimentalCoroutinesApi
class SaveReminderViewModelTest : AutoCloseKoinTest() {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: SaveReminderViewModel
    private lateinit var fakeRepository: FakeDataSource

    @Before
    fun initKoin() {
        stopKoin()//stop the original app koin
        val myModule = module {
            viewModel {
                SaveReminderViewModel(
                    get() as ReminderDataSource
                )
            }

            single<ReminderDataSource> {
                FakeDataSource(
                    remindersList = FakeReminders.getRemindersList()
                )
            }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }
        // Get our real viewModel
        viewModel = get()
        //Get our real repository
        fakeRepository = get<ReminderDataSource>() as FakeDataSource

    }


    @Test
    fun createReminder_validateReminder_saveReminder() = runBlockingTest {
        // Create reminder and add it to the repository.
        val reminder = FakeReminders.getRemindersDto()

        // validate and save reminder
        viewModel.validateEnteredData(reminder)

        // Verify the task is valid.
        assertThat(
            viewModel.validateEnteredData(reminder),
            CoreMatchers.`is`(true)
        )

        (mainCoroutineRule.coroutineContext[ContinuationInterceptor]!! as DelayController).pauseDispatcher()
        viewModel.saveReminder(reminder)
        assertThat(viewModel.showLoading.getOrAwaitValue(), CoreMatchers.`is`(true))

        (mainCoroutineRule.coroutineContext[ContinuationInterceptor]!! as DelayController).resumeDispatcher()
        assertThat(viewModel.showLoading.getOrAwaitValue(), CoreMatchers.`is`(false))
    }

    @Test
    fun createReminder_validateReminder_missingTitle() = runBlockingTest {
        // Create reminder and add it to the repository.
        val reminder = ReminderDataItem(
            null,
            description = "DESCRIPTION_ONE",
            location = "Home",
            latitude = 1.32324432,
            longitude = 1.432434
        )

        // Validate Reminder
        viewModel.validateEnteredData(reminder)

        // Verify the task is valid.
        assertThat(
            viewModel.showSnackBarInt.getOrAwaitValue(),
            CoreMatchers.`is`(R.string.err_enter_title)
        )
    }

    @Test
    fun createReminder_validateReminder_missingLocation() = runBlockingTest {
        // Create reminder and add it to the repository.
        val reminder = ReminderDataItem(
            "Fake_Title",
            description = "DESCRIPTION_ONE",
            location = null,
            latitude = 1.32324432,
            longitude = 1.432434
        )

        // Validate Reminder
        viewModel.validateEnteredData(reminder)

        // Verify the task is valid.
        assertThat(
            viewModel.showSnackBarInt.getOrAwaitValue(),
            CoreMatchers.`is`(R.string.err_select_location)
        )
    }

    //TODO: provide testing to the SaveReminderView and its live data objects


}