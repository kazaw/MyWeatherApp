package com.kacper.myweatherapp.data

import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class WeatherAdapter {
    fun getCityList(): List<City>? {
        val cityList: MutableList<City> = ArrayList()
        cityList.add(City("My location", 0.0, 0.0))
        cityList.add(City("Cracow", 50.06, 19.56))
        return cityList
    }

    fun getWeatherData(urlString: String?): String? {
        var data: String? = null
        try {
            val url = URL(urlString)
            val httpURLConnection = url.openConnection() as HttpURLConnection
            if (httpURLConnection.responseCode == 200) {
                val buff = BufferedReader(InputStreamReader(httpURLConnection.inputStream))
                val stringBuilder = StringBuilder()
                var line: String?
                while (buff.readLine().also { line = it } != null) stringBuilder.append(line)
                data = stringBuilder.toString()
                httpURLConnection.disconnect()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return data
    }

    fun setCityFromJsonString(data: String?, citySource: City): City? {
        try {
            val jsonObject = JSONObject(data)
            var jsonTmp: JSONObject
            jsonTmp = jsonObject.getJSONObject("main")
            citySource.pressure = java.lang.Double.valueOf(jsonTmp.getString("pressure"))
            citySource.temperature = java.lang.Double.valueOf(jsonTmp.getString("temp"))
            jsonTmp = jsonObject.getJSONObject("wind")
            citySource.windSpeed = java.lang.Double.valueOf(jsonTmp.getString("speed"))
            val jsonArray = jsonObject.getJSONArray("weather")
            jsonTmp = jsonArray[0] as JSONObject
            citySource.iconName = ("i" + jsonTmp.getString("icon")) //Error: The resource name must start with a letter
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return citySource
    }

}