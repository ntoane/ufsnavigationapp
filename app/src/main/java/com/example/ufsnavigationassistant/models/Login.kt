package com.example.ufsnavigationassistant.models

data class Login(
    var std_number: Int = 0,
    var password: String? = null
)

data class Token(
    var message: String? = null,
    var token: String? = null,
    var std_number: Int = 0
)

data class AuthUser(
    var auth: Boolean? = null,
    var username: String? = null
)