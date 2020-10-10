package com.kacper.myweatherapp

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.kacper.myweatherapp.api.OpenWeatherMapAPI
import com.kacper.myweatherapp.data.City
import com.kacper.myweatherapp.events.ClickCityEvent
import com.kacper.myweatherapp.data.WeatherAdapter
import com.kacper.myweatherapp.events.DeleteCityEvent
import com.kacper.myweatherapp.events.InsertCityEvent
import com.kacper.myweatherapp.ui.CityFragment
import com.kacper.myweatherapp.utilities.*
import com.kacper.myweatherapp.viewmodel.CityViewModel
import com.kacper.myweatherapp.viewmodel.CityViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_weather.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
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

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        setupPermissions()
        requestQueue = Volley.newRequestQueue(this)

        cityViewModelFactory = CityViewModelFactory(application)
        cityViewModel = ViewModelProvider(this, cityViewModelFactory).get(
            CityViewModel::class.java
        )
        cityViewModel.getAll().observeOnce(this, {
            Log.d("MainActivityDEBUG", "cityViewModel-1")
            this.cityList = it as MutableList<City>
            if (cityList.size == 0){
                activeCity = City("My location", 15.3, 15.3)//TODO: get device location
                cityViewModel.insert(activeCity)
                Log.d("MainActivityDEBUG", "cityViewModel-2")
            }
            activeCity = cityList[0]
            setUI(activeCity)
            Log.d("MainActivityDEBUG", "$activeCity ${activeCity.uid}")
            updateAll(cityList)
        })



    }
    private fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
        observe(lifecycleOwner, object : Observer<T> {
            override fun onChanged(t: T?) {
                observer.onChanged(t)
                Log.d("MainActivityDEBUG", "removed observer")
                removeObserver(this)
            }
        })
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
        Log.d("MainActivityDEBUG", "EventBus unregistered")
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
        Log.d("MainActivityDEBUG", "EventBus registered")
    }

    private fun getWeatherData(city : City, isUpdate: Boolean){
        val url = OpenWeatherMapAPI.getWeatherGPSDataLink(city.lat, city.lon)
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                var tmpCity = WeatherAdapter.setCityFromJsonString(response, city)
                if (isUpdate){
                    Log.d("MainActivityDEBUG", "volley and update ok $tmpCity")
                    cityViewModel.update(tmpCity)
                }
                else{
                    Log.d("MainActivityDEBUG", "volley and insert ok $tmpCity")
                    cityViewModel.insert(tmpCity)
                }
            },
            {
                Toast.makeText(this, "Network didnt work", Toast.LENGTH_SHORT).show()
                Log.d("MainActivityDEBUG", "volley didnt work $city")
            })
        requestQueue.add(stringRequest)
    }

    private fun setUI(city: City){// TODO: USE Event bus to change this onclick
        val decimalFormat = DecimalFormat("#.##")
        var temperature = city.temperature
        var temperatureString = sharedPreferences.getString(KEY_PREFERENCE_TEMPERATURE, "")?.let {
            getTemperatureString(
                it, temperature)
        }
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

    private fun updateAll(list: List<City>){
        for (item in list){
            Log.d("MainActivityDEBUG", "updateAll $item")
            getWeatherData(item, true)
        }
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
        val cityFragment = CityFragment.newInstance(1)
        supportFragmentManager.let { cityFragment.show(it, "CityFragment") }
    }

    @Subscribe
    fun onClickCityEvent(event : ClickCityEvent){
        Log.d("MainActivityDEBUG", "EventBus is working ${event.city}")
        getWeatherData(event.city, true)
        activeCity = event.city
        setUI(activeCity)
    }

    @Subscribe
    fun onDeleteCityEvent(event: DeleteCityEvent){
        Log.d("MainActivityDEBUG", "Delete city event${event.city}")
        cityViewModel.delete(event.city)
    }

    @Subscribe
    fun onInsertCityEvent(event: InsertCityEvent){
        Log.d("MainActivityDEBUG", "Insert city event${event.city}")
        getWeatherData(event.city, false)
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
        if (p1 == KEY_PREFERENCE_TEMPERATURE) {
            setUI(activeCity)//TODO: Change it because is ugly
        }
    }

}