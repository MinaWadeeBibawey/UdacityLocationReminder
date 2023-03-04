package android.com.example.udacityfourthproject.locationreminders.reminderslist

import android.com.example.udacityfourthproject.locationreminders.data.dto.ReminderDTO
import java.io.Serializable
import java.util.*

/**
 * data class acts as a data mapper between the DB and the UI
 */
data class ReminderDataItem(
    var title: String?,
    var description: String?,
    var location: String?,
    var latitude: Double?,
    var longitude: Double?,
    val id: String = UUID.randomUUID().toString()
) : Serializable{
    fun toDomainModel(): ReminderDTO {
        return ReminderDTO(title,description, location, latitude,longitude)
    }
}