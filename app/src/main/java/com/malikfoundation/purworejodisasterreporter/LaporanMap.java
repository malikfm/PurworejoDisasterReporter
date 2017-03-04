package com.malikfoundation.purworejodisasterreporter;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LaporanMap extends FragmentActivity implements OnMapReadyCallback {
    private double latVal;
    private double lonVal;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_map);
        handler = new Handler();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        latVal = getIntent().getDoubleExtra("lat", 0);
        lonVal = getIntent().getDoubleExtra("lon", 0);
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
        GoogleMap mMap;
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng bencana = new LatLng(latVal, lonVal);
        mMap.addMarker(new MarkerOptions().position(bencana).title("Lokasi Bencana"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bencana, 15));
    }

    protected void onStart() {
        super.onStart();
        mStatusChecker.run();
    }

    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(mStatusChecker);
    }

    private Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo ni = cm.getActiveNetworkInfo();
                if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER) && ni != null && ni.isConnected()) {
                    // Do nothing
                } else {
                    Toast.makeText(getApplicationContext(), "GPS atau koneksi internet tidak aktif.", Toast.LENGTH_LONG).show();
                }
            } finally {
                handler.postDelayed(mStatusChecker, 60000);
            }
        }
    };
}
