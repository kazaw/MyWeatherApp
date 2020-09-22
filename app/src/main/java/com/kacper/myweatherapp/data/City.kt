package com.kacper.myweatherapp.data

data class City(val name : String, val lat : Double, val lon : Double) {
    var temperature : Double = 0.0
    var windSpeed : Double = 0.0
    var pressure : Double = 0.0
    var iconName : String = ""
    override fun toString(): String {
        return name
    }
}