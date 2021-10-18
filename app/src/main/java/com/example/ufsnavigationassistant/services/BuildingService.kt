package com.example.ufsnavigationassistant.services

import retrofit2.Call
import com.example.ufsnavigationassistant.models.Building
import com.example.ufsnavigationassistant.models.BuildingLevel
import com.example.ufsnavigationassistant.models.LevelRoom
import com.example.ufsnavigationassistant.models.RoomDirection
import retrofit2.http.GET
import retrofit2.http.Path

interface BuildingService {

    //GET annotation for endpoint to get buildings
    @GET("building/buildings")
    fun getBuildings(): Call<List<Building>>

    @GET("building/building_levels/{building_id}")
    fun getBuildingLevels(@Path("building_id") building_id: Int): Call<List<BuildingLevel>>

    @GET("building/building_levels_rooms/{building_id}/{floor_num}")
    fun getBuildingLevelRooms(
        @Path("building_id") building_id: Int,
        @Path("floor_num") floor_num: Int
    ): Call<List<LevelRoom>>

    @GET("building/building_levels_toilets/{building_id}/{floor_num}")
    fun getBuildingLevelToilets(
        @Path("building_id") building_id: Int,
        @Path("floor_num") floor_num: Int
    ): Call<List<LevelRoom>>

    //Get other buildings
    @GET("building/health_services")
    fun getHealthServices(): Call<List<Building>>

    @GET("building/eating_places")
    fun getEatingPlaces(): Call<List<Building>>

    @GET("building/room_directions//{room_id}")
    fun getRoomDirections(@Path("room_id") room_id: Int): Call<List<RoomDirection>>
}