package com.example.ufsnavigationassistant.services

import com.example.ufsnavigationassistant.models.AuthUser
import com.example.ufsnavigationassistant.models.Login
import com.example.ufsnavigationassistant.models.Token
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    //POST annotation for endpoint
    @POST("login/authenticate")
    fun login(@Body credentials: Login): Call<Token>

    @POST("login/auth_user")
    fun authUser(@Body token: Token): Call<AuthUser>
}