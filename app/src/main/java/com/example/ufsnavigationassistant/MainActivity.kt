package com.example.ufsnavigationassistant

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.*
import com.example.ufsnavigationassistant.activities.LoginActivity
import com.example.ufsnavigationassistant.models.AuthUser
import com.example.ufsnavigationassistant.models.Token
import com.example.ufsnavigationassistant.services.LoginService
import com.example.ufsnavigationassistant.services.ServiceBuilder
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var loginBtn: Button
    private lateinit var logoutBtn: Button
    private lateinit var username: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()

        //For destinations navigation
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.whereFragment,
                R.id.eventFragment,
                R.id.timetableFragment,
                R.id.buildingsFragment,
                R.id.parkingsFragment,
                R.id.healthFragment,
                R.id.eatingFragment,
            ),
            drawer_layout
        )
        //connect nav_graph to navigation and drawer viewer
        bottom_navigatin_view.setupWithNavController(navController)
        drawer_navigation_view.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)

        //get view from navigation drawer header and add onClickListener
        val navigationView = findViewById<View>(R.id.drawer_navigation_view) as NavigationView
        val headerView = navigationView.getHeaderView(0)

        loginBtn = headerView.findViewById(R.id.login_launch_btn)
        logoutBtn = headerView.findViewById(R.id.logout_launch_btn)
        username = headerView.findViewById(R.id.username)

        loginBtn.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }

        //Change drawer header text if user logged in
        val sharedPreferences: SharedPreferences = this.getSharedPreferences("token_data",
            Context.MODE_PRIVATE)
        val readToken = sharedPreferences.getString("token","")
        val sentToken = Token()
        sentToken.token = readToken
        authUser(sentToken)

        logoutBtn.setOnClickListener {
            //delete key before adding new key on login
            sharedPreferences.edit().remove("token").apply()
            sharedPreferences.edit().remove("std_number").apply()
            finish()
            startActivity(intent)
            Toast.makeText(this, "You are logged out successfully", Toast.LENGTH_SHORT).show()
        }
    }

    //open drawer when drawer icon clicked and back btn press
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun authUser(token: Token) {
        Log.e("Token: ", token.toString())
        val loginService = ServiceBuilder.buildService(LoginService::class.java)
        val requestCall = loginService.authUser(token)

        requestCall.enqueue(object : Callback<AuthUser> {

            override fun onResponse(call: Call<AuthUser>, response: Response<AuthUser>) {
                if (response.isSuccessful) {
                    //finish() // Move back to DestinationListActivity
                    var userAuthorized = response.body() // get response
                    if(userAuthorized?.auth == true) {
                        //Hide login button and display student's name
                        username.text = userAuthorized.username
                        loginBtn.visibility = View.GONE
                        logoutBtn.visibility = View.VISIBLE
                        username.visibility = View.VISIBLE
                    } else {
                        loginBtn.visibility = View.VISIBLE
                        logoutBtn.visibility = View.GONE
                        username.visibility = View.GONE
                    }
                } else {
                    loginBtn.visibility = View.VISIBLE
                    logoutBtn.visibility = View.GONE
                    username.visibility = View.GONE
                    //Toast.makeText(this@MainActivity, "User Unauthorized", Toast.LENGTH_LONG)
                        //.show()
                }
            }

            override fun onFailure(call: Call<AuthUser>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Login failure: $t", Toast.LENGTH_LONG)
                    .show()
            }
        })

    }
}