package android.com.example.udacityfourthproject

import android.app.Activity
import android.app.Application
import android.com.example.udacityfourthproject.locationreminders.ReminderDescriptionActivity
import android.com.example.udacityfourthproject.locationreminders.RemindersActivity
import android.com.example.udacityfourthproject.locationreminders.data.ReminderDataSource
import android.com.example.udacityfourthproject.locationreminders.data.dto.ReminderDTO
import android.com.example.udacityfourthproject.locationreminders.data.local.LocalDB
import android.com.example.udacityfourthproject.locationreminders.data.local.RemindersLocalRepository
import android.com.example.udacityfourthproject.locationreminders.reminderslist.RemindersListViewModel
import android.com.example.udacityfourthproject.locationreminders.savereminder.SaveReminderViewModel
import android.com.example.udacityfourthproject.util.DataBindingIdlingResource
import android.com.example.udacityfourthproject.util.monitorActivity
import android.com.example.udacityfourthproject.utils.EspressoIdlingResource
import androidx.appcompat.widget.Toolbar
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get

@RunWith(AndroidJUnit4::class)
@LargeTest
//END TO END test to black box test the app
class RemindersActivityTest :
    AutoCloseKoinTest() {// Extended Koin Test - embed autoclose @after method to close Koin after every test

    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application

    private val dataBindingIdlingResource = DataBindingIdlingResource()

    fun <T : Activity> ActivityScenario<T>.getToolbarNavigationContentDescription()
            : String {
        var description = ""
        onActivity {
            description =
                it.findViewById<Toolbar>(R.id.toolbar).navigationContentDescription as String
        }
        return description
    }

    /**
     * As we use Koin as a Service Locator Library to develop our code, we'll also use Koin to test our code.
     * at this step we will initialize Koin related code to be able to use it in out testing.
     */
    @Before
    fun init() {
        stopKoin()//stop the original app koin
        appContext = getApplicationContext()
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
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(appContext) }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }
        //Get our real repository
        repository = get()
        //
        runBlocking {
            repository.deleteAllReminders()
        }

    }

    @Before
    fun registerIdlingResources() {
        IdlingRegistry.getInstance().apply {
            register(EspressoIdlingResource.countingIdlingResource)
            register(dataBindingIdlingResource)
        }
    }

    @After
    fun unregisterIdlingResources() {
        IdlingRegistry.getInstance().apply {
            unregister(EspressoIdlingResource.countingIdlingResource)
            unregister(dataBindingIdlingResource)
        }
    }

    @Test
    fun addNewReminder_saveReminder_missingLocation() {
        // GIVEN - on the home screen
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // WHEN - Click on the first list item
        Espresso.onView(withId(R.id.addReminderFAB)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.reminderTitle))
            .perform(ViewActions.typeText("TITLE1"), ViewActions.closeSoftKeyboard())
        Espresso.onView(withId(R.id.reminderDescription))
            .perform(ViewActions.typeText("DESCRIPTION"))
        Espresso.onView(withId(R.id.selectLocation)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.save_location_button)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.saveReminder)).perform(ViewActions.click())

        activityScenario.close()
    }


    @Test
    fun reminderDetailScreen_doubleUpButton() = runBlocking {
        val reminder = FakeReminders.getRemindersDto()
        repository.saveReminder(reminder)

        // Start the Tasks screen.
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // 1. Click on the reminder on the list.
        Espresso.onView(ViewMatchers.withText("Fake_Reminder")).perform(ViewActions.click())

        //system back press
        Espresso.pressBack()

        // When using ActivityScenario.launch(), always call close().
        activityScenario.close()
    }


    @Test
    fun addNewReminder_location_doubleBackButton() = runBlocking {
        // Start the Tasks screen.
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // 1. Click on add new reminder.
        Espresso.onView(withId(R.id.addReminderFAB)).perform(ViewActions.click())
        // 2. Click on choose location.
        Espresso.onView(withId(R.id.selectLocation)).perform(ViewActions.click())

        // return back to save reminder fragment
        Espresso.pressBack()
          // return back to reminders list fragment
        Espresso.pressBack()

        // When using ActivityScenario.launch(), always call close().
        activityScenario.close()
    }


    @Test
    fun addNewReminder_location_doubleUpButton() = runBlocking {

        // Start the Tasks screen.
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // 1. Click on add new reminder.
        Espresso.onView(withId(R.id.addReminderFAB)).perform(ViewActions.click())
        // 2. Click on choose location.
        Espresso.onView(withId(R.id.selectLocation)).perform(ViewActions.click())

        // return back to save reminder fragment
        Espresso.onView(
            withContentDescription(
                activityScenario
                    .getToolbarNavigationContentDescription()
            )
        ).perform(ViewActions.click())

        // return back to reminders list fragment
        Espresso.onView(
            withContentDescription(
                activityScenario
                    .getToolbarNavigationContentDescription()
            )
        ).perform(ViewActions.click())


        // When using ActivityScenario.launch(), always call close().
        activityScenario.close()
    }

}
