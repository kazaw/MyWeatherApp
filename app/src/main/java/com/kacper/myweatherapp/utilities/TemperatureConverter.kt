package com.kacper.myweatherapp.utilities

import java.text.DecimalFormat

fun convertKelvinToCelsius(kelvin : Double) : Double {
    return kelvin - 273.15
}

fun convertKelvinToFahrenheit(kelvin : Double) : Double {
    return (kelvin * 9/5) - 459.67
}

fun convertCelsiusToFahrenheit(celsius : Double) : Double {
    return celsius + 273.15
}
fun convertFahrenheitToKelvin(fahrenheit : Double) : Double {
    return (fahrenheit + 459.67) * (5/9)
}

fun getTemperatureString(key: String, temperature : Double) : String {
    val decimalFormat = DecimalFormat("#.##")
    var temperatureString = ""
    when(key){
        "C" -> temperatureString =
            decimalFormat.format(convertKelvinToCelsius(temperature)).toString() + "\u2103"
        "K" -> temperatureString = "$temperature K"
        "F" -> temperatureString =
            decimalFormat.format(convertKelvinToFahrenheit(temperature)).toString() + "\u2109"
    }
    return  temperatureString
}