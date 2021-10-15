package com.example.ufsnavigationassistant.fragments.drawer

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ufsnavigationassistant.R
import com.example.ufsnavigationassistant.activities.BuildingDetailsActivity
import com.example.ufsnavigationassistant.core.BuildingAdapter
import com.example.ufsnavigationassistant.models.Building
import com.example.ufsnavigationassistant.services.BuildingService
import com.example.ufsnavigationassistant.services.ServiceBuilder
import kotlinx.android.synthetic.main.fragment_eating.*
import kotlinx.android.synthetic.main.fragment_health.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EatingFragment : Fragment(R.layout.fragment_eating),  BuildingAdapter.OnItemClickListener {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadBuildings()

    }

    override fun onItemClick(building: Building) {
        //An Intent to open building details
        val buildingIntent = Intent(context, BuildingDetailsActivity::class.java)
        buildingIntent.putExtra("building_data", building)
        startActivity(buildingIntent)
    }

    private fun loadBuildings() {
        val buildingService: BuildingService = ServiceBuilder.buildService(BuildingService::class.java)
        val requestCall: Call<List<Building>> = buildingService.getEatingPlaces()

        //Method is executed if Http response is received
        //Status code will decide if Http Response is a Success or Failure
        requestCall.enqueue(object : Callback<List<Building>> {
            override fun onResponse(call: Call<List<Building>>, response: Response<List<Building>>) {
                if(response.isSuccessful) {
                    //Get List from http response body
                    val buildingList: List<Building> = response.body()!!
                    //Log.d("List Build", buildingList.toString())
                    //Attach the list to the recycler view
                    eatingBuildingRecycler.adapter = BuildingAdapter(buildingList, this@EatingFragment)
                    eatingBuildingRecycler.layoutManager = LinearLayoutManager(context)
                    eatingBuildingRecycler.setHasFixedSize(true)

                    //Attach ItemDecorator to draw lines between list items
                    DividerItemDecoration(
                        context, // context
                        (eatingBuildingRecycler.layoutManager as LinearLayoutManager).orientation
                    ).apply {
                        // add divider item decoration to recycler view
                        // this will show divider line between items
                        eatingBuildingRecycler.addItemDecoration(this)
                    }
                } else { // Application-level failure
                    Toast.makeText(activity, "There are no buildings for this category", Toast.LENGTH_LONG).show()
                }
            }

            //Invoked in case of network error or establishing connection with the server
            //Or error creating Http Request or Error Processing Http Response
            override fun onFailure(call: Call<List<Building>>, t: Throwable) {
                Toast.makeText(activity, "Error occurred: $t", Toast.LENGTH_LONG).show()
            }
        })
    }
}