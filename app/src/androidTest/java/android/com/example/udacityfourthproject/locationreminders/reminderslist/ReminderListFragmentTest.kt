package android.com.example.udacityfourthproject.locationreminders.reminderslist

import android.com.example.udacityfourthproject.FakeReminders
import android.com.example.udacityfourthproject.MainCoroutineRule
import android.com.example.udacityfourthproject.R
import android.com.example.udacityfourthproject.getOrAwaitValue
import android.com.example.udacityfourthproject.locationreminders.RemindersActivity
import android.com.example.udacityfourthproject.locationreminders.data.FakeDataSource
import android.com.example.udacityfourthproject.locationreminders.data.ReminderDataSource
import android.com.example.udacityfourthproject.locationreminders.savereminder.SaveReminderViewModel
import android.com.example.udacityfourthproject.util.DataBindingIdlingResource
import android.com.example.udacityfourthproject.util.monitorActivity
import android.com.example.udacityfourthproject.utils.EspressoIdlingResource
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.DelayController
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get
import org.mockito.Mockito.*
import kotlin.coroutines.ContinuationInterceptor

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest : AutoCloseKoinTest() {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: RemindersListViewModel
    private lateinit var saveReminderViewModel: SaveReminderViewModel
    private lateinit var fakeRepository: FakeDataSource

    private val dataBindingIdlingResource = DataBindingIdlingResource()


    @Before
    fun initKoin() {
        stopKoin()//stop the original app koin
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    get() as ReminderDataSource
                )
            }

            single {
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
        //Get our real repository
        viewModel = get()
        saveReminderViewModel = get()
        fakeRepository = get<ReminderDataSource>() as FakeDataSource

    }


    fun registerIdlingResources() {
        IdlingRegistry.getInstance().apply {
            register(EspressoIdlingResource.countingIdlingResource)
            register(dataBindingIdlingResource)
        }
    }


    fun unregisterIdlingResources() {
        IdlingRegistry.getInstance().apply {
            unregister(EspressoIdlingResource.countingIdlingResource)
            unregister(dataBindingIdlingResource)
        }
    }


    @Test
    fun clickAddNewReminder_NavigateToSaveReminder() {
        // GIVEN - on the home screen
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        // WHEN - Click on the first list item
        onView(withId(R.id.addReminderFAB)).perform(click())

        // THEN - Verify that we navigate to the first detail screen
        verify(navController).navigate(
            ReminderListFragmentDirections.toSaveReminder()
        )
    }

    @Test
    fun activeTaskDetails_DisplayedInUi() = runBlockingTest {
        val reminder = FakeReminders.getRemindersDto()
        fakeRepository.saveReminder(reminder)

        // GIVEN
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        // WHEN - Click on the first list item
        onView(withId(R.id.reminderssRecyclerView))
            .perform(
                RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                    hasDescendant(withText("Fake_Reminder")), click()
                )
            )

        // THEN - Reminder details are displayed on the screen
        // make sure that the title/description/Location are shown and correct
        onView(withId(R.id.reminderTitle)).check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.reminderTitle)).check(ViewAssertions.matches(withText("Fake_Reminder")))
        onView(withId(R.id.reminderDescription)).check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.reminderDescription)).check(ViewAssertions.matches(withText("FAKE_DESCRIPTION")))
        onView(withId(R.id.selectedLocation)).check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.selectedLocation)).check(ViewAssertions.matches(withText("FAKE_LOCATION")))
    }

    @Test
    fun clickAddNewReminder_showErrorMessage()  = runBlockingTest{
        registerIdlingResources()
        // GIVEN - on the home screen
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // WHEN - Click on add new reminder
        onView(withId(R.id.addReminderFAB)).perform(click())
        // WHEN - Click on save reminder
        onView(withId(R.id.saveReminder)).perform(click())

        (mainCoroutineRule.coroutineContext[ContinuationInterceptor]!! as DelayController).pauseDispatcher()
        val snackBarText = saveReminderViewModel.showSnackBarInt.getOrAwaitValue()

        MatcherAssert.assertThat(snackBarText, CoreMatchers.`is`(R.string.err_enter_title))

        activityScenario.close()
        unregisterIdlingResources()
    }

}