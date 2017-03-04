package com.malikfoundation.purworejodisasterreporter;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Bantuan extends AppCompatActivity {
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bantuan);
        handler = new Handler();

        final ImageView imgListBantuan1 = (ImageView)findViewById(R.id.imgListBantuan1);
        final ImageView imgListBantuanSl1 = (ImageView)findViewById(R.id.imgListBantuanSl1);
        final TextView txtBantuanPelaporan = (TextView)findViewById(R.id.txtBantuanPelaporan);
        txtBantuanPelaporan.setText("Membuat Laporan Baru");
        final TextView txtBantuanPoinPelaporan = (TextView)findViewById(R.id.txtBantuanPoinPelaporan);
        txtBantuanPoinPelaporan.setText("1. Pilih menu tulis laporan baru pada pojok kanan bawah menu utama.\n" +
                "2. Isi data laporan dengan lengkap dan jelas.\n" +
                "3. Tap tombol \"KIRIM\" untuk mengirim laporan.\n\n" +
                "Laporan yang tidak jelas tidak akan diproses dan akan dihapus oleh admin.");

        final ImageView imgListBantuan2 = (ImageView)findViewById(R.id.imgListBantuan2);
        final ImageView imgListBantuanSl2 = (ImageView)findViewById(R.id.imgListBantuanSl2);
        TextView txtBantuanDetail = (TextView)findViewById(R.id.txtBantuanDetail);
        txtBantuanDetail.setText("Melihat Laporan Terkini");
        final TextView txtBantuanPoinDetail = (TextView)findViewById(R.id.txtBantuanPoinDetail);
        txtBantuanPoinDetail.setText("1. Pilih list laporan terkini pada menu utama.\n" +
                "2. Tap pada map untuk melihat map secara fullscreen.");

        final ImageView imgListBantuan3 = (ImageView)findViewById(R.id.imgListBantuan3);
        final ImageView imgListBantuanSl3 = (ImageView)findViewById(R.id.imgListBantuanSl3);
        TextView txtBantuanLokasi = (TextView)findViewById(R.id.txtBantuanLokasi);
        txtBantuanLokasi.setText("Melihat Lokasi Saat Ini");
        final TextView txtBantuanPoinLokasi = (TextView)findViewById(R.id.txtBantuanPoinLokasi);
        txtBantuanPoinLokasi.setText("1. Tap menu pada pojok kiri atas menu utama.\n" +
                "2. Pilih menu \"Lokasi Saya\" untuk menampilkan lokasi Anda saat ini.");

        txtBantuanPelaporan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtBantuanPoinPelaporan.getVisibility() == View.VISIBLE) {
                    txtBantuanPoinPelaporan.setVisibility(View.GONE);
                    txtBantuanPoinDetail.setVisibility(View.GONE);
                    txtBantuanPoinLokasi.setVisibility(View.GONE);
                    if (imgListBantuan1 != null && imgListBantuan2 != null && imgListBantuan3 != null &&
                            imgListBantuanSl1 != null && imgListBantuanSl2 != null && imgListBantuanSl3 != null) {
                        imgListBantuan1.setVisibility(View.VISIBLE);
                        imgListBantuan2.setVisibility(View.VISIBLE);
                        imgListBantuan3.setVisibility(View.VISIBLE);
                        imgListBantuanSl1.setVisibility(View.GONE);
                        imgListBantuanSl2.setVisibility(View.GONE);
                        imgListBantuanSl3.setVisibility(View.GONE);
                    }
                } else {
                    txtBantuanPoinPelaporan.setVisibility(View.VISIBLE);
                    txtBantuanPoinDetail.setVisibility(View.GONE);
                    txtBantuanPoinLokasi.setVisibility(View.GONE);
                    if (imgListBantuan1 != null && imgListBantuan2 != null && imgListBantuan3 != null &&
                            imgListBantuanSl1 != null && imgListBantuanSl2 != null && imgListBantuanSl3 != null) {
                        imgListBantuan1.setVisibility(View.GONE);
                        imgListBantuan2.setVisibility(View.VISIBLE);
                        imgListBantuan3.setVisibility(View.VISIBLE);
                        imgListBantuanSl1.setVisibility(View.VISIBLE);
                        imgListBantuanSl2.setVisibility(View.GONE);
                        imgListBantuanSl3.setVisibility(View.GONE);
                    }
                }
            }
        });

        txtBantuanDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtBantuanPoinDetail.getVisibility() == View.VISIBLE) {
                    txtBantuanPoinPelaporan.setVisibility(View.GONE);
                    txtBantuanPoinDetail.setVisibility(View.GONE);
                    txtBantuanPoinLokasi.setVisibility(View.GONE);
                    if (imgListBantuan1 != null && imgListBantuan2 != null && imgListBantuan3 != null &&
                            imgListBantuanSl1 != null && imgListBantuanSl2 != null && imgListBantuanSl3 != null) {
                        imgListBantuan1.setVisibility(View.VISIBLE);
                        imgListBantuan2.setVisibility(View.VISIBLE);
                        imgListBantuan3.setVisibility(View.VISIBLE);
                        imgListBantuanSl1.setVisibility(View.GONE);
                        imgListBantuanSl2.setVisibility(View.GONE);
                        imgListBantuanSl3.setVisibility(View.GONE);
                    }
                } else {
                    txtBantuanPoinPelaporan.setVisibility(View.GONE);
                    txtBantuanPoinDetail.setVisibility(View.VISIBLE);
                    txtBantuanPoinLokasi.setVisibility(View.GONE);
                    if (imgListBantuan1 != null && imgListBantuan2 != null && imgListBantuan3 != null &&
                            imgListBantuanSl1 != null && imgListBantuanSl2 != null && imgListBantuanSl3 != null) {
                        imgListBantuan1.setVisibility(View.VISIBLE);
                        imgListBantuan2.setVisibility(View.GONE);
                        imgListBantuan3.setVisibility(View.VISIBLE);
                        imgListBantuanSl1.setVisibility(View.GONE);
                        imgListBantuanSl2.setVisibility(View.VISIBLE);
                        imgListBantuanSl3.setVisibility(View.GONE);
                    }
                }
            }
        });

        txtBantuanLokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtBantuanPoinLokasi.getVisibility() == View.VISIBLE) {
                    txtBantuanPoinPelaporan.setVisibility(View.GONE);
                    txtBantuanPoinDetail.setVisibility(View.GONE);
                    txtBantuanPoinLokasi.setVisibility(View.GONE);
                    if (imgListBantuan1 != null && imgListBantuan2 != null && imgListBantuan3 != null &&
                            imgListBantuanSl1 != null && imgListBantuanSl2 != null && imgListBantuanSl3 != null) {
                        imgListBantuan1.setVisibility(View.VISIBLE);
                        imgListBantuan2.setVisibility(View.VISIBLE);
                        imgListBantuan3.setVisibility(View.VISIBLE);
                        imgListBantuanSl1.setVisibility(View.GONE);
                        imgListBantuanSl2.setVisibility(View.GONE);
                        imgListBantuanSl3.setVisibility(View.GONE);
                    }
                } else {
                    txtBantuanPoinPelaporan.setVisibility(View.GONE);
                    txtBantuanPoinDetail.setVisibility(View.GONE);
                    txtBantuanPoinLokasi.setVisibility(View.VISIBLE);
                    if (imgListBantuan1 != null && imgListBantuan2 != null && imgListBantuan3 != null &&
                            imgListBantuanSl1 != null && imgListBantuanSl2 != null && imgListBantuanSl3 != null) {
                        imgListBantuan1.setVisibility(View.VISIBLE);
                        imgListBantuan2.setVisibility(View.VISIBLE);
                        imgListBantuan3.setVisibility(View.GONE);
                        imgListBantuanSl1.setVisibility(View.GONE);
                        imgListBantuanSl2.setVisibility(View.GONE);
                        imgListBantuanSl3.setVisibility(View.VISIBLE);
                    }
                }
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
