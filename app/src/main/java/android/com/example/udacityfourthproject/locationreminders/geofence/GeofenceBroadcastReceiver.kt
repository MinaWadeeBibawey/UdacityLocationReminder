package android.com.example.udacityfourthproject.locationreminders.geofence

import android.com.example.udacityfourthproject.locationreminders.data.ReminderDataSource
import android.com.example.udacityfourthproject.locationreminders.data.dto.ReminderDTO
import android.com.example.udacityfourthproject.locationreminders.data.dto.Result
import android.com.example.udacityfourthproject.locationreminders.reminderslist.ReminderDataItem
import android.com.example.udacityfourthproject.utils.sendNotification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import kotlinx.coroutines.*
import org.koin.java.KoinJavaComponent.inject
import kotlin.coroutines.CoroutineContext

/**
 * Triggered by the Geofence.  Since we can have many Geofences at once, we pull the request
 * ID from the first Geofence, and locate it within the cached data in our Room DB
 *
 * Or users can add the reminders and then close the app, So our app has to run in the background
 * and handle the geofencing in the background.
 * To do that you can use https://developer.android.com/reference/android/support/v4/app/JobIntentService to do that.
 *
 */

private const val TAG = "GeofenceReceiver"

class GeofenceBroadcastReceiver() : BroadcastReceiver(), CoroutineScope {

    private var coroutineJob: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + coroutineJob

    val repo : ReminderDataSource by inject(ReminderDataSource::class.java)

    override fun onReceive(context: Context, intent: Intent) {

        GeofenceTransitionsJobIntentService.enqueueWork(context, intent)
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent?.hasError() == true) {
            val errorMessage = geofencingEvent.errorCode.let {
                GeofenceStatusCodes
                    .getStatusCodeString(it)
            }
            Log.e(TAG, "$errorMessage test geofence error")
            return
        }

        // Get the transition type.
        val geofenceTransition = geofencingEvent?.geofenceTransition

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            val triggeringGeofences = geofencingEvent.triggeringGeofences

            triggeringGeofences?.forEach {

                //Get the local repository instance
                //Interaction to the repository has to be through a coroutine scope
                CoroutineScope(coroutineContext).launch(SupervisorJob()) {
                    //get the reminder with the request id
                    val result = repo.getReminder(it.requestId)
                    if (result is Result.Success<ReminderDTO>) {
                        val reminderDTO = result.data
                        //send a notification to the user with the reminder details
                        sendNotification(
                            context, ReminderDataItem(
                                reminderDTO.title,
                                reminderDTO.description,
                                reminderDTO.location,
                                reminderDTO.latitude,
                                reminderDTO.longitude,
                                reminderDTO.id
                            )
                        )
                    }
                }
            }
        } else {
            // Log the error.
            Log.e(
                TAG, "Cannot Create geofence Broadcast"
            )
        }
    }
}
