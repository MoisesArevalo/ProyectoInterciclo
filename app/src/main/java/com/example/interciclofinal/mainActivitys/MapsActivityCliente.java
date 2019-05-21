package com.example.interciclofinal.mainActivitys;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.interciclofinal.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MapsActivityCliente extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private  int MY_PERMISO_LOCATION_FINE ;

    private FusedLocationProviderClient fusedLocationClient;
    private DatabaseReference mDatabaseReference;


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_cliente);

        mDatabaseReference= FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        updatePosition();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void updatePosition() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //pedir permiso de ubicacion
            ActivityCompat.requestPermissions(MapsActivityCliente.this,new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            },MY_PERMISO_LOCATION_FINE);
            return;
        }


        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            Log.e("latitud: ",location.getLatitude() +" longitud "+location.getLongitude());
                            Map<String, Object> posicion=new HashMap<>();
                            posicion.put("id",mAuth.getCurrentUser().getUid());
                            posicion.put("latitud",location.getLatitude());
                            posicion.put("longitud",location.getLongitude());

                            //marcar el movimiento en el mapa

                            MarkerOptions markerOptions=new MarkerOptions();
                            markerOptions.position(new LatLng(location.getLatitude(),location.getLongitude()));
                            mMap.addMarker(markerOptions);

                            mDatabaseReference.child("usuarios_real_time").push().setValue(posicion);
                        }
                    }
                });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }
}
