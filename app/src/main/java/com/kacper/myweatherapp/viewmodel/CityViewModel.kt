package com.kacper.myweatherapp.viewmodel

import android.app.Application

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.kacper.myweatherapp.data.AppDatabase
import com.kacper.myweatherapp.data.City
import com.kacper.myweatherapp.data.CityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class CityViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CityRepository
    init {
        val cityDao = AppDatabase.getDatabase(application, viewModelScope).cityDao()
        repository = CityRepository(cityDao)
    }
    fun getCity(city: City) : LiveData<City> {
        return repository.getCity(city.uid)
    }

    fun getAll(): LiveData<List<City>> {
        return  repository.getAll()
    }

    fun insert(city: City) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(city)
    }

    fun update(city: City) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(city)
    }

    fun delete(city: City) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(city)
    }

    fun isEmpty() : LiveData<City> {
        return repository.isEmpty()
    }


}