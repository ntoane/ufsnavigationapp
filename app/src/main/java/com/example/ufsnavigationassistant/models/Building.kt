package com.example.ufsnavigationassistant.models

import java.util.*

data class Building(
    var building_id: Int = 0,
    var category_id: Int = 0,
    var building_name: String? = null,
    var lat_coordinate: Double = 0.0,
    var lon_coordinate: Double = 0.0,
    var description: String? = null,
    var created_at: Date? = null
    )
