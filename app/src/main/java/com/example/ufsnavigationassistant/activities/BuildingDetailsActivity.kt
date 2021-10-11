package com.example.ufsnavigationassistant.activities

import android.content.Intent
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ufsnavigationassistant.R
import com.example.ufsnavigationassistant.core.BuildingLevelAdapter
import com.example.ufsnavigationassistant.core.ImageSliderAdapter
import com.example.ufsnavigationassistant.models.Building
import com.example.ufsnavigationassistant.models.BuildingLevel
import com.example.ufsnavigationassistant.models.LevelRoom
import com.example.ufsnavigationassistant.services.BuildingService
import com.example.ufsnavigationassistant.services.ServiceBuilder
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
/*import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute*/
import com.smarteist.autoimageslider.SliderView
import kotlinx.android.synthetic.main.activity_building_details.*
import kotlinx.android.synthetic.main.fragment_buildings.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.core.content.ContextCompat
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute
import android.location.LocationManager





class BuildingDetailsActivity : AppCompatActivity(), BuildingLevelAdapter.OnItemClickListener,
    PermissionsListener {

    private var permissionsManager: PermissionsManager = PermissionsManager(this)
    private lateinit var buildingName: String
    private var currentRoute: DirectionsRoute? = null
    private var navigationMapRoute: NavigationMapRoute? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))
        setContentView(R.layout.activity_building_details)

        // set toolbar as support action bar and change title
        supportActionBar!!.title = "Buildings"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //Load building data from the Intent
        val buildingData: Building? = intent.getParcelableExtra("building_data")

        buildingName =
            buildingData?.building_name.toString() //private variable to hold building's name
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

        //Request permission
        requestPermission()
        //Fab Button to start turn-by-turn directions
        navigation_fab.setOnClickListener {
            //Get route first
            val location = Location(LocationManager.GPS_PROVIDER)
            location.latitude = 23.5678
            location.longitude = 34.456
            val start = Location(LocationManager.GPS_PROVIDER)
            val end = Location(LocationManager.GPS_PROVIDER)
            start.longitude = -29.107234473985358
            start.latitude = 26.187358635582477
            end.longitude = -29.108502714940844
            end.latitude = 26.187501910941766
            val originPoint = Point.fromLngLat(start.longitude, start.latitude);
            val destinationPoint = Point.fromLngLat(end.longitude, end.latitude)
            getRoute(originPoint, destinationPoint)
            Log.d("Passed", "Passed getRoute")
            //Open turn-by-turn
            if (currentRoute != null) {
                Log.d("Inside", "Inside currentRoute")
                Log.d("CurrentRoute:",currentRoute.toString())
                val navigationLauncherOptions = NavigationLauncherOptions.builder() //1
                    .directionsRoute(currentRoute) //2
                    .shouldSimulateRoute(true) //3
                    .build()
                NavigationLauncher.startNavigation(this, navigationLauncherOptions) //4
            }else {
                Log.d("CRoute", "Current route empty")
            }
            /*val navigationIntent = Intent(this@BuildingDetailsActivity, BuildingNavigationActivity::class.java)
            navigationIntent.putExtra("building_data", buildingData)
            startActivity(navigationIntent)*/
            //Toast.makeText(this@BuildingDetailsActivity, "Will start turn-by-turn directions to ${buildingData.building_name} building", Toast.LENGTH_LONG).show()
        }

    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        //TODO("Not yet implemented")
    }

    override fun onPermissionResult(granted: Boolean) {
        //TODO("Not yet implemented")
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
                    Toast.makeText(
                        this@BuildingDetailsActivity,
                        "Failed to retrieve rooms",
                        Toast.LENGTH_SHORT
                    ).show()
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

    private fun getRoute(originPoint: Point, endPoint: Point) {
        NavigationRoute.builder(this) //1
            .accessToken(Mapbox.getAccessToken()!!) //2
            .origin(originPoint) //3
            .destination(endPoint) //4
            .build() //5
            .getRoute(object : Callback<DirectionsResponse> { //6
                override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                    Log.d("RouteFail", t.localizedMessage)
                }

                override fun onResponse(
                    call: Call<DirectionsResponse>,
                    response: Response<DirectionsResponse>
                ) {
                    /*if (navigationMapRoute != null) {
                        navigationMapRoute?.updateRouteVisibilityTo(false)
                    } else {
                        navigationMapRoute = NavigationMapRoute(null, mapView, map)
                    }*/

                    currentRoute = response.body()?.routes()?.first()
                    if (currentRoute != null) {
                        Log.d("ResponseRoute", currentRoute.toString())
                        navigationMapRoute?.addRoute(currentRoute)
                    }

                }
            })
    }

    private  fun requestPermission() {
        if (!PermissionsManager.areLocationPermissionsGranted(this)) {
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(this)
        }
    }
}