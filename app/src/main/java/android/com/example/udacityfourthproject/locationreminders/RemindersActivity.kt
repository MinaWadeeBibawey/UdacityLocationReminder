package android.com.example.udacityfourthproject.locationreminders

import android.Manifest
import android.com.example.udacityfourthproject.R
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.navigateUp
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_reminders.*

/**
 * The RemindersActivity that holds the reminders fragments
 */

@RequiresApi(Build.VERSION_CODES.O)
class RemindersActivity : AppCompatActivity() {

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    @RequiresApi(33)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminders)
        setSupportActionBar(findViewById(R.id.toolbar))


        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                Snackbar.make(
                    findViewById<View>(android.R.id.content).rootView,
                    "Please grant Notification permission from App Settings",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                findNavController(R.id.nav_host_fragment).popBackStack()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
