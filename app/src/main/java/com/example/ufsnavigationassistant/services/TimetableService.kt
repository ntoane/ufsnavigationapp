package com.example.ufsnavigationassistant.services

import com.example.ufsnavigationassistant.models.*
import retrofit2.Call
import retrofit2.http.*

interface TimetableService {

    //POST annotation to create new timetable entry
    @POST("timetable/create_timetable")
    fun createTimetable(@Body newTimetable: CreateTimetable): Call<CreateTimetable>

    //GET annotation for endpoint to get buildings
    @GET("timetable/student_timetable/{std_number}")
    fun getTimetable(@Path("std_number") std_number: Int): Call<List<Timetable>>

    @GET("timetable/next_class/{std_number}")
    fun getNextClass(@Path("std_number") std_number: Int): Call<Timetable>

    @DELETE("timetable/delete_timetable_entry/{timetable_id}")
    fun deleteTimetable(@Path("timetable_id") timetable_id: Int): Call<DeleteTimetable>

    @GET("timetable/module_codes")
    fun getModuleCodes(): Call<List<ModuleCode>>
}