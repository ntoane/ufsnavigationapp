package com.example.ufsnavigationassistant.core;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.concurrent.Executor;

public class CurrentLocation extends AppCompatActivity {
    private Context context;
    private Location currentLocation;

    //For GPS
    public static final int DEFAULT_UPDATE_INTERVAL = 30;
    public static final int FAST_UPDATE_INTERVAL = 5;
    private static final int PERMISSION_FINE_LOCATION = 99;

    //Location request is a config file for all settings related to FusedLocationProviderClient
    LocationRequest locationRequest;
    //Google API for location services. The majority of the app function using this class
    FusedLocationProviderClient fusedLocationProviderClient;

    //Create constructor
    public CurrentLocation(Context context) {
        this.context = context;
        //Set all properties of LocationRequest
        locationRequest = new LocationRequest();
        //How often does the default location check occur
        locationRequest.setInterval(100 * DEFAULT_UPDATE_INTERVAL);
        //How often does the location check occur when set to the most frequent update
        locationRequest.setFastestInterval(100 * FAST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        getGPSCoordinates();
    }

    private void getGPSCoordinates() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // user provided the permission
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener( this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // we got permission. Assign the current location
                    currentLocation = location;
                }
            });
        } else {
            // permission not granted
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getGPSCoordinates();
            } else {
                Toast.makeText(context, "This App requires permission to be granted in order to work properly", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }
}
