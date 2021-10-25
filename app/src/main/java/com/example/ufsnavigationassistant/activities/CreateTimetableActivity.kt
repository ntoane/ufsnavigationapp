package com.example.ufsnavigationassistant.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.ufsnavigationassistant.R
import kotlinx.android.synthetic.main.activity_create_timetable.*

class CreateTimetableActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_timetable)

        // access the items of the list
        val days = resources.getStringArray(R.array.Days)

        // access the spinner
        //val spinner = findViewById<Spinner>(R.id.spinner)
        if (day_spinner != null) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, days)
            day_spinner.adapter = adapter

            day_spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    Toast.makeText(this@CreateTimetableActivity,  "Item selected: " +
                                "" + days[position], Toast.LENGTH_LONG).show()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }
    }
}