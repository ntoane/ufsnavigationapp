package com.example.ufsnavigationassistant.fragments.bottomnav

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ufsnavigationassistant.MainActivity
import com.example.ufsnavigationassistant.R
import com.example.ufsnavigationassistant.activities.CreateTimetableActivity
import com.example.ufsnavigationassistant.activities.StartNavigationActivity
import com.example.ufsnavigationassistant.core.EventAdapter
import com.example.ufsnavigationassistant.core.PermissionUtils
import com.example.ufsnavigationassistant.core.TimetableAdapter
import com.example.ufsnavigationassistant.models.*
import com.example.ufsnavigationassistant.services.EventService
import com.example.ufsnavigationassistant.services.LoginService
import com.example.ufsnavigationassistant.services.ServiceBuilder
import com.example.ufsnavigationassistant.services.TimetableService
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation
import kotlinx.android.synthetic.main.fragment_event.*
import kotlinx.android.synthetic.main.fragment_timetable.*
import kotlinx.android.synthetic.main.timetable_list_item.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TimetableFragment : Fragment(R.layout.fragment_timetable), TimetableAdapter.OnClickListener {

    val CUSTOM_PREF_NAME = "token_data"
    private var lat: Double = 0.0
    private var lon: Double = 0.0
    private var buildingName: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = customPreference( CUSTOM_PREF_NAME)
        val readToken = prefs.getString("token","")
        val sentToken = Token()
        sentToken.token = readToken
        authUser(sentToken)

        //Timetabling loading here
        loadNextClass()
        loadTimetable()

        //Launch crete timetable activity
        timetable_navigation_fab.setOnClickListener {
            startActivity(Intent(context, CreateTimetableActivity::class.java))
        }
        //Navigate to the next class
        next_class_start_nav.setOnClickListener {
            val navigationIntent = Intent(context, StartNavigationActivity::class.java)
            navigationIntent.putExtra("latitude", lat)
            navigationIntent.putExtra("longitude", lon)
            navigationIntent.putExtra("building_name", buildingName)
            startActivity(navigationIntent)
        }
    }

    private fun customPreference(name: String): SharedPreferences =
        requireActivity().getSharedPreferences(name, Context.MODE_PRIVATE)

    private fun authUser(token: Token) {
        val loginService = ServiceBuilder.buildService(LoginService::class.java)
        val requestCall = loginService.authUser(token)

        requestCall.enqueue(object : Callback<AuthUser> {

            override fun onResponse(call: Call<AuthUser>, response: Response<AuthUser>) {
                if (response.isSuccessful) {
                    //finish() // Move back to DestinationListActivity
                    var userAuthorized = response.body() // get response
                    if(userAuthorized?.auth == true) {
                        //Toast.makeText(context, "You are Authorized", Toast.LENGTH_LONG).show()

                    } else {
                        Toast.makeText(context, "Please Login to have access to Timetable", Toast.LENGTH_LONG).show()
                        startActivity(Intent(context, MainActivity::class.java))
                    }
                } else {
                    Toast.makeText(context, "Please Login to have access to Timetable", Toast.LENGTH_LONG).show()
                    startActivity(Intent(context, MainActivity::class.java))
                }
            }

            override fun onFailure(call: Call<AuthUser>, t: Throwable) {
                Toast.makeText(context, "Authorization failure: $t", Toast.LENGTH_LONG)
                    .show()
            }
        })
    }

    override fun onItemClick(timetable: Timetable) {
        //onItemClick is implemented on button delete timetable
        val timetableService: TimetableService = ServiceBuilder.buildService(TimetableService::class.java)
        val requestCall: Call<DeleteTimetable> = timetableService.deleteTimetable(timetable.timetable_id)

        requestCall.enqueue(object : Callback<DeleteTimetable> {
            override fun onResponse(call: Call<DeleteTimetable>, response: Response<DeleteTimetable>) {
                if(response.isSuccessful) {
                    //Get List from http response body
                    val deleteResult: DeleteTimetable = response.body()!!
                    Toast.makeText(activity, "${deleteResult.message}", Toast.LENGTH_LONG).show()
                    loadTimetable()
                    loadNextClass()

                } else { // Application-level failure
                    Toast.makeText(activity, "Failed to delete the timetable entry", Toast.LENGTH_LONG).show()
                }
            }

            //Invoked in case of network error or establishing connection with the server
            //Or error creating Http Request or Error Processing Http Response
            override fun onFailure(call: Call<DeleteTimetable>, t: Throwable) {
                Toast.makeText(activity, "Error occurred: $t", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun loadTimetable() {
        val prefs = customPreference( CUSTOM_PREF_NAME)
        val stdNumber = prefs.getInt("std_number",0)

        val timetableService: TimetableService = ServiceBuilder.buildService(TimetableService::class.java)
        val requestCall: Call<List<Timetable>> = timetableService.getTimetable(stdNumber)

        //Method is executed if Http response is received
        //Status code will decide if Http Response is a Success or Failure
        requestCall.enqueue(object : Callback<List<Timetable>> {
            override fun onResponse(call: Call<List<Timetable>>, response: Response<List<Timetable>>) {
                if(response.isSuccessful) {
                    //Get List from http response body
                    val timetableList: List<Timetable> = response.body()!!
                    //Attach the list to the recycler view
                    timetableRecycler.adapter = TimetableAdapter(timetableList, this@TimetableFragment)
                    timetableRecycler.layoutManager = LinearLayoutManager(context)
                    timetableRecycler.setHasFixedSize(true)

                } else { // Application-level failure
                    Toast.makeText(activity, "There are currently no timetable entries", Toast.LENGTH_LONG).show()
                }
            }

            //Invoked in case of network error or establishing connection with the server
            //Or error creating Http Request or Error Processing Http Response
            override fun onFailure(call: Call<List<Timetable>>, t: Throwable) {
                Toast.makeText(activity, "Error occurred: $t", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun loadNextClass() {
        val prefs = customPreference( CUSTOM_PREF_NAME)
        val stdNumber = prefs.getInt("std_number",0)

        val timetableService: TimetableService = ServiceBuilder.buildService(TimetableService::class.java)
        val requestCall: Call<Timetable> = timetableService.getNextClass(stdNumber)

        //Method is executed if Http response is received
        //Status code will decide if Http Response is a Success or Failure
        requestCall.enqueue(object : Callback<Timetable> {
            override fun onResponse(call: Call<Timetable>, response: Response<Timetable>) {
                if(response.isSuccessful) {
                    //Get List from http response body
                    val nextClass: Timetable = response.body()!!
                    if(!nextClass.status) {
                        tv_next_class.text = "Next Class (Unavailable Today)"
                        next_class_card.visibility = View.GONE
                        Toast.makeText(activity, "You do not have next classes today", Toast.LENGTH_LONG).show()
                    } else {
                        //Update UI elements for next class
                        next_course_name.text = nextClass.module_code
                        next_timetable_room.text = nextClass.room_name
                        next_timetable_building.text = nextClass.building_name
                        next_timetable_day.text = nextClass.day
                        next_timetable_start_time.text = nextClass.start_time
                        next_timetable_end_time.text = nextClass.end_time

                        //Update coordinates
                        lat = nextClass.lat_coordinate
                        lon = nextClass.lon_coordinate
                        buildingName = nextClass.building_name
                    }

                } else { // Application-level failure
                    tv_next_class.text = "Next Class (Unavailable Today)"
                    next_class_card.visibility = View.GONE
                    Toast.makeText(activity, "You do not have next classes today", Toast.LENGTH_LONG).show()
                }
            }

            //Invoked in case of network error or establishing connection with the server
            //Or error creating Http Request or Error Processing Http Response
            override fun onFailure(call: Call<Timetable>, t: Throwable) {
                Toast.makeText(activity, "Error occurred: $t", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onResume() {// Is used to load new timetable entries
        super.onResume()

        loadNextClass()
        loadTimetable()
    }
}