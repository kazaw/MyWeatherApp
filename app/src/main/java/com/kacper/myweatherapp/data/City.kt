package com.kacper.myweatherapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kacper.myweatherapp.utilities.DATABASE_TABLE_CITY
import java.io.Serializable

@Entity(tableName = DATABASE_TABLE_CITY)
data class City(val name : String, val lat : Double, val lon : Double) : Serializable {
    @PrimaryKey(autoGenerate = true) var uid: Int = 0
    var temperature : Double = 0.0
    var windSpeed : Double = 0.0
    var pressure : Double = 0.0
    var iconName : String = ""
    override fun toString(): String {
        return name
    }
}