package com.kacper.myweatherapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.ActivityChooserView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import com.kacper.myweatherapp.R
import com.kacper.myweatherapp.data.City
import com.kacper.myweatherapp.viewmodel.CityViewModel
import com.kacper.myweatherapp.viewmodel.CityViewModelFactory

/**
 * A fragment representing a list of Items.
 */
class CityFragment : DialogFragment() {

    private var columnCount = 1
    private lateinit var cityViewModel: CityViewModel
    private lateinit var cityViewModelFactory: CityViewModelFactory
    private lateinit var recyclerViewAdapter : MyCityRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cityViewModelFactory = CityViewModelFactory(requireActivity().application)
        cityViewModel = ViewModelProvider(requireActivity(), cityViewModelFactory).get(
            CityViewModel::class.java
        )

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_city_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                recyclerViewAdapter = MyCityRecyclerViewAdapter(activity, mutableListOf()) {
                    itemClick()
                }
                adapter = recyclerViewAdapter
                cityViewModel.getAll().observe(viewLifecycleOwner, {data ->
                    recyclerViewAdapter.swapData(data)
                })
            }
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
    }

    private fun itemClick(){
        Toast.makeText(context, "Item clicked", Toast.LENGTH_SHORT).show()
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            CityFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}