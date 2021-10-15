package com.example.ufsnavigationassistant

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

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
                R.id.accountFragment,
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

    }

    //open drawer when drawer icon clicked and back btn press
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}