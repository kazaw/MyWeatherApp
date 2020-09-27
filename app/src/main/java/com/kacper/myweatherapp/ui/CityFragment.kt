package com.kacper.myweatherapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.kacper.myweatherapp.R
import com.kacper.myweatherapp.data.City

/**
 * A fragment representing a list of Items.
 */
class CityFragment : DialogFragment() {

    private var columnCount = 1
    private lateinit var cityList: List<City>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
            cityList = it.getSerializable(ARG_CITY_LIST) as List<City>
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
                adapter = MyCityRecyclerViewAdapter(activity, cityList) {
                    itemClick()
                }
            }
        }
        return view
    }

    private fun itemClick(){
        Toast.makeText(context, "Item clicked", Toast.LENGTH_SHORT).show()
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"
        const val ARG_CITY_LIST = "city-list"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int, cityList: ArrayList<City>) =
            CityFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                    putSerializable(ARG_CITY_LIST, cityList)
                }
            }
    }
}