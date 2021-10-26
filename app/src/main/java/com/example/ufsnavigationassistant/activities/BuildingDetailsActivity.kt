package com.example.ufsnavigationassistant.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ufsnavigationassistant.R
import com.example.ufsnavigationassistant.core.BuildingLevelAdapter
import com.example.ufsnavigationassistant.core.ImageSliderAdapter
import com.example.ufsnavigationassistant.models.Building
import com.example.ufsnavigationassistant.models.BuildingLevel
import com.example.ufsnavigationassistant.services.BuildingService
import com.example.ufsnavigationassistant.services.ServiceBuilder
import com.mapbox.geojson.Point
import com.smarteist.autoimageslider.SliderView
import kotlinx.android.synthetic.main.activity_building_details.*
import kotlinx.android.synthetic.main.fragment_buildings.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.view.View
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute
import com.example.ufsnavigationassistant.core.PermissionUtils
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class BuildingDetailsActivity : AppCompatActivity(), BuildingLevelAdapter.OnItemClickListener {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 999
    }

    private lateinit var buildingName: String
    var navigation: MapboxNavigation? = null
    private var originLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(applicationContext, getString(R.string.mapbox_access_token))
        setContentView(R.layout.activity_building_details)

        // set toolbar as support action bar and change title
        supportActionBar!!.title = "Buildings"
        //supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //Load building data from the Intent
        val buildingData: Building? = intent.getParcelableExtra("building_data")

        buildingName = buildingData?.building_name.toString() //private variable to hold building's name
        //Assign UI elements data from Intent
        tv_building_name.text = buildingData?.building_name
        tv_building_description.text = buildingData?.description

        //Images slider, get array of images for this building
        val imageList: ArrayList<String> = ArrayList()
        for (image in buildingData!!.images) {
            image.url?.let { imageList.add("http://10.0.2.2/systems/ufsnavigation/uploads/buildings/$it") }
        }
        setImageInSlider(imageList, imageSlider)

        //Load building levels
        loadBuildingLevels(buildingData.building_id)

        /***************************Mapbox Navigation****************************/

        navigation = MapboxNavigation(applicationContext, getString(R.string.mapbox_access_token))

        //Floating Action Buttons visibility
        walk_fab.visibility = View.GONE
        car_fab.visibility = View.GONE
        var isAllFabVisible = false
        //Parent Fab Button to show other Fab buttons
        navigation_fab.setOnClickListener {
            isAllFabVisible = if (!isAllFabVisible) {
                //When isAllFabVisible becomes true make all the action FABs VISIBLE.
                walk_fab.show()
                car_fab.show()
                // make the boolean variable true as we have set the sub FABs visibility to GONE
                  true
            } else {
                //When isAllFabVisible becomes true make all the action name FABs GONE.
                walk_fab.hide()
                car_fab.hide()
                // make the boolean variable false as we have set the sub FABs visibility to GONE
                 false
            }

        }
        //Walking Fab Button to start turn-by-turn directions
        walk_fab.setOnClickListener {
            //Set routing profile
            val routingProfile = DirectionsCriteria.PROFILE_WALKING

            //Build origin and destination Points for route construction
            //val startPoint = Point.fromLngLat(26.187378421174966, -29.10737947010742)
            val startPoint = Point.fromLngLat(originLocation!!.longitude, originLocation!!.latitude)
            val endPoint = Point.fromLngLat(buildingData.lon_coordinate, buildingData.lat_coordinate)

            //Call this function to start turn-by-turn navigation
            getRoute(startPoint, endPoint, routingProfile)
        }
        //Car Fab Button to start turn-by-turn directions
        car_fab.setOnClickListener {
            //Set routing profile
            val routingProfile = DirectionsCriteria.PROFILE_DRIVING

            //Build origin and destination Points for route construction
            val startPoint = Point.fromLngLat(originLocation!!.longitude, originLocation!!.latitude)
            val endPoint =
                Point.fromLngLat(buildingData.lon_coordinate, buildingData.lat_coordinate)

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

    private fun loadBuildingLevels(buildingId: Int) {
        val buildingService: BuildingService =
            ServiceBuilder.buildService(BuildingService::class.java)
        val requestCall: Call<List<BuildingLevel>> = buildingService.getBuildingLevels(buildingId)

        //Method is executed if Http response is received
        //Status code will decide if Http Response is a Success or Failure
        requestCall.enqueue(object : Callback<List<BuildingLevel>> {
            override fun onResponse(
                call: Call<List<BuildingLevel>>,
                response: Response<List<BuildingLevel>>
            ) {
                if (response.isSuccessful) {
                    //Get List from http response body
                    val levelList: List<BuildingLevel> = response.body()!!
                    //Attach the list to the recycler view
                    levelRecyclerView.adapter =
                        BuildingLevelAdapter(levelList, this@BuildingDetailsActivity)
                    levelRecyclerView.layoutManager =
                        LinearLayoutManager(this@BuildingDetailsActivity)
                    levelRecyclerView.setHasFixedSize(true)

                    //Attach ItemDecorator to draw lines between list items
                    DividerItemDecoration(
                        this@BuildingDetailsActivity, // context
                        (levelRecyclerView.layoutManager as LinearLayoutManager).orientation
                    ).apply {
                        // add divider item decoration to recycler view
                        // this will show divider line between items
                        levelRecyclerView.addItemDecoration(this)
                    }
                } else { // Application-level failure
                    Toast.makeText(this@BuildingDetailsActivity, "This building does not have rooms", Toast.LENGTH_SHORT).show()
                }
            }

            //Invoked in case of network error or establishing connection with the server
            //Or error creating Http Request or Error Processing Http Response
            override fun onFailure(call: Call<List<BuildingLevel>>, t: Throwable) {
                Toast.makeText(
                    this@BuildingDetailsActivity,
                    "Error occurred: $t",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    override fun onItemClick(buildingLevel: BuildingLevel) {
        //An Intent to open building level rooms
        val roomsIntent = Intent(this, BuildingLevelRoomsActivity::class.java)
        roomsIntent.putExtra("building_id", buildingLevel.building_id)
        roomsIntent.putExtra("level_num", buildingLevel.floor_num)
        roomsIntent.putExtra("building_name", buildingName)
        startActivity(roomsIntent)
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
                            this@BuildingDetailsActivity,
                            "No routes found, there is connection error",
                            Toast.LENGTH_LONG
                        ).show()
                        return
                    } else if (response.body()!!.routes().size < 1) {
                        Toast.makeText(
                            this@BuildingDetailsActivity,
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
                    NavigationLauncher.startNavigation(this@BuildingDetailsActivity, options)
                }

                override fun onFailure(call: Call<DirectionsResponse?>, t: Throwable) {
                    Toast.makeText(this@BuildingDetailsActivity, "Error: $t", Toast.LENGTH_LONG).show()
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