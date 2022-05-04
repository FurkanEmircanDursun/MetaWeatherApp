package com.example.myweatherapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import com.example.myweatherapp.R
import com.example.myweatherapp.databinding.ActivityMainBinding
import com.example.myweatherapp.util.Constants
import com.example.myweatherapp.util.LocationTrackListener
import com.example.myweatherapp.util.LocationUtil
import com.example.myweatherapp.util.ToolbarChangesListener
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks, ToolbarChangesListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationTrackListener: LocationTrackListener

    @Inject
    lateinit var fragmentsFactory: WeatherFragmentsFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_MyWeatherApp)
        super.onCreate(savedInstanceState)
        supportFragmentManager.fragmentFactory = fragmentsFactory
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


    }

    fun setOnLocationTrackListener(listener: LocationTrackListener) {
        this.locationTrackListener = listener
    }

    override fun onStart() {
        super.onStart()
        if (!LocationUtil.hasLocationPermissions(this)) {
            requestPermissions()
        } else {
            getLastLocations()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocations() {
        if (LocationUtil.hasLocationPermissions(this)) {
            if (isLocationEnabled()) {
                locationTrackListener.locationTrack(true)
            } else {
                Toast.makeText(this, "", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    private fun requestPermissions() {
        EasyPermissions.requestPermissions(
            this,
            "Uygulamanın düzgün çalışabilmesi için konum izni verilmelidir ",
            Constants.REQUEST_CODE_LOCATION_PERMISSION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        locationTrackListener.locationTrack(true)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    override fun toolbarName(title: String) {
    }

    override fun toolbarBackButton(isDetailFragment: Boolean) {

    }

}