package android.com.example.udacityfourthproject

import android.app.Application
import android.com.example.udacityfourthproject.koin.repositoriesModule
import android.com.example.udacityfourthproject.locationreminders.data.ReminderDataSource
import android.com.example.udacityfourthproject.locationreminders.data.local.LocalDB
import android.com.example.udacityfourthproject.locationreminders.data.local.RemindersLocalRepository
import android.com.example.udacityfourthproject.locationreminders.reminderslist.RemindersListViewModel
import android.com.example.udacityfourthproject.locationreminders.savereminder.SaveReminderViewModel
import androidx.lifecycle.SavedStateHandle
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        /**
         * use Koin Library as a service locator
         */
        val myModule = module {
            //Declare a ViewModel - be later inject into Fragment with dedicated injector using by viewModel()
            viewModel {
                RemindersListViewModel(
                    get()
                )
            }
            //Declare singleton definitions to be later injected using by inject()
            single {
                //This view model is declared singleton to be used across multiple fragments
                SaveReminderViewModel(
                    get()
                )
            }
           /* single { RemindersLocalRepository(get()) as ReminderDataSource }*/

            single { LocalDB.createRemindersDao(this@MyApp) }
        }

        startKoin {
            androidContext(this@MyApp)
            modules(repositoriesModule ,myModule)
        }
    }
}