package com.example.ufsnavigationassistant.services

import retrofit2.Call
import com.example.ufsnavigationassistant.models.Building
import com.example.ufsnavigationassistant.models.BuildingLevel
import com.example.ufsnavigationassistant.models.LevelRoom
import com.example.ufsnavigationassistant.models.Parking
import retrofit2.http.GET
import retrofit2.http.Path

interface ParkingService {

    //GET annotation for endpoint to get buildings
    @GET("parking/car_parkings")
    fun getCarParkings(): Call<List<Parking>>

    @GET("parking/wheelchair_parkings")
    fun getWheelchairParkings(): Call<List<Parking>>
}