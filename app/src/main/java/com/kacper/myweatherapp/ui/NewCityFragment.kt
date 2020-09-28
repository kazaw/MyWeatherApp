package com.kacper.myweatherapp.ui

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.kacper.myweatherapp.R
import com.kacper.myweatherapp.data.City
import com.kacper.myweatherapp.utilities.KEY_PREFERENCE_CITY_LIST
import java.text.DecimalFormat

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"

class NewCityFragment : DialogFragment() {
    // TODO: Rename and change types of parameters
    private lateinit var cityList: MutableList<City>
    private lateinit var gson: Gson
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesEditor : SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferencesEditor = sharedPreferences.edit()
        gson = Gson()
        arguments?.let {
            cityList = it.getSerializable(CityFragment.ARG_CITY_LIST) as MutableList<City>
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_new_city, container, false)
        val button : Button = v.findViewById(R.id.button_add_city_dialog)
        val editText1 : EditText = v.findViewById(R.id.editText_city_name)
        val editText2 : EditText = v.findViewById(R.id.editText_latitude)
        val editText3 : EditText = v.findViewById(R.id.editText_longitude)
        button.setOnClickListener{
            if (editText1.text.isEmpty() || editText2.text.isEmpty() || editText3.text.isEmpty()){
                Toast.makeText(context, "Fill all fields", Toast.LENGTH_SHORT).show()
            }
            else {
                val name : String = editText1.text.toString()
                val lat : Double = editText2.text.toString().toDouble()
                val lon : Double = editText3.text.toString().toDouble()
                onButtonClick(City(name, lat, lon))
            }
        }
        return v
    }

    private fun onButtonClick(city: City) {
        cityList.add(city)
        val jsonString = gson.toJson(cityList)
        sharedPreferencesEditor.putString(KEY_PREFERENCE_CITY_LIST, jsonString)
        sharedPreferencesEditor.apply()
        dismiss()
    }

    companion object {
        @JvmStatic
        fun newInstance(cityList: ArrayList<City>) =
            NewCityFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(CityFragment.ARG_CITY_LIST, cityList)
                }
            }
    }
}