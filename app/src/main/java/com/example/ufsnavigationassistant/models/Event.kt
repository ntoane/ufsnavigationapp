package com.example.ufsnavigationassistant.models

data class Event(
    var calendar_id: Int = 0,
    var building_id: Int = 0,
    var event_name: String? = null,
    var start_time: String? = null,
    var end_time: String? = null,
    var event_date: String? = null,
    var building_name: String? = null,
    var lat_coordinate: Double = 0.0,
    var lon_coordinate: Double =0.0
    )
