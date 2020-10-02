package com.kacper.myweatherapp

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kacper.myweatherapp.api.OpenWeatherMapAPI
import com.kacper.myweatherapp.data.City
import com.kacper.myweatherapp.data.WeatherAdapter
import com.kacper.myweatherapp.ui.CityFragment
import com.kacper.myweatherapp.utilities.*
import com.kacper.myweatherapp.viewmodel.CityViewModel
import com.kacper.myweatherapp.viewmodel.CityViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_weather.*
import java.text.DecimalFormat

class MainActivity : AppCompatActivity(), Toolbar.OnMenuItemClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private val PERMISSIONS_REQUEST_LOCATION = 99
    private lateinit var requestQueue: RequestQueue
    private lateinit var cityList: MutableList<City>
    private lateinit var activeCity: City
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var gson: Gson
    private lateinit var sharedPreferencesEditor : SharedPreferences.Editor
    private lateinit var cityViewModel: CityViewModel
    private lateinit var cityViewModelFactory: CityViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        supportActionBar?.hide(); //hide the title bar
        setContentView(R.layout.activity_main)
        toolbar_main.setNavigationIcon(R.drawable.ic_baseline_menu_24)
        toolbar_main.setNavigationOnClickListener {
            navigationClick()
        }

        toolbar_main.inflateMenu(R.menu.menu_main)
        toolbar_main.setOnMenuItemClickListener(this)

        gson = Gson()
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        sharedPreferencesEditor = sharedPreferences.edit()
        setupPermissions()
        requestQueue = Volley.newRequestQueue(this)

        cityViewModelFactory = CityViewModelFactory(application)
        cityViewModel = ViewModelProvider(this, cityViewModelFactory).get(
            CityViewModel::class.java
        )

        if (sharedPreferences.getString(KEY_PREFERENCE_CITY_LIST, null) != null){
            getCityList()
        } else {
            cityList = ArrayList()
            activeCity = City("My location", 15.3, 15.3)//TODO: get device location
            cityList.add(activeCity)
            val jsonString = gson.toJson(cityList)
            sharedPreferencesEditor.putString(KEY_PREFERENCE_CITY_LIST, jsonString)
            sharedPreferencesEditor.apply()
        }
        try {
            //cityList = WeatherAdapter.getCityList() as MutableList<City>
            getWeatherData()
            activeCity = cityList[0]//TODO: Selected from recycler view
            setUI(activeCity)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

/*    private fun getWeatherData(lat: Double, lon: Double){
        val url = OpenWeatherMapAPI.getWeatherGPSDataLink(lat, lon)
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                activeCity = cityList[1]//TODO: Selected from recycler view
                activeCity = WeatherAdapter.setCityFromJsonString(response, activeCity)
                setUI(activeCity)
            },
            { Toast.makeText(this, "Network didnt work", Toast.LENGTH_SHORT).show() })
        requestQueue.add(stringRequest)
    }*/

    private fun getWeatherData(){
        for (i in 0 until cityList.size) {
            var item = cityList[i]
            val url = OpenWeatherMapAPI.getWeatherGPSDataLink(item.lat, item.lon)
            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->

                    item = WeatherAdapter.setCityFromJsonString(response, item)
                    cityList[i] = item
                    Log.d("MainActivityDEBUG", "volley ok $i $item")
                },
                {
                    Toast.makeText(this, "Network didnt work $i", Toast.LENGTH_SHORT).show()
                    Log.d("MainActivityDEBUG", "volley didnt work $i $item")})
            requestQueue.add(stringRequest)
        }
    }


    private fun setUI(city: City){// TODO: USE Event bus to change this onclick
        val decimalFormat = DecimalFormat("#.##")
        var temperature = city.temperature
        var temperatureString = sharedPreferences.getString(KEY_PREFERENCE_TEMPERATURE, "")?.let {
            getTemperatureString(
                it, temperature)
        }
/*        when(sharedPreferences.getString(KEY_PREFERENCE_TEMPERATURE, "")){
            "C" -> temperatureString =
                decimalFormat.format(convertKelvinToCelsius(temperature)).toString() + "\u2103"
            "K" -> temperatureString = "$temperature K"
            "F" -> temperatureString =
                decimalFormat.format(convertKelvinToFahrenheit(temperature)).toString() + "\u2109"
        }*/
        textView_location.text = city.name
        textView_longitude_data.text = decimalFormat.format(city.lon)
        textView_latitude_data.text = decimalFormat.format(city.lat)
        textView_temperature_data.text =  temperatureString
        textView_wind_data.text = city.windSpeed.toString()
        textView_pressure_data.text = city.pressure.toString()
        val imageResourceId = resources.getIdentifier(
            city.iconName, "drawable",
            packageName
        )
        imageView_weather_icon.setImageResource(imageResourceId)
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makePermissionsRequest()
        }
    }

    private fun makePermissionsRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSIONS_REQUEST_LOCATION
        )
    }

    private fun startSettingsActivity(){
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun navigationClick(){//TODO: Change name
        //Toast.makeText(this, "TODO: Navigation Click", Toast.LENGTH_SHORT).show()
        val cityFragment = CityFragment.newInstance(1, cityList as ArrayList<City>)
        supportFragmentManager.let { cityFragment.show(it, "CityFragment") }
    }

    inline fun <reified T> genericType() = object: TypeToken<T>() {}.type
    private fun getCityList(){
        Log.d("MainActivityDEBUG", "getCityList")
        val jsonString: String? = sharedPreferences.getString(KEY_PREFERENCE_CITY_LIST, "")
        val type = genericType<List<City>>()
        cityList = gson.fromJson(jsonString, type)
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return when (item?.itemId){
            R.id.action_settings -> {
                startSettingsActivity()
                true
            }
            else -> false
        }
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        //Toast.makeText(this, "Shared pre", Toast.LENGTH_SHORT).show()
        if (p1 == KEY_PREFERENCE_TEMPERATURE) {
            setUI(activeCity)//TODO: Change it because is ugly
        } else if (p1 == KEY_PREFERENCE_CITY_LIST) {
            Toast.makeText(this, "changed list", Toast.LENGTH_SHORT).show()
            getCityList()
            getWeatherData()
        }
    }

}