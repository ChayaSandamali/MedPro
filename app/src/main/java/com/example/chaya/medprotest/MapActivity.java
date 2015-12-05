package com.example.chaya.medprotest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

public class MapActivity extends FragmentActivity {
    /*google map*/
    private GoogleMap googleMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        try {
    /*loading the map*/
            initilizeMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String option = getIntent().getStringExtra("OPTION");
        if(option.equals("VIEW_PHARMACIES")){
            String url = "//medprohost.site11.com/get_pharmacies.php";
            URI uri = null;
            try {
                uri = new URI("http", url, null);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            url = uri.toString();
            ServerOperations serverOperations = new ServerOperations(url,"readFromDb");
            try {
                JSONArray jsonArray = serverOperations.execute().get();
                for(int i = 0; i < jsonArray.length(); i++){
                    String name = jsonArray.getJSONObject(i).getString("Name");
                    String longitude = jsonArray.getJSONObject(i).getString("Longitude");
                    String latitude = jsonArray.getJSONObject(i).getString("Latitude");
                    placeMarker(Double.parseDouble(longitude), Double.parseDouble(latitude), name);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private void placeMarker(double longitude, double latitude, String title) {
    /*create marker*/
        MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title(title);
     /*add the marker to the selected place*/
        googleMap.addMarker(marker);
     /*enable moving to the current location directly*/
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /*function to intialize map from GoogleMap services*/
    private void initilizeMap() {
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager
                .findFragmentById(R.id.map);
        googleMap = mapFragment.getMap();
    }
}
