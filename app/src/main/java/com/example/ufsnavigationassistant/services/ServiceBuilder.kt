package com.example.ufsnavigationassistant.services

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceBuilder {

    //Base URL
    private const val URL = "http://10.0.2.2/systems/ufsnavigation/api/"

    //Create Logger
    private val logger = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    //Create OkHttp Client and Add interceptor
    private val okHttp: OkHttpClient.Builder = OkHttpClient.Builder().addInterceptor(logger)

    //Create Retrofit Builder
    private val builder: Retrofit.Builder = Retrofit.Builder().baseUrl(URL)
        .addConverterFactory(GsonConverterFactory.create()) //integrate Json conversion with retrofit
        .client(okHttp.build()) //attach client

    //Create Retrofit Instance
    private val retrofit: Retrofit = builder.build()

    fun <T> buildService(serviceType: Class<T>): T {
        return retrofit.create(serviceType)
    }
}