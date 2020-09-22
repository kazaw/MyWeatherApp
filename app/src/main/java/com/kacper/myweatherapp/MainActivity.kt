package com.kacper.myweatherapp

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.Window
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), Toolbar.OnMenuItemClickListener {

    private val PERMISSIONS_REQUEST_LOCATION = 99

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
        try {

        }catch (e : Exception){
            e.printStackTrace()
        }

    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makePermissionsRequest()
        }
    }

    private fun makePermissionsRequest() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSIONS_REQUEST_LOCATION)
    }

    private fun startSettingsActivity(){
        Toast.makeText(this, "TODO: Start Setting", Toast.LENGTH_SHORT).show()
    }

    private fun navigationClick(){//TODO: Change name
        Toast.makeText(this, "TODO: Navigation Click", Toast.LENGTH_SHORT).show()
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