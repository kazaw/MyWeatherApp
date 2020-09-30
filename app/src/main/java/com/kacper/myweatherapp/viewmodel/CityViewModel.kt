package com.kacper.myweatherapp.viewmodel

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kacper.myweatherapp.data.City
import com.kacper.myweatherapp.utilities.KEY_PREFERENCE_CITY_LIST
import kotlinx.coroutines.launch


class CityViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(
        application
    )
    private val sharedPreferencesEditor: SharedPreferences.Editor = sharedPreferences.edit()
    private val gson: Gson = Gson()

    inline fun <reified T> genericType() = object: TypeToken<T>() {}.type
    private fun getCityList() : MutableList<City> {
        Log.d("CityViewModelDEBUG", "getCityList")
        val jsonString: String? = sharedPreferences.getString(KEY_PREFERENCE_CITY_LIST, "")
        val type = genericType<List<City>>()
        return gson.fromJson(jsonString, type)
    }

    fun getAll(): LiveData<List<City>> {
        val liveData = MutableLiveData<List<City>>()
        liveData.value = getCityList()
        return liveData
    }

    fun insert(city: City) = viewModelScope.launch {
        val cityList = getCityList()
        cityList.add(city)
        val jsonString = gson.toJson(cityList)
        sharedPreferencesEditor.putString(KEY_PREFERENCE_CITY_LIST, jsonString)
        sharedPreferencesEditor.apply()
    }

}