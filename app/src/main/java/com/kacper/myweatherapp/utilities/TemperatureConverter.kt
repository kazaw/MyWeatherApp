package com.kacper.myweatherapp.utilities

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