package com.example.ufsnavigationassistant.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.ufsnavigationassistant.R;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartNavigationActivity extends AppCompatActivity {
    MapboxNavigation navigation;

    Point origin = Point.fromLngLat(-77.03613, 38.90992);
    Point destination = Point.fromLngLat(-77.0365, 38.8977);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        //No Layout file needed

        navigation = new MapboxNavigation(getApplicationContext(), getString(R.string.mapbox_access_token));
        getRoute();
    }

    private  void getRoute() {
        NavigationRoute.builder(getApplicationContext())
                .accessToken(getString(R.string.mapbox_access_token))
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        if(response.body() == null) {
                            Toast.makeText(StartNavigationActivity.this, "No routes found, there is connection error", Toast.LENGTH_LONG).show();
                            return;
                        }else if(response.body().routes().size() < 1) {
                            Toast.makeText(StartNavigationActivity.this, "No routes found to the destination", Toast.LENGTH_LONG).show();
                            return;
                        }

                        DirectionsRoute route = response.body().routes().get(0);
                        //NavigationLauncherOptions
                        NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                                .directionsRoute(route)
                                .shouldSimulateRoute(true)
                                .build();
                        NavigationLauncher.startNavigation(StartNavigationActivity.this, options);

                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        Toast.makeText(StartNavigationActivity.this, "Error: "+t.toString(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        navigation.onDestroy();//End the navigation session
        finish();
    }
}