package com.kacper.myweatherapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.kacper.myweatherapp.api.OpenWeatherMapAPI
import com.kacper.myweatherapp.data.City
import com.kacper.myweatherapp.data.WeatherAdapter
import com.kacper.myweatherapp.ui.CityFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_weather.*

class MainActivity : AppCompatActivity(), Toolbar.OnMenuItemClickListener {

    private val PERMISSIONS_REQUEST_LOCATION = 99
    private lateinit var requestQueue: RequestQueue
    private lateinit var cityList: MutableList<City>

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
                var city = cityList[1]
                city = WeatherAdapter.setCityFromJsonString(response, city)
                setUI(city)
            },
            { Toast.makeText(this, "Network didnt work", Toast.LENGTH_SHORT).show() })
        requestQueue.add(stringRequest)
    }

    private fun setUI(city: City){
        textView_location.text = city.name
        textView_temperature_data.text = city.temperature.toString()
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
        Toast.makeText(this, "TODO: Start Setting", Toast.LENGTH_SHORT).show()
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
}