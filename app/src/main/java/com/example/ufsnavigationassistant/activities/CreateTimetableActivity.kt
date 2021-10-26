package com.example.ufsnavigationassistant.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.ufsnavigationassistant.R
import kotlinx.android.synthetic.main.activity_create_timetable.*
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.SpinnerAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ufsnavigationassistant.core.TimetableAdapter
import com.example.ufsnavigationassistant.models.CreateTimetable
import com.example.ufsnavigationassistant.models.ModuleCode
import com.example.ufsnavigationassistant.services.ServiceBuilder
import com.example.ufsnavigationassistant.services.TimetableService
import kotlinx.android.synthetic.main.fragment_timetable.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class CreateTimetableActivity : AppCompatActivity() {

    private val CUSTOM_PREF_NAME = "token_data"
    private lateinit var day: String
    //private var moduleCode: String? =null

    private var moduleCodes: List<ModuleCode> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_timetable)

        // set toolbar as support action bar and change title
        supportActionBar!!.title = "Timetable"

        //Retrieve module codes
        //getModuleCodes()
        //Log.e("Module: ",moduleCodes.toString())
        // access the items of the list
        val days = resources.getStringArray(R.array.Days)
        // access the spinner
        if (day_spinner != null) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, days)
            day_spinner.adapter = adapter

            day_spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    day = days[position]
                    /*Toast.makeText(this@CreateTimetableActivity,  "Item selected: " +
                                "" + days[position], Toast.LENGTH_LONG).show()*/
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }

        //Set start and end time from time picker
        et_start_time.setOnClickListener{
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                et_start_time.setText(SimpleDateFormat("HH:mm").format(cal.time))
            }
            TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }
        et_end_time.setOnClickListener{
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                et_end_time.setText(SimpleDateFormat("HH:mm").format(cal.time))
            }
            TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }
        
        //Submit button to create timetable entry
        btn_create_timetable.setOnClickListener {
            // Get student number
            val prefs = customPreference( CUSTOM_PREF_NAME)
            val stdNumber = prefs.getInt("std_number",0)
            val token = prefs.getString("token","")
            //Initialize views
            val roomId = et_room_name.text.trim()
            val moduleCode = et_module_code.text.trim()
            val startTime = et_start_time.text.trim()
            val endTime = et_end_time.text.trim()

            if(stdNumber > 0 && roomId.isNotEmpty() && moduleCode.isNotEmpty() && day.isNotEmpty() && startTime.isNotEmpty() && endTime.isNotEmpty()) {
                createTimetable(stdNumber, roomId.toString().toInt(), moduleCode.toString(), day, startTime.toString(), endTime.toString(), token!!)
            }else {
                Toast.makeText(this, "Please select all the fields", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun createTimetable(stdNumber: Int, roomId: Int, moduleCode: String, day: String, startTime: String, endTime: String, token: String) {
        //Create new timetable object
        val newTimetable = CreateTimetable()
        newTimetable.std_number = stdNumber
        newTimetable.room_id = roomId
        newTimetable.module_code = moduleCode
        newTimetable.day = day
        newTimetable.start_time = startTime
        newTimetable.end_time = endTime
        newTimetable.token = token

        val timetableService: TimetableService = ServiceBuilder.buildService(TimetableService::class.java)
        val requestCall: Call<CreateTimetable> = timetableService.createTimetable(newTimetable)

        requestCall.enqueue(object : Callback<CreateTimetable> {
            override fun onResponse(call: Call<CreateTimetable>, response: Response<CreateTimetable>) {
                if(response.isSuccessful) {
                    finish() //Move back to previous Activity
                    //We can get the created timetable entry
                    val timetable: CreateTimetable = response.body()!!
                    Toast.makeText(this@CreateTimetableActivity, "Timetable entry added successfully", Toast.LENGTH_SHORT).show()

                } else { // Application-level failure
                    Toast.makeText(this@CreateTimetableActivity, "Failed to create timetable entry", Toast.LENGTH_LONG).show()
                }
            }

            //Invoked in case of network error or establishing connection with the server
            //Or error creating Http Request or Error Processing Http Response
            override fun onFailure(call: Call<CreateTimetable>, t: Throwable) {
                Toast.makeText(this@CreateTimetableActivity, "Error occurred: $t", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun customPreference(name: String): SharedPreferences =
        this.getSharedPreferences(name, Context.MODE_PRIVATE)


    private fun getModuleCodes(){
        val timetableService: TimetableService = ServiceBuilder.buildService(TimetableService::class.java)
        val requestCall: Call<List<ModuleCode>> = timetableService.getModuleCodes()

        requestCall.enqueue(object : Callback<List<ModuleCode>> {
            override fun onResponse(call: Call<List<ModuleCode>>, response: Response<List<ModuleCode>>) {
                if(response.isSuccessful) {
                    //Get List from http response body
                    val modules: List<ModuleCode> = response.body()!!
                    setModuleCode(modules)
                    //Log.e("Codes: ", moduleCodes.toString())

                } else { // Application-level failure
                    Toast.makeText(this@CreateTimetableActivity, "There are currently modules available", Toast.LENGTH_LONG).show()
                }
            }

            //Invoked in case of network error or establishing connection with the server
            //Or error creating Http Request or Error Processing Http Response
            override fun onFailure(call: Call<List<ModuleCode>>, t: Throwable) {
                Toast.makeText(this@CreateTimetableActivity, "Error occurred: $t", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun setModuleCode(modules: List<ModuleCode>) {

        moduleCodes = modules
    }
}