package com.example.ufsnavigationassistant.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ufsnavigationassistant.R
import com.example.ufsnavigationassistant.core.BuildingLevelAdapter
import com.example.ufsnavigationassistant.core.LevelRoomAdapter
import com.example.ufsnavigationassistant.models.Building
import com.example.ufsnavigationassistant.models.BuildingLevel
import com.example.ufsnavigationassistant.models.LevelRoom
import com.example.ufsnavigationassistant.services.BuildingService
import com.example.ufsnavigationassistant.services.ServiceBuilder
import kotlinx.android.synthetic.main.activity_building_details.*
import kotlinx.android.synthetic.main.activity_building_level_rooms.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BuildingLevelRoomsActivity : AppCompatActivity(), LevelRoomAdapter.OnItemClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_building_level_rooms)

        //Load rooms data from the Intent
        val buildingId = intent.getIntExtra("building_id", 0)
        val levelNum = intent.getIntExtra("level_num", 0)
        val buildingName = intent.getStringExtra("building_name")

        // set toolbar as support action bar and change title
        supportActionBar!!.title = "Level $levelNum($buildingName)"
        //supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //Load rooms and send to adapter
        loadBuildingLevelRooms(buildingId, levelNum)
        loadBuildingLevelToilets(buildingId, levelNum)
    }

    private fun loadBuildingLevelRooms(buildingId: Int, levelNum: Int) {
        val buildingService: BuildingService = ServiceBuilder.buildService(BuildingService::class.java)
        val requestCall: Call<List<LevelRoom>> = buildingService.getBuildingLevelRooms(buildingId, levelNum)

        //Method is executed if Http response is received
        //Status code will decide if Http Response is a Success or Failure
        requestCall.enqueue(object : Callback<List<LevelRoom>> {
            override fun onResponse(call: Call<List<LevelRoom>>, response: Response<List<LevelRoom>>) {
                if(response.isSuccessful) {
                    //Get List from http response body
                    val roomList: List<LevelRoom> = response.body()!!
                    //Attach the list to the recycler view
                    rooms_recyclerView.adapter = LevelRoomAdapter(roomList, this@BuildingLevelRoomsActivity)
                    rooms_recyclerView.layoutManager = LinearLayoutManager(this@BuildingLevelRoomsActivity)
                    rooms_recyclerView.setHasFixedSize(true)

                    //Attach ItemDecorator to draw lines between list items
                    DividerItemDecoration(
                        this@BuildingLevelRoomsActivity, // context
                        (rooms_recyclerView.layoutManager as LinearLayoutManager).orientation
                    ).apply {
                        // add divider item decoration to recycler view
                        // this will show divider line between items
                        rooms_recyclerView.addItemDecoration(this)
                    }
                } else { // Application-level failure
                    //Toast.makeText(this@BuildingLevelRoomsActivity, "Failed to retrieve rooms", Toast.LENGTH_SHORT).show()
                }
            }

            //Invoked in case of network error or establishing connection with the server
            //Or error creating Http Request or Error Processing Http Response
            override fun onFailure(call: Call<List<LevelRoom>>, t: Throwable) {
                Toast.makeText(this@BuildingLevelRoomsActivity, "Error occurred: $t", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun loadBuildingLevelToilets(buildingId: Int, levelNum: Int) {
        val buildingService: BuildingService = ServiceBuilder.buildService(BuildingService::class.java)
        val requestCall: Call<List<LevelRoom>> = buildingService.getBuildingLevelToilets(buildingId, levelNum)

        //Method is executed if Http response is received
        //Status code will decide if Http Response is a Success or Failure
        requestCall.enqueue(object : Callback<List<LevelRoom>> {
            override fun onResponse(call: Call<List<LevelRoom>>, response: Response<List<LevelRoom>>) {
                if(response.isSuccessful) {
                    //Get List from http response body
                    val roomList: List<LevelRoom> = response.body()!!
                    //Attach the list to the recycler view
                    toilets_recyclerView.adapter = LevelRoomAdapter(roomList, this@BuildingLevelRoomsActivity)
                    toilets_recyclerView.layoutManager = LinearLayoutManager(this@BuildingLevelRoomsActivity)
                    toilets_recyclerView.setHasFixedSize(true)

                    //Attach ItemDecorator to draw lines between list items
                    DividerItemDecoration(
                        this@BuildingLevelRoomsActivity, // context
                        (toilets_recyclerView.layoutManager as LinearLayoutManager).orientation
                    ).apply {
                        // add divider item decoration to recycler view
                        // this will show divider line between items
                        toilets_recyclerView.addItemDecoration(this)
                    }
                } else { // Application-level failure of NOT returning any list
                    list_toilets.visibility = View.INVISIBLE //make toilets textview invisible
                    //Toast.makeText(this@BuildingLevelRoomsActivity, "Failed to retrieve toilets", Toast.LENGTH_SHORT).show()
                }
            }

            //Invoked in case of network error or establishing connection with the server
            //Or error creating Http Request or Error Processing Http Response
            override fun onFailure(call: Call<List<LevelRoom>>, t: Throwable) {
                Toast.makeText(this@BuildingLevelRoomsActivity, "Error occurred: $t", Toast.LENGTH_LONG).show()
            }
        })
    }


    override fun onItemClick(room: LevelRoom) {
        val directionIntent = Intent(this, RoomDirectionActivity::class.java)
        directionIntent.putExtra("room", room)
        startActivity(directionIntent)
        //Toast.makeText(this@BuildingLevelRoomsActivity, " clicked", Toast.LENGTH_LONG).show()
        //Log.d("Room", room.room_name.toString()+" clicked")
    }

}
