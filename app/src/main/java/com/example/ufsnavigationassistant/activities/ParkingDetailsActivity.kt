package com.example.ufsnavigationassistant.activities

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import com.example.ufsnavigationassistant.R
import com.example.ufsnavigationassistant.core.ImageSliderAdapter
import com.example.ufsnavigationassistant.core.PermissionUtils
import com.example.ufsnavigationassistant.models.Building
import com.example.ufsnavigationassistant.models.Parking
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
import com.smarteist.autoimageslider.SliderView
import kotlinx.android.synthetic.main.activity_building_details.*
import kotlinx.android.synthetic.main.activity_building_details.imageSlider
import kotlinx.android.synthetic.main.activity_building_details.tv_building_name
import kotlinx.android.synthetic.main.activity_parking_details.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ParkingDetailsActivity : AppCompatActivity() {
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 999
    }

    private lateinit var parkingName: String
    var navigation: MapboxNavigation? = null
    private var originLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(applicationContext, getString(R.string.mapbox_access_token))
        setContentView(R.layout.activity_parking_details)

        // set toolbar as support action bar and change title
        supportActionBar!!.title = "Parkings"

        //Load building data from the Intent
        val parkingData: Parking? = intent.getParcelableExtra("parking_data")

        parkingName = parkingData?.parking_name.toString() //private variable to hold parking's name
        //Assign UI elements data from Intent
        tv_parking_name.text = parkingData?.parking_name
        tv_parking_description.text = parkingData?.description

        //Images slider, get array of images for this building
        val imageList: ArrayList<String> = ArrayList()
        for (image in parkingData!!.images) {
            image.url?.let { imageList.add("http://10.0.2.2/systems/ufsnavigation/uploads/parkings/$it") }
        }
        setImageInSlider(imageList, imageSlider)

        /***************************Mapbox Navigation****************************/

        navigation = MapboxNavigation(applicationContext, getString(R.string.mapbox_access_token))
        //Car Fab Button to start turn-by-turn directions
        parking_navigation_fab.setOnClickListener {
            //Set routing profile
            val routingProfile = DirectionsCriteria.PROFILE_DRIVING

            //Build origin and destination Points for route construction
            val startPoint = Point.fromLngLat(originLocation!!.longitude, originLocation!!.latitude)
            val endPoint =
                Point.fromLngLat(parkingData.lon_coordinate, parkingData.lat_coordinate)

            //Call this function to start turn-by-turn navigation
            getRoute(startPoint, endPoint, routingProfile)
        }
    }

    private fun setImageInSlider(images: ArrayList<String>, imageSlider: SliderView) {
        val adapter = ImageSliderAdapter()
        adapter.renewItems(images)
        imageSlider.setSliderAdapter(adapter)
        imageSlider.isAutoCycle = true
        imageSlider.startAutoCycle()
    }

    private fun getRoute(origin: Point, destination: Point, routingProfile: String) {
        NavigationRoute.builder(applicationContext)
            .accessToken(getString(R.string.mapbox_access_token))
            .origin(origin)
            .destination(destination)
            .profile(routingProfile)
            .build()
            .getRoute(object : Callback<DirectionsResponse?> {
                override fun onResponse(
                    call: Call<DirectionsResponse?>,
                    response: Response<DirectionsResponse?>
                ) {
                    if (response.body() == null) {
                        Toast.makeText(
                            this@ParkingDetailsActivity,
                            "No routes found, there is connection error",
                            Toast.LENGTH_LONG
                        ).show()
                        return
                    } else if (response.body()!!.routes().size < 1) {
                        Toast.makeText(
                            this@ParkingDetailsActivity,
                            "No routes found to the destination",
                            Toast.LENGTH_LONG
                        ).show()
                        return
                    }
                    val route = response.body()!!.routes().first()
                    //NavigationLauncherOptions
                    val options = NavigationLauncherOptions.builder()
                        .directionsRoute(route)
                        .shouldSimulateRoute(true)
                        .build()
                    NavigationLauncher.startNavigation(this@ParkingDetailsActivity, options)
                }
                override fun onFailure(call: Call<DirectionsResponse?>, t: Throwable) {
                    Toast.makeText(this@ParkingDetailsActivity, "Error: $t", Toast.LENGTH_LONG).show()
                }
            })
    }

    /*************************Mapbox Location services *******************************/
    override fun onDestroy() {
        super.onDestroy()
        navigation!!.onDestroy() //End the navigation session
    }

    /****************************** Google API Location services for current Location*********************/

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