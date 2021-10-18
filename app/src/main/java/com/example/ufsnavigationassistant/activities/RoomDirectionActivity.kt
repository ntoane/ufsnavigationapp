package com.example.ufsnavigationassistant.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ufsnavigationassistant.R
import com.example.ufsnavigationassistant.core.LevelRoomAdapter
import com.example.ufsnavigationassistant.core.RoomDirectionAdapter
import com.example.ufsnavigationassistant.models.LevelRoom
import com.example.ufsnavigationassistant.models.RoomDirection
import com.example.ufsnavigationassistant.services.BuildingService
import com.example.ufsnavigationassistant.services.ServiceBuilder
import kotlinx.android.synthetic.main.activity_building_level_rooms.*
import kotlinx.android.synthetic.main.activity_room_direction.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RoomDirectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_direction)

        //Load Room data from Intent
        val room: LevelRoom? = intent.getParcelableExtra("room")
        // set toolbar as support action bar and change title
        supportActionBar!!.title = "Directions to ${room?.room_name}"

        //Load directions to the room
        loadRoomDirections(room!!.room_id)
    }

    private fun loadRoomDirections(roomId: Int) {
        val buildingService: BuildingService = ServiceBuilder.buildService(BuildingService::class.java)
        val requestCall: Call<List<RoomDirection>> = buildingService.getRoomDirections(roomId)

        //Method is executed if Http response is received
        //Status code will decide if Http Response is a Success or Failure
        requestCall.enqueue(object : Callback<List<RoomDirection>> {
            override fun onResponse(call: Call<List<RoomDirection>>, response: Response<List<RoomDirection>>) {
                if(response.isSuccessful) {
                    //Get List from http response body
                    val roomDirections: List<RoomDirection> = response.body()!!
                    //Attach the list to the recycler view
                    direction_recyclerView.adapter = RoomDirectionAdapter(roomDirections)
                    direction_recyclerView.layoutManager = LinearLayoutManager(this@RoomDirectionActivity)
                    direction_recyclerView.setHasFixedSize(true)

                } else { // Application-level failure
                    Toast.makeText(this@RoomDirectionActivity, "No directions available to this room", Toast.LENGTH_LONG).show()
                }
            }

            //Invoked in case of network error or establishing connection with the server
            //Or error creating Http Request or Error Processing Http Response
            override fun onFailure(call: Call<List<RoomDirection>>, t: Throwable) {
                Toast.makeText(this@RoomDirectionActivity, "Error occurred: $t", Toast.LENGTH_LONG).show()
            }
        })
    }
}