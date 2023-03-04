package android.com.example.udacityfourthproject.koin

import android.com.example.udacityfourthproject.locationreminders.data.ReminderDataSource
import android.com.example.udacityfourthproject.locationreminders.data.local.RemindersLocalRepository
import org.koin.dsl.module

val repositoriesModule = module {
    single<ReminderDataSource> {
        RemindersLocalRepository(
            remindersDao = get()
        )
    }
}
