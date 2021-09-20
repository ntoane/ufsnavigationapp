package com.example.ufsnavigationassistant

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import com.example.ufsnavigationassistant.models.Building
import com.example.ufsnavigationassistant.services.BuildingService
import com.example.ufsnavigationassistant.services.ServiceBuilder
import kotlinx.android.synthetic.main.fragment_buildings.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BuildingsFragment : Fragment(R.layout.fragment_buildings) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadBuildings()

    }

    private fun loadBuildings() {
        tv_buildings.text = "Waiting to replace this text"
        val buildingService: BuildingService =
            ServiceBuilder.buildService(BuildingService::class.java)
        val requestCall: Call<List<Building>> = buildingService.getBuildings()

        //Method is executed if Http response is received
        //Status code will decide if Http Response is a Success or Failure
        requestCall.enqueue(object : Callback<List<Building>> {
            override fun onResponse(call: Call<List<Building>>, response: Response<List<Building>>) {
                if(response.isSuccessful) {
                    val buildingList: List<Building> = response.body()!!
                    tv_buildings.text = buildingList[0].building_name
                } else { // Application-level failure
                    Toast.makeText(activity, "Failed to retrieve buildings", Toast.LENGTH_SHORT).show()
                }
            }

            //Invoked in case of network error or establishing connection with the server
            //Or error creating Http Request or Error Processing Http Response
            override fun onFailure(call: Call<List<Building>>, t: Throwable) {
                Toast.makeText(activity, "Error occurred: $t", Toast.LENGTH_SHORT).show()
            }
        })
    }
}