package com.kacper.myweatherapp.api

class OpenWeatherMapAPI {
    companion object{
        public fun getWeatherGPSDataLink(lat: Double, lon: Double): String? {
            return "http://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&appid=$API_KEY"
        }
    }
}