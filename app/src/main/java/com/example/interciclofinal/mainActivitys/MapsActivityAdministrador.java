package com.example.interciclofinal.mainActivitys;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapsActivityAdministrador extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private  int MY_PERMISO_LOCATION_FINE ;

    private FusedLocationProviderClient fusedLocationClient;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;


    ArrayList<Marker> tempRealTimeMarket = new ArrayList<>();
    ArrayList<Marker>  realTimeMarket = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_administrador);

        mDatabaseReference= FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        subirLatLon();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-42, 89);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap=googleMap;
        mDatabaseReference.child("usuarios_real_time").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (Marker marker:realTimeMarket){
                    marker.remove();
                }

                for(DataSnapshot snapshot1:dataSnapshot.getChildren()){
                    Position position=snapshot1.getValue(Position.class);
                    Double lat=position.getLatitud();
                    Double longi=position.getLongitud();
                    MarkerOptions markerOptions=new MarkerOptions();
                    markerOptions.position(new LatLng(lat,longi));

                    tempRealTimeMarket.add(mMap.addMarker(markerOptions));
                }
                realTimeMarket.clear();
                realTimeMarket.addAll(tempRealTimeMarket);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void subirLatLon() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //pedir permiso de ubicacion
            ActivityCompat.requestPermissions(MapsActivityAdministrador.this,new String[]{
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

                            mDatabaseReference.child("usuarios_real_time").push().setValue(posicion);

                        }
                    }
                });
    }
}
