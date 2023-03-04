package android.com.example.udacityfourthproject.locationreminders.reminderslist

import android.com.example.udacityfourthproject.R
import android.com.example.udacityfourthproject.authentication.AuthenticationActivity
import android.com.example.udacityfourthproject.base.BaseFragment
import android.com.example.udacityfourthproject.base.NavigationCommand
import android.com.example.udacityfourthproject.databinding.FragmentRemindersBinding
import android.com.example.udacityfourthproject.locationreminders.ReminderDescriptionActivity
import android.com.example.udacityfourthproject.locationreminders.RemindersActivity
import android.com.example.udacityfourthproject.utils.setDisplayHomeAsUpEnabled
import android.com.example.udacityfourthproject.utils.setTitle
import android.com.example.udacityfourthproject.utils.setup
import android.com.example.udacityfourthproject.utils.startNewActivity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ReminderListFragment : BaseFragment() {
    //use Koin to retrieve the ViewModel instance
    override val _viewModel: RemindersListViewModel by viewModel()
    private lateinit var binding: FragmentRemindersBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_reminders, container, false
            )
        binding.viewModel = _viewModel

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(false)
        setTitle(getString(R.string.app_name))

        binding.refreshLayout.setOnRefreshListener { _viewModel.loadReminders() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        setupRecyclerView()
        binding.addReminderFAB.setOnClickListener {
            navigateToAddReminder()
        }

    }

    override fun onResume() {
        super.onResume()
        //load the reminders list on the ui
        _viewModel.loadReminders()
    }

    private fun navigateToAddReminder() {
        //use the navigationCommand live data to navigate between the fragments
        _viewModel.navigationCommand.postValue(
            NavigationCommand.To(
                ReminderListFragmentDirections.toSaveReminder()
            )
        )
    }

    private fun setupRecyclerView() {
        val adapter = RemindersListAdapter {
            val intent = Intent(requireContext(),ReminderDescriptionActivity::class.java)
            intent.putExtra("EXTRA_ReminderDataItem",it)
            startActivity(intent)
        }

//        setup the recycler view using the extension function
        binding.reminderssRecyclerView.setup(adapter)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                AuthUI.getInstance().signOut(requireContext())

                val activity = AuthenticationActivity::class.java
                requireActivity().startNewActivity(activity)
            }
        }
        return super.onOptionsItemSelected(item)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
//        display logout as menu item
        inflater.inflate(R.menu.main_menu, menu)
    }

}
