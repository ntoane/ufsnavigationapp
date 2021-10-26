package com.example.ufsnavigationassistant.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.ufsnavigationassistant.MainActivity
import com.example.ufsnavigationassistant.R
import com.example.ufsnavigationassistant.core.PermissionUtils
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute
import kotlinx.android.synthetic.main.activity_start_navigation.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class StartNavigationActivity : AppCompatActivity() {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 999
    }

    var navigation: MapboxNavigation? = null
    private var originLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(applicationContext, getString(R.string.mapbox_access_token))
        setContentView(R.layout.activity_start_navigation)

        //Get data from intent
        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)
        val buildingName = intent.getStringExtra("building_name")

        // set toolbar as support action bar and change title
        supportActionBar!!.title = "Directions to $buildingName"

        //startPoint()
        //setUpLocationListener()

        navigation = MapboxNavigation(applicationContext, getString(R.string.mapbox_access_token))

        //Build origin and destination Points for route construction
        val startPoint = Point.fromLngLat(26.187378421174966, -29.10737947010742)
        //val startPoint = Point.fromLngLat(originLocation!!.longitude, originLocation!!.latitude)
        val endPoint = Point.fromLngLat(longitude, latitude)

        //Call this function to start turn-by-turn navigation
        getRoute(startPoint, endPoint)

        btn_back.setOnClickListener {
            //startActivity(Intent(this@StartNavigationActivity, MainActivity::class.java))
            finish()
        }
    }

    private fun getRoute(origin: Point, destination: Point) {
        NavigationRoute.builder(applicationContext)
            .accessToken(getString(R.string.mapbox_access_token))
            .origin(origin)
            .destination(destination)
            .profile(DirectionsCriteria.PROFILE_WALKING)
            .build()
            .getRoute(object : Callback<DirectionsResponse?> {
                override fun onResponse(
                    call: Call<DirectionsResponse?>,
                    response: Response<DirectionsResponse?>
                ) {
                    if (response.body() == null) {
                        Toast.makeText(this@StartNavigationActivity, "No routes found, there is connection error", Toast.LENGTH_LONG).show()
                        return
                    } else if (response.body()!!.routes().size < 1) {
                        Toast.makeText(this@StartNavigationActivity, "No routes found to the destination", Toast.LENGTH_LONG).show()
                        return
                    }
                    val route = response.body()!!.routes().first()
                    //NavigationLauncherOptions
                    val options = NavigationLauncherOptions.builder()
                        .directionsRoute(route)
                        .shouldSimulateRoute(true)
                        .build()
                    NavigationLauncher.startNavigation(this@StartNavigationActivity, options)
                    //Set UI elements visible
                    btn_back.visibility = View.VISIBLE
                    tv_destination_reached.visibility = View.VISIBLE
                }

                override fun onFailure(call: Call<DirectionsResponse?>, t: Throwable) {
                    Toast.makeText(this@StartNavigationActivity, "Error: $t", Toast.LENGTH_LONG).show()
                }
            })
    }

    /*************************Mapbox Location services *******************************/
    override fun onDestroy() {
        super.onDestroy()
        navigation!!.onDestroy() //End the navigation session
    }

    /****************************** Google API Location services for current Location *********************/

    @SuppressLint("MissingPermission")
    private fun setUpLocationListener() {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        // for getting the current location update after every 2 seconds with high accuracy
        val locationRequest = LocationRequest().setInterval(2000).setFastestInterval(2000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    for (location in locationResult.locations) {
                        originLocation = location
                    }
                }
            },
            Looper.myLooper()
        )
    }

    override fun onStart() {
        super.onStart()
        Log.d("onStart","Starting....")
        when {
            PermissionUtils.isAccessFineLocationGranted(this) -> {
                when {
                    PermissionUtils.isLocationEnabled(this) -> {
                        setUpLocationListener()
                    }
                    else -> {
                        PermissionUtils.showGPSNotEnabledDialog(this)
                    }
                }
            }
            else -> {
                PermissionUtils.requestAccessFineLocationPermission(
                    this,
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    when {
                        PermissionUtils.isLocationEnabled(this) -> {
                            setUpLocationListener()
                        }
                        else -> {
                            PermissionUtils.showGPSNotEnabledDialog(this)
                        }
                    }
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.location_permission_not_granted),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}