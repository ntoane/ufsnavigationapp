package com.example.ufsnavigationassistant.services

import retrofit2.Call
import com.example.ufsnavigationassistant.models.Building
import retrofit2.http.GET

interface BuildingService {

    //GET annotation for endpoint to get buildings
    @GET("building/buildings")
    fun getBuildings(): Call<List<Building>>
}