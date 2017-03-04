package com.malikfoundation.purworejodisasterreporter;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ListView listview;
    private ProgressBar progressBarMain;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new Handler();
        listview = (ListView)findViewById(R.id.list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Laporan Terkini");
        }
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent pb = new Intent(getApplicationContext(), PelaporanBencana.class);
                    startActivity(pb);
                }
            });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer);
        if (drawer != null) {
            drawer.setDrawerListener(toggle);
        }
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

        progressBarMain = (ProgressBar)findViewById(R.id.progressBarMain);

        new JSONTask().execute("https://188.166.190.247/process/get_data.php");
//        new JSONTask().execute("http://192.168.100.40/skripsi/process/get_data.php");
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_lokasi_saya) {
            Intent ls = new Intent(this, LokasiSaya.class);
            startActivity(ls);
        } else if (id == R.id.nav_bantuan) {
            Intent b = new Intent(this, Bantuan.class);
            startActivity(b);
        } else if (id == R.id.nav_tentang) {
            Intent t = new Intent(this, Tentang.class);
            startActivity(t);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }
    }

    private class JSONTask extends AsyncTask<String, Void, List<DataModel>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBarMain.setVisibility(View.VISIBLE);
            TrustManager localTrustmanager = new X509TrustManager() {

                @Override
                public X509Certificate[] getAcceptedIssuers() {return null;
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }
            };

            try {
                SSLContext context = SSLContext.getInstance("TLS");
                context.init(null, new TrustManager[]{localTrustmanager},
                        new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected List<DataModel> doInBackground(String... params) {
            HttpsURLConnection connection = null;
//            HttpURLConnection connection = null;
            URL url;
            BufferedReader reader = null;

            try {
                url = new URL(params[0]);
                connection = (HttpsURLConnection) url.openConnection();
                connection.setHostnameVerifier(hostnameVerifier);
//                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                InputStream is = connection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                reader = new BufferedReader(new InputStreamReader(is));
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                String finalJSON = buffer.toString();
                JSONObject parentJSON = new JSONObject(finalJSON);
                JSONArray parentArray = parentJSON.getJSONArray("data_bencana");
                List<DataModel> dataModelList = new ArrayList<>();
                Gson gson = new Gson();

                for(int i=0; i<parentArray.length(); i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    DataModel dataModel = gson.fromJson(finalObject.toString(), DataModel.class);

                    // adding the final object in the list
                    dataModelList.add(dataModel);
                }
                return dataModelList;
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (SocketTimeoutException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(final List<DataModel> result) {
            super.onPostExecute(result);
            progressBarMain.setVisibility(View.GONE);
            CustomAdapter adapter;
            if(result != null) {
                adapter = new CustomAdapter(getApplicationContext(), R.layout.list_item, result);
                listview.setAdapter(adapter);
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        DataModel dataModel = result.get(position);
                        Intent intent = new Intent(MainActivity.this, DetailLaporan.class);
                        intent.putExtra("dataModel", new Gson().toJson(dataModel));
                        startActivity(intent);
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Tidak dapat mengambil data dari server.", Toast.LENGTH_SHORT).show();
            }
        }

        private HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.refresh) {
            new JSONTask().execute("https://188.166.190.247/process/get_data.php");
//            new JSONTask().execute("http://192.168.100.40/skripsi/process/get_data.php");
            return true;
        }

        return super.onOptionsItemSelected(item);
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