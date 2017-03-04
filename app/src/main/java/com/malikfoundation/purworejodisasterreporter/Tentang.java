package com.malikfoundation.purworejodisasterreporter;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class Tentang extends AppCompatActivity {
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tentang);
        handler = new Handler();

        TextView txtTentang = (TextView)findViewById(R.id.txtTentang);
        txtTentang.setText("Purworejo Disaster Reporter");

        TextView txtVer = (TextView)findViewById(R.id.txtVer);
        txtVer.setText("Version 1.0");

        TextView txtDev = (TextView)findViewById(R.id.txtDev);
        txtDev.setText("Created by Malik Fajar Mubarok");

        TextView txtCopyRight = (TextView)findViewById(R.id.txtCopyRight);
        txtCopyRight.setText("Copyright \u00A9 2016");
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
