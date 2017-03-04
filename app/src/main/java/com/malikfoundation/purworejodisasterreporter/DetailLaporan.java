package com.malikfoundation.purworejodisasterreporter;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

public class DetailLaporan extends AppCompatActivity implements OnMapReadyCallback {
    private DataModel dataModel;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_laporan);
        handler = new Handler();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapDetailLaporan);
        mapFragment.getMapAsync(this);
        TextView detailNama = (TextView)findViewById(R.id.detailNama);
        TextView detailDatetime = (TextView)findViewById(R.id.detailDatetime);
        ImageView detailImage = (ImageView)findViewById(R.id.detailImage);
        ProgressBar progressBarDetail = (ProgressBar)findViewById(R.id.progressBarDetail);
        TextView detailLevel = (TextView)findViewById(R.id.detailLevel);
        TextView detailLatitude = (TextView)findViewById(R.id.detailLatitude);
        TextView detailLongitude = (TextView)findViewById(R.id.detailLongitude);
        TextView detailDeskripsi = (TextView)findViewById(R.id.detailDeskripsi);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String jsonIntent = bundle.getString("dataModel");
            dataModel = new Gson().fromJson(jsonIntent, DataModel.class);
            detailNama.setText(dataModel.getBencana());
            detailDatetime.setText("Dilaporkan pada " + dataModel.getDatetime());
            new ImageLoadTask(dataModel.getImage(), detailImage, progressBarDetail).execute();
            detailLevel.setText("Level Bencana: " + dataModel.getLevel());
            detailLatitude.setText("Latitude: " + dataModel.getLatitude());
            detailLongitude.setText("Longitude: " + dataModel.getLongitude());
            detailDeskripsi.setText(dataModel.getDeskripsi());
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMap mMap;
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        final LatLng bencana = new LatLng(dataModel.getLatitude(), dataModel.getLongitude());
        mMap.addMarker(new MarkerOptions().position(bencana).title("Lokasi Bencana"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bencana, 15));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Intent m = new Intent(DetailLaporan.this, LaporanMap.class);
                m.putExtra("lat", dataModel.getLatitude());
                m.putExtra("lon", dataModel.getLongitude());
                startActivity(m);
            }
        });
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
