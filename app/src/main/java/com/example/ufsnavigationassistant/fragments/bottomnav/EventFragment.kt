package com.example.ufsnavigationassistant.fragments.bottomnav

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ufsnavigationassistant.R
import com.example.ufsnavigationassistant.core.EventAdapter
import com.example.ufsnavigationassistant.models.Event
import com.example.ufsnavigationassistant.services.EventService
import com.example.ufsnavigationassistant.services.ServiceBuilder
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation
import kotlinx.android.synthetic.main.fragment_buildings.*
import kotlinx.android.synthetic.main.fragment_event.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.ufsnavigationassistant.activities.StartNavigationActivity


class EventFragment : Fragment(R.layout.fragment_event), EventAdapter.OnClickListener {
    var navigation: MapboxNavigation? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadEvents()
    }

    override fun onItemClick(event: Event) {
        val navigationIntent = Intent(context, StartNavigationActivity::class.java)
        navigationIntent.putExtra("latitude", event.lat_coordinate)
        navigationIntent.putExtra("longitude", event.lon_coordinate)
        navigationIntent.putExtra("building_name", event.building_name)
        startActivity(navigationIntent)
    }

    private fun loadEvents() {
        val eventService: EventService = ServiceBuilder.buildService(EventService::class.java)
        val requestCall: Call<List<Event>> = eventService.getEvents()

        //Method is executed if Http response is received
        //Status code will decide if Http Response is a Success or Failure
        requestCall.enqueue(object : Callback<List<Event>> {
            override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                if(response.isSuccessful) {
                    //Get List from http response body
                    val eventsList: List<Event> = response.body()!!
                    //Attach the list to the recycler view
                    eventRecycler.adapter = EventAdapter(eventsList, this@EventFragment)
                    eventRecycler.layoutManager = LinearLayoutManager(context)
                    eventRecycler.setHasFixedSize(true)

                } else { // Application-level failure
                    Toast.makeText(activity, "There are currently no upcoming events", Toast.LENGTH_LONG).show()
                }
            }

            //Invoked in case of network error or establishing connection with the server
            //Or error creating Http Request or Error Processing Http Response
            override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                Toast.makeText(activity, "Error occurred: $t", Toast.LENGTH_LONG).show()
            }
        })
    }
}
