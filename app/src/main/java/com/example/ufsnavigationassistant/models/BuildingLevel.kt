package com.example.ufsnavigationassistant.models

data class BuildingLevel(
    var room_id: Int = 0,
    var building_id: Int = 0,
    var room_name: String? = null,
    var floor_num: Int = 0,
    var num_room: Int =0
)
