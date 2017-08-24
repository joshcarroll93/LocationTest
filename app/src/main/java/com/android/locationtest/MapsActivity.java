package com.android.locationtest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    double longitude;
    double latitude;
    private Location location;
    private GoogleMap mMap;
    private Geocoder geocoder;
    private List<Address> addresses;
    private LatLng currentLocation;
    private MarkerOptions markerOptions;
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        //geocoder = new Geocoder(this, Locale.getDefault());
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        try {
            LocationManager lm = (LocationManager) getSystemService(MapsActivity.LOCATION_SERVICE);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 7500, 50, locationListener);

            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }catch(SecurityException se ) {
            se.printStackTrace();
        }

        mMap = googleMap;
        markerOptions = new MarkerOptions();

        getLocationDetails(location);

        //checking location permission
        // TODO: 23/08/2017 josh implement location permission prompt 
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            Toast.makeText(MapsActivity.this, "You have to accept to enjoy all app's services!", Toast.LENGTH_LONG).show();
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
        }
    }

    public void getLocationDetails(Location location){

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        currentLocation = new LatLng(latitude, longitude);
        float accuracy = location.getAccuracy();
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        }catch(IOException io) {
            io.printStackTrace();
        }
        address = addresses.get(0).getAddressLine(0);



        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15.0f));
        mMap.addMarker(markerOptions.position(currentLocation).title(address));
        Toast.makeText(MapsActivity.this, "location accuracy within "+ accuracy + " meters", Toast.LENGTH_SHORT).show();
        Snackbar.make(findViewById(android.R.id.content), "Location Changed:\n" + address , Snackbar.LENGTH_LONG).show();
    }
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            mMap.clear();
            getLocationDetails(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if(locationListener != null)
                locationListener.onStatusChanged(provider, status, extras);
        }

        @Override
        public void onProviderEnabled(String provider) {
            if(locationListener != null)
                locationListener.onProviderEnabled(provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            if(locationListener != null)
                locationListener.onProviderDisabled(provider);
        }
    };
}
