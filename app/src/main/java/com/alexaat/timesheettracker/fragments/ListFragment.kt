package com.alexaat.timesheettracker.fragments

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.alexaat.timesheettracker.R
import com.alexaat.timesheettracker.database.WorkDayDatabase
import com.alexaat.timesheettracker.databinding.FragmentListBinding
import com.alexaat.timesheettracker.hideKeyboard
import com.alexaat.timesheettracker.viewModels.ListFragmentViewModel
import com.alexaat.timesheettracker.viewModels.ListFragmentViewModelFactory
import com.google.android.material.snackbar.Snackbar


class ListFragment : Fragment() {



    lateinit var viewModel: ListFragmentViewModel
    var menuColor = Color.BLACK

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        this.hideKeyboard()

        val binding: FragmentListBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_list, container, false
        )

        setHasOptionsMenu(true)


        val activity = this.requireActivity()
        val db = WorkDayDatabase.getInstance(activity).workDayDatabaseDao
        val viewModelFactory = ListFragmentViewModelFactory(activity, db)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ListFragmentViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this


        viewModel.navigateToWorkEndFragment.observe(viewLifecycleOwner, Observer {
            if (it > 0) {
                val action = ListFragmentDirections.actionListFragmentToWorkEndFragment(it)
                val navController = findNavController()
                navController.navigate(action)
                viewModel.navigationDone()

            }
        })

        viewModel.showSnackBar.observe(viewLifecycleOwner, Observer{
            if(it!=""){
                this.view?.let{view ->
                    showSnackBar(view, it)
                }
                viewModel.snackBarIsDisplayed()
            }
        })

        viewModel.isTrackingOn.observe(viewLifecycleOwner, Observer {
             if(it){
                 menuColor = Color.LTGRAY
             }else{
                 menuColor = Color.BLACK
             }
            activity.invalidateOptionsMenu()
        })



        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.deleteAll -> viewModel.deleteAllFromDb()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showSnackBar(view:View, text:String){
        Snackbar.make(view, text, Snackbar.LENGTH_LONG).show()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
       super.onPrepareOptionsMenu(menu)
        if(menu.size()>0){
            val item = menu.getItem(0)
            val spanString = SpannableString(menu.getItem(0).getTitle().toString())
            spanString.setSpan(ForegroundColorSpan(menuColor), 0, spanString.length, 0)
            item.title = spanString
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }
}
