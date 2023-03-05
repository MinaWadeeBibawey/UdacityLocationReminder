package com.udacity.project4.reminderslist


import com.udacity.project4.data.FakeDataSource

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.udacity.project4.FakeReminders
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.getOrAwaitValue
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.data.dto.succeeded
import com.udacity.project4.locationreminders.data.dto.Result.Error
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.DelayController
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
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
class RemindersListViewModelTest : AutoCloseKoinTest() {

    private lateinit var viewModel: RemindersListViewModel
    private lateinit var fakeRepository: FakeDataSource
    private lateinit var fakerRemindersList: MutableList<ReminderDTO>


    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initKoin() {
        stopKoin()//stop the original app koin
        fakerRemindersList = FakeReminders.getRemindersList()
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    get() as ReminderDataSource
                )
            }

            single<ReminderDataSource> {
                FakeDataSource(
                    remindersList = fakerRemindersList
                )
            }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }
        //Get our real repository
        viewModel = get()
        fakeRepository = get<ReminderDataSource>() as FakeDataSource

    }

    @Test
    fun getReminders_returnError() = mainCoroutineRule.runBlockingTest {
        // set return value to return Error
        fakeRepository.setReturnError(true)
        //get Reminders
        viewModel.loadReminders()
        // Should Return error
        (mainCoroutineRule.coroutineContext[ContinuationInterceptor]!! as DelayController).pauseDispatcher()
        assertThat(fakeRepository.getReminders().succeeded, `is`(false))
        // test liveData Return
        (mainCoroutineRule.coroutineContext[ContinuationInterceptor]!! as DelayController).resumeDispatcher()
        assertThat(viewModel.showNoData.getOrAwaitValue(), `is`(true))
    }

    @Test
    fun getReminders_returnSuccess() = mainCoroutineRule.runBlockingTest {
        // set return value to return Error
        fakeRepository.setReturnError(false)
        // get Reminders
        viewModel.loadReminders()
        // Should not Return error
        (mainCoroutineRule.coroutineContext[ContinuationInterceptor]!! as DelayController).pauseDispatcher()
        assertThat(fakeRepository.getReminders().succeeded, `is`(true))
        // test liveData Return
        (mainCoroutineRule.coroutineContext[ContinuationInterceptor]!! as DelayController).resumeDispatcher()
        assertThat(viewModel.remindersList.value?.size, `is`(3))
    }

    @Test
    fun getReminder_returnError() = mainCoroutineRule.runBlockingTest {
        // set return value to return Error
        fakeRepository.setReturnError(true)
        //get Reminders
        val reminder = fakeRepository.getReminder("asdasdfsas")

        assertThat(reminder, `is`(Error("failed to get reminder")))
    }

    @Test
    fun getReminders_remindersNotFound_snackBarShown() = mainCoroutineRule.runBlockingTest {
        // set return value to return Error
        fakeRepository.setReturnError(true)
        // get Reminders
        viewModel.loadReminders()
        // Should Return error
        (mainCoroutineRule.coroutineContext[ContinuationInterceptor]!! as DelayController).pauseDispatcher()
        assertThat(fakeRepository.getReminders().succeeded, `is`(false))
        // test liveData Return
        (mainCoroutineRule.coroutineContext[ContinuationInterceptor]!! as DelayController).resumeDispatcher()
        assertThat(viewModel.showSnackBar.getOrAwaitValue(), `is`("failed to get Reminders"))
    }

    @Test
    fun showLoading_stopLoading_showReminders() = mainCoroutineRule.runBlockingTest {
        (mainCoroutineRule.coroutineContext[ContinuationInterceptor]!! as DelayController).pauseDispatcher()
        viewModel.loadReminders()
        assertThat(viewModel.showLoading.getOrAwaitValue(), `is`(true))

        (mainCoroutineRule.coroutineContext[ContinuationInterceptor]!! as DelayController).resumeDispatcher()
        assertThat(viewModel.showLoading.getOrAwaitValue(), `is`(false))
    }

    @Test
    fun removeAllReminders_reminderListEmpty() = mainCoroutineRule.runBlockingTest {
        fakeRepository.deleteAllReminders()

        (mainCoroutineRule.coroutineContext[ContinuationInterceptor]!! as DelayController).pauseDispatcher()
        viewModel.loadReminders()

        (mainCoroutineRule.coroutineContext[ContinuationInterceptor]!! as DelayController).resumeDispatcher()
        assertThat(viewModel.remindersList.getOrAwaitValue().isEmpty(), `is`(true))
    }

    @Test
    fun getReminderWithID_reminderNotFound() = mainCoroutineRule.runBlockingTest {
        // set return value to return Error
        fakeRepository.setReturnError(false)
        //get Reminders
        val reminder = fakeRepository.getReminder("asdasdfsas")

        assertThat(reminder, `is`(Error("Reminder not found")))
    }

    @Test
    fun getReminderWithID_returnReminder() = mainCoroutineRule.runBlockingTest {
        (mainCoroutineRule.coroutineContext[ContinuationInterceptor]!! as DelayController).pauseDispatcher()
        viewModel.loadReminders()

        (mainCoroutineRule.coroutineContext[ContinuationInterceptor]!! as DelayController).resumeDispatcher()
        val remindersList = viewModel.remindersList.getOrAwaitValue()

        //get Reminders
        fakeRepository.setReturnError(false)

        assertThat(remindersList[0].id, `is`(fakerRemindersList[0].id))
    }
}
