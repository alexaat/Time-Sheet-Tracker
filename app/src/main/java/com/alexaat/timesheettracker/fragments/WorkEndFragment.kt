package com.alexaat.timesheettracker.fragments


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.alexaat.timesheettracker.R
import com.alexaat.timesheettracker.database.WorkDayDatabase
import com.alexaat.timesheettracker.databinding.FragmentWorkEndBinding
import com.alexaat.timesheettracker.viewModels.WorkEndFragmentViewModel
import com.alexaat.timesheettracker.viewModels.WorkEndFragmentViewModelFactory
import com.google.android.material.snackbar.Snackbar

class WorkEndFragment : Fragment() {

    lateinit var viewModel:WorkEndFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentWorkEndBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_work_end, container, false)
        val argument = WorkEndFragmentArgs.fromBundle(requireArguments()).workDayId
        val activity = this.requireActivity()
        val db = WorkDayDatabase.getInstance(activity).workDayDatabaseDao
        val viewModelFactory = WorkEndFragmentViewModelFactory(argument, db)
        viewModel = ViewModelProvider(this, viewModelFactory).get(WorkEndFragmentViewModel::class.java)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setHasOptionsMenu(true)

        binding.button.setOnClickListener {
            val description = binding.editDescription.text.toString()
            viewModel.onSaveButtonPressed(description)


        }
        viewModel.navigateToListFragment.observe(viewLifecycleOwner, Observer {
            if (it) {
                val action = WorkEndFragmentDirections.actionWorkEndFragmentToListFragment()
                val navController = findNavController()
                navController.navigate(action)
                viewModel.doneNavigating()
            }
        })

        viewModel.showClockOutNotSavedSnackBar.observe(viewLifecycleOwner, Observer {
            if(it){
                this.view?.let{view->
                    val bar = Snackbar.make(view, getString(R.string.clock_out_is_not_saved), Snackbar.LENGTH_SHORT)
                    bar.view.setBackgroundResource(R.color.secondaryLightColor)
                    bar.show()
                }
                viewModel.showClockOutNotSavedSnackBarComplete()
            }
        })

        viewModel.showClockOutSavedSnackBar.observe(viewLifecycleOwner, Observer {
            if(it){
                this.view?.let{view->
                   val bar =  Snackbar.make(view, getString(R.string.clock_out_is_saved), Snackbar.LENGTH_SHORT)
                   bar.view.setBackgroundResource(R.color.primaryLightColor)
                   bar.show()
                }
                viewModel.showClockOutSavedSnackBarComplete()
            }
        })

        return binding.root
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home ->{
                viewModel.cancelClockOut()
                return false
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onResume() {
        super.onResume()
        view?.let{
            it.isFocusableInTouchMode = true
            it.requestFocus()
            it.setOnKeyListener{ _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
                    viewModel.cancelClockOut()
                }
                false
            }
        }

    }
}


