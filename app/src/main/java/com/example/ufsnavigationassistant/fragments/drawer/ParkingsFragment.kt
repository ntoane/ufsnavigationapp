package com.example.ufsnavigationassistant.fragments.drawer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ufsnavigationassistant.R
import com.example.ufsnavigationassistant.activities.BuildingDetailsActivity
import com.example.ufsnavigationassistant.activities.ParkingDetailsActivity
import com.example.ufsnavigationassistant.core.ParkingAdapter
import com.example.ufsnavigationassistant.models.Parking
import com.example.ufsnavigationassistant.services.ParkingService
import com.example.ufsnavigationassistant.services.ServiceBuilder
import kotlinx.android.synthetic.main.fragment_buildings.*
import kotlinx.android.synthetic.main.fragment_parkings.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ParkingsFragment : Fragment(R.layout.fragment_parkings), ParkingAdapter.OnItemClickListener{
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadParkings()

    }

    override fun onItemClick(parking: Parking) {
        //An Intent to open building details
        val buildingIntent = Intent(context, ParkingDetailsActivity::class.java)
        buildingIntent.putExtra("parking_data", parking)
        startActivity(buildingIntent)
    }

    private fun loadParkings() {
        val parkingService: ParkingService = ServiceBuilder.buildService(ParkingService::class.java)
        val requestCall: Call<List<Parking>> = parkingService.getCarParkings()

        //Method is executed if Http response is received
        //Status code will decide if Http Response is a Success or Failure
        requestCall.enqueue(object : Callback<List<Parking>> {
            override fun onResponse(call: Call<List<Parking>>, response: Response<List<Parking>>) {
                if(response.isSuccessful) {
                    //Get List from http response body
                    val parkingList: List<Parking> = response.body()!!
                    //Attach the list to the recycler view
                    parkingRecycler.adapter = ParkingAdapter(parkingList, this@ParkingsFragment)
                    parkingRecycler.layoutManager = LinearLayoutManager(context)
                    parkingRecycler.setHasFixedSize(true)

                    //Attach ItemDecorator to draw lines between list items
                    DividerItemDecoration(
                        context, // context
                        (parkingRecycler.layoutManager as LinearLayoutManager).orientation
                    ).apply {
                        // add divider item decoration to recycler view
                        // this will show divider line between items
                        parkingRecycler.addItemDecoration(this)
                    }
                } else { // Application-level failure
                    Toast.makeText(activity, "Failed to retrieve parkings", Toast.LENGTH_LONG).show()
                }
            }

            //Invoked in case of network error or establishing connection with the server
            //Or error creating Http Request or Error Processing Http Response
            override fun onFailure(call: Call<List<Parking>>, t: Throwable) {
                Toast.makeText(activity, "Error occurred: $t", Toast.LENGTH_LONG).show()
            }
        })
    }
}