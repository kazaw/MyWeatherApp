package com.kacper.myweatherapp.data

import androidx.lifecycle.LiveData

class CityRepository(private val cityDao: CityDao) {
    fun getAll() = cityDao.getAll()

    fun getCity(cityUid: Int) = cityDao.getCity(cityUid)

    suspend fun insert(city: City) = cityDao.insert(city)

    suspend fun insertAll(cityList: List<City>) = cityDao.insertAll(cityList)

    suspend fun update(city: City) = cityDao.update(city)

    suspend fun delete(city: City) = cityDao.delete(city)

    suspend fun deleteAll() = cityDao.deleteAll()

    fun isEmpty(): LiveData<City> = cityDao.isEmpty()
}