package com.example.ufsnavigationassistant.models


data class Timetable(
    var timetable_id: Int = 0,
    var room_id: Int = 0,
    var std_number: Int = 0,
    var module_code: String? = null,
    var start_time: String? = null,
    var end_time: String? = null,
    var day: String? = null,
    var room_name: String? = null,
    var level_num: String? = null,
    var building_name: String? = null,
    var lat_coordinate: Double = 0.0,
    var lon_coordinate: Double = 0.0,
    var status: Boolean
)

data class CreateTimetable(
    var std_number: Int = 0,
    var room_id: Int = 0,
    var module_code: String? = null,
    var day: String? = null,
    var start_time: String? = null,
    var end_time: String? = null,
    var token: String? = null
)

data class DeleteTimetable(
    var message: String? = null
)

data class ModuleCode(
    var status: Boolean? = null,
    var module_code: String? = null
)
