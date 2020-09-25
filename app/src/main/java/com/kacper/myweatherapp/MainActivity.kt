package com.kacper.myweatherapp

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.kacper.myweatherapp.api.OpenWeatherMapAPI
import com.kacper.myweatherapp.data.City
import com.kacper.myweatherapp.data.WeatherAdapter
import com.kacper.myweatherapp.ui.CityFragment
import com.kacper.myweatherapp.utilities.convertKelvinToCelsius
import com.kacper.myweatherapp.utilities.convertKelvinToFahrenheit
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_weather.*
import java.text.DecimalFormat

class MainActivity : AppCompatActivity(), Toolbar.OnMenuItemClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private val PERMISSIONS_REQUEST_LOCATION = 99
    private lateinit var requestQueue: RequestQueue
    private lateinit var cityList: MutableList<City>
    private lateinit var activeCity: City
    private lateinit var sharedPreferences: SharedPreferences

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

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        setupPermissions()
        requestQueue = Volley.newRequestQueue(this)

        try {
            cityList = WeatherAdapter.getCityList() as MutableList<City>
            getWeatherData(10.0, 10.0)
        }catch (e: Exception){
            e.printStackTrace()
        }

    }

    private fun getWeatherData(lat: Double, lon: Double){
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
    }

    private fun setUI(city: City){

        var temperature = city.temperature
        val decimalFormat = DecimalFormat("#.##")
        var temperatureString = ""
        when(sharedPreferences.getString("key_preference_temperature", "")){
            "C" -> temperatureString = decimalFormat.format(convertKelvinToCelsius(temperature)).toString() + "\u2103"
            "K" -> temperatureString = "$temperature K"
            "F" -> temperatureString = decimalFormat.format(convertKelvinToFahrenheit(temperature)).toString() + "\u2109"
        }
        textView_location.text = city.name
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
        Toast.makeText(this, "Shared pre", Toast.LENGTH_SHORT).show()
        if (p1 == "key_preference_temperature") {
            setUI(activeCity)//TODO: Change it because is ugly
        }
    }

}