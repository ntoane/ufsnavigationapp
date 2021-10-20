package com.example.ufsnavigationassistant.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.ufsnavigationassistant.MainActivity
import com.example.ufsnavigationassistant.models.Login
import com.example.ufsnavigationassistant.models.Token
import com.example.ufsnavigationassistant.services.LoginService
import com.example.ufsnavigationassistant.services.ServiceBuilder
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {
    val CUSTOM_PREF_NAME = "token_data"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.ufsnavigationassistant.R.layout.activity_login)

        if (et_username.text != null && et_password.text != null) {
            btn_login.setOnClickListener {
                val newLogin = Login()
                newLogin.std_number = et_username.text.toString().toInt()
                newLogin.password = et_password.text.toString()

                val loginService = ServiceBuilder.buildService(LoginService::class.java)
                val requestCall = loginService.login(newLogin)

                requestCall.enqueue(object : Callback<Token> {

                    override fun onResponse(call: Call<Token>, response: Response<Token>) {
                        if (response.isSuccessful) {
                            //finish() // Move back to DestinationListActivity
                            var tokenResponse = response.body() // get response
                            Toast.makeText(
                                this@LoginActivity,
                                "${tokenResponse?.message}",
                                Toast.LENGTH_LONG
                            ).show()

                            //Store token data to shared preference
                            val prefs = customPreference(this@LoginActivity, CUSTOM_PREF_NAME)
                            prefs.edit().remove("token").apply()//delete key before adding new key on login

                            var editor = prefs.edit()
                            editor.putString("token",tokenResponse?.token)
                            editor.apply()

                            //Go to MainActivity screen
                            val homeIntent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(homeIntent);
                            finish()
                        } else {
                            Toast.makeText(this@LoginActivity, "Login failure", Toast.LENGTH_LONG)
                                .show()
                        }
                    }

                    override fun onFailure(call: Call<Token>, t: Throwable) {
                        Toast.makeText(this@LoginActivity, "Login failure: $t", Toast.LENGTH_LONG)
                            .show()
                    }
                })
            }
        }
    }

    fun customPreference(context: Context, name: String): SharedPreferences =
        context.getSharedPreferences(name, Context.MODE_PRIVATE)
}