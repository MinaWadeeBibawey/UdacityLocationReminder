package com.udacity.project4.koin

import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import org.koin.dsl.module

val repositoriesModule = module {
    single<ReminderDataSource> {
        RemindersLocalRepository(
            remindersDao = get()
        )
    }
}
