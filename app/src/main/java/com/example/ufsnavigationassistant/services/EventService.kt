package com.example.ufsnavigationassistant.services

import com.example.ufsnavigationassistant.models.*
import retrofit2.Call
import retrofit2.http.GET

interface EventService {

    //GET annotation for endpoint to get buildings
    @GET("event/upcoming")
    fun getEvents(): Call<List<Event>>
}