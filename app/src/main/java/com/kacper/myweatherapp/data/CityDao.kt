package com.kacper.myweatherapp.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kacper.myweatherapp.utilities.DATABASE_TABLE_CITY

@Dao
interface CityDao {
    @Query("SELECT * FROM $DATABASE_TABLE_CITY")
    fun getAll(): LiveData<List<City>>

    @Query("SELECT * FROM $DATABASE_TABLE_CITY WHERE uid = :CityUid")
    fun getCity(CityUid: Int) : LiveData<City>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(city: City)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(city: List<City>)

    @Update
    suspend fun update(city: City)

    @Delete
    suspend fun delete(city: City)

    @Query("DELETE FROM $DATABASE_TABLE_CITY")
    suspend fun deleteAll()

    @Query("SELECT * FROM $DATABASE_TABLE_CITY LIMIT 1")
    fun isEmpty(): LiveData<City>

}