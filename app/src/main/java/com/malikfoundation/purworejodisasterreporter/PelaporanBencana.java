package com.malikfoundation.purworejodisasterreporter;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class PelaporanBencana extends AppCompatActivity {
    private static final int CAPTURE_IMAGE = 1;
    private String message;
    private ProgressDialog dialog;
    private Button btnFoto;
    private ImageView imgPrev;
    private Button btnHapusImg;
    private Spinner listBencana;
    private EditText txtDeskripsi;
    private RadioGroup radioGroup;
    private TextView txtLat;
    private TextView txtLon;
    private TextView txtNomor;
    private Button btnKirim;
    private LocationManager lm;
    private String provider;
    private Uri fileUri = null;
    private String id = null;
    private String datetime = null;
    private String fileName = null;
    private String fileTemp = null;
    private String filePath = null;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pelaporan_bencana);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        handler = new Handler();
        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Sedang Mengirim...");
        btnFoto = (Button)findViewById(R.id.btnFoto);
        imgPrev = (ImageView) findViewById(R.id.imgPrev);
        btnHapusImg = (Button)findViewById(R.id.btnHapusImg);
        listBencana = (Spinner)findViewById(R.id.dropdown);
        txtDeskripsi = (EditText)findViewById(R.id.txtDeskripsi);
        radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        txtLat = (TextView)findViewById(R.id.txtLat);
        txtLon = (TextView)findViewById(R.id.txtLon);
        txtNomor = (TextView)findViewById(R.id.txtNomor);
        btnKirim = (Button)findViewById(R.id.btnKirim);

        TelephonyManager tm = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        txtNomor.setText(tm.getLine1Number());
        if (txtNomor.getText().toString().equals("")) {
            txtNomor.setText("Unknown");
        }
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        provider = lm.getBestProvider(criteria, true);
        Location location = lm.getLastKnownLocation(provider);
        updateLocation(location);

        btnFoto.setOnClickListener(ambilFoto);
        btnHapusImg.setOnClickListener(hapusFoto);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.list_bencana, R.layout.spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_item);
        // Apply the adapter to the spinner
        listBencana.setAdapter(adapter);
        btnKirim.setOnClickListener(kirimData);

    }

    private final LocationListener locListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            updateLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {
            updateLocation(null);
        }
    };

    private void updateLocation(Location location) {
        String latVal, lonVal;
        double lat, lon;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        if (location != null) {
            lat = location.getLatitude();
            lon = location.getLongitude();

            latVal = "Latitude: " +lat;
            lonVal = "Longitude: " +lon;
        } else {
            location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                lat = location.getLatitude();
                lon = location.getLongitude();
                latVal = "Latitude: " +lat;
                lonVal = "Longitude: " +lon;
            } else {
                latVal = "Latitude: Sedang Mencari...";
                lonVal = "Longitude: Sedang Mencari...";
            }
        }
        txtLat.setText(latVal);
        txtLon.setText(lonVal);
    }

    // Action untuk btnFoto
    private View.OnClickListener ambilFoto = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                fileUri = Uri.fromFile(outputMediaFile());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, CAPTURE_IMAGE);
            }
        }
    };

    //Action untuk btnHapusImg
    private View.OnClickListener hapusFoto = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            File file = new File(fileUri.getPath());
            file.delete();
            fileUri = null;
            id = null;
            datetime = null;
            fileName = null;
            fileTemp = null;
            filePath = null;
            btnFoto.setVisibility(View.VISIBLE);
            imgPrev.setVisibility(View.GONE);
            btnHapusImg.setVisibility(View.GONE);
        }
    };

    //Action untuk btnKirim
    private View.OnClickListener kirimData = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String bnc = listBencana.getSelectedItem().toString();
            String des = txtDeskripsi.getText().toString();
            int radioId = radioGroup.getCheckedRadioButtonId();
            RadioButton level = (RadioButton)findViewById(radioId);
            String lvl = null;
            if (level != null) {
                lvl = level.getText().toString();
            }
            String lat = txtLat.getText().toString().substring(10);
            String lon = txtLon.getText().toString().substring(11);
            String num = txtNomor.getText().toString();

            if (id != null && !id.equals("") && !des.equals("") && !lat.equals("Sedang Mencari...") &&
                    !lon.equals("Sedang Mencari...") && !num.equals("Unknown")) {
                btnHapusImg.setEnabled(false);
                new PostClass().execute(bnc, des, lvl, lat, lon, num);
            } else {
                Toast.makeText(getApplicationContext(),
                        "Mohon isi data dengan lengkap.", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    };

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
        outState.putString("id", id);
        outState.putString("datetime", datetime);
        outState.putString("file_name", fileName);
        outState.putString("file_tmp", fileTemp);
        outState.putString("file_path", filePath);
    }

    /*
     * Here we restore the fileUri again
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
        id = savedInstanceState.getString("id");
        datetime = savedInstanceState.getString("datetime");
        fileName = savedInstanceState.getString("file_name");
        fileTemp = savedInstanceState.getString("file_tmp");
        filePath = savedInstanceState.getString("file_path");
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAPTURE_IMAGE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                // display it in image view
                previewCapturedImage();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "Batal mengambil foto.", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Gagal mengambil foto.", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    /*
     * returning image
     */
    private File outputMediaFile() {
        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "Purworejo Disaster Reporter");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Toast.makeText(getApplicationContext(),
                        "Gagal membuat direktori.", Toast.LENGTH_SHORT)
                        .show();
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()).format(new Date());
        filePath = mediaStorageDir.getPath();
        fileName = "IMG_" + timeStamp + ".jpg";
        fileTemp = "IMG_" + timeStamp + "_tmp" + ".jpg";
        File mediaFile = new File(filePath + File.separator
                + fileTemp);
        id = "ID_" + timeStamp;

        return mediaFile;
    }

    /*
     * Display image from a path to ImageView
     */
    private void previewCapturedImage() {
        try {
            // show image preview
            btnFoto.setVisibility(View.GONE);
            imgPrev.setVisibility(View.VISIBLE);
            btnHapusImg.setVisibility(View.VISIBLE);

            imgPrev.setImageBitmap(decodeSampledBitmap(fileUri.getPath(), 100, 100));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private Bitmap decodeSampledBitmap(String path, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private class PostClass extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            btnKirim.setEnabled(false);
            dialog.show();
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
        protected String doInBackground(String... params) {
            HttpsURLConnection connection = null;
//            HttpURLConnection connection = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            URL url;
            String fileUpload = compressImage();
            String responseMessage = "ERROR";

            try {
                url = new URL("https://188.166.190.247/process/write_data.php");
//                url = new URL("http://192.168.100.40/skripsi/process/write_data.php");
                connection = (HttpsURLConnection) url.openConnection();
                connection.setHostnameVerifier(hostnameVerifier);
//                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setUseCaches(false);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("uploaded_file", fileUpload);

                FileInputStream fileInputStream = new FileInputStream(fileUpload);

                int bytesAvailable = fileInputStream.available();
                int maxBufferSize = 1024 * 1024;
                int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                byte[ ] buffer = new byte[bufferSize];
                int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());

                dStream.writeBytes(twoHyphens + boundary + lineEnd);

                dStream.writeBytes("Content-Disposition: form-data; name=\"id\""+ lineEnd);
                dStream.writeBytes(lineEnd);
                dStream.writeBytes(id);
                dStream.writeBytes(lineEnd);
                dStream.writeBytes(twoHyphens + boundary + lineEnd);

                dStream.writeBytes("Content-Disposition: form-data; name=\"bencana\""+ lineEnd);
                dStream.writeBytes(lineEnd);
                dStream.writeBytes(params[0]);
                dStream.writeBytes(lineEnd);
                dStream.writeBytes(twoHyphens + boundary + lineEnd);

                dStream.writeBytes("Content-Disposition: form-data; name=\"deskripsi\"" + lineEnd);
                dStream.writeBytes(lineEnd);
                dStream.writeBytes(params[1]);
                dStream.writeBytes(lineEnd);
                dStream.writeBytes(twoHyphens + boundary + lineEnd);

                dStream.writeBytes("Content-Disposition: form-data; name=\"level\"" + lineEnd);
                dStream.writeBytes(lineEnd);
                dStream.writeBytes(params[2]);
                dStream.writeBytes(lineEnd);
                dStream.writeBytes(twoHyphens + boundary + lineEnd);

                dStream.writeBytes("Content-Disposition: form-data; name=\"latitude\""+ lineEnd);
                dStream.writeBytes(lineEnd);
                dStream.writeBytes(params[3]);
                dStream.writeBytes(lineEnd);
                dStream.writeBytes(twoHyphens + boundary + lineEnd);

                dStream.writeBytes("Content-Disposition: form-data; name=\"longitude\""+ lineEnd);
                dStream.writeBytes(lineEnd);
                dStream.writeBytes(params[4]);
                dStream.writeBytes(lineEnd);
                dStream.writeBytes(twoHyphens + boundary + lineEnd);

                dStream.writeBytes("Content-Disposition: form-data; name=\"datetime\""+ lineEnd);
                dStream.writeBytes(lineEnd);
                dStream.writeBytes(datetime);
                dStream.writeBytes(lineEnd);
                dStream.writeBytes(twoHyphens + boundary + lineEnd);

                dStream.writeBytes("Content-Disposition: form-data; name=\"num\""+ lineEnd);
                dStream.writeBytes(lineEnd);
                dStream.writeBytes(params[5]);
                dStream.writeBytes(lineEnd);
                dStream.writeBytes(twoHyphens + boundary + lineEnd);

                dStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=" + fileName + "" + lineEnd);
                dStream.writeBytes(lineEnd);
                while (bytesRead > 0)
                {
                    dStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable,maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
                dStream.writeBytes(lineEnd);
                dStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                if (connection.getResponseCode() == 200) {
                    responseMessage = "OK";
                }

                message = connection.getResponseMessage();

                fileInputStream.close();
                dStream.flush();
                dStream.close();
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                responseMessage = "MALFORMED";
            } catch (SocketTimeoutException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                responseMessage = "TIMEOUT";
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                responseMessage = "INTERRUPTED";
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }

            return responseMessage;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            dialog.dismiss();
            btnKirim.setEnabled(true);
            switch (result) {
                case "OK":
                    PelaporanBencana.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Berhasil mengirim data.", Toast.LENGTH_SHORT)
                                    .show();
                            btnFoto.setVisibility(View.VISIBLE);
                            btnHapusImg.setVisibility(View.GONE);
                            imgPrev.setVisibility(View.GONE);
                            txtDeskripsi.setText("");
                        }
                    });
                    File deleteTmp = new File(fileUri.getPath());
                    deleteTmp.delete();
                    fileUri = null;
                    id = null;
                    datetime = null;
                    fileName = null;
                    fileTemp = null;
                    filePath = null;
                    break;
                case "MALFORMED":
                    PelaporanBencana.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Gagal mengirim data. Malformed URL.", Toast.LENGTH_SHORT)
                                    .show();
                            btnHapusImg.setEnabled(true);
                            File unsavedFile = new File(filePath + File.separator + fileName);
                            unsavedFile.delete();
                        }
                    });
                    break;
                case "TIMEOUT":
                    PelaporanBencana.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Gagal mengirim data. Connection Timeout.", Toast.LENGTH_SHORT)
                                    .show();
                            btnHapusImg.setEnabled(true);
                            File unsavedFile = new File(filePath + File.separator + fileName);
                            unsavedFile.delete();
                        }
                    });
                    break;
                case "INTERRUPTED":
                    PelaporanBencana.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Gagal mengirim data. Connection Interrupted.", Toast.LENGTH_SHORT)
                                    .show();
                            btnHapusImg.setEnabled(true);
                            File unsavedFile = new File(filePath + File.separator + fileName);
                            unsavedFile.delete();
                        }
                    });
                    break;
                default:
                    PelaporanBencana.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Gagal mengirim data. " + message, Toast.LENGTH_SHORT)
                                    .show();
                            btnHapusImg.setEnabled(true);
                            File unsavedFile = new File(filePath + File.separator + fileName);
                            unsavedFile.delete();
                        }
                    });
                    break;
            }
        }

        private HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        private String compressImage() {
            String filePathTmp = fileUri.getPath();
            Bitmap scaledBitmap = null;

            BitmapFactory.Options options = new BitmapFactory.Options();

            //by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
            //you try the use the bitmap here, you will get null.
            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeFile(filePathTmp, options);

            int actualHeight = options.outHeight;
            int actualWidth = options.outWidth;

            //max Height and width values of the compressed image is taken as 816x612
            float maxHeight = 640.0f;
            float maxWidth = 640.0f;
            float imgRatio = actualHeight / actualWidth;
            float maxRatio = maxHeight / maxWidth;

            //width and height values are set maintaining the aspect ratio of the image
            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxWidth / actualWidth;
                    actualWidth = (int) (imgRatio * actualWidth);
                    actualHeight = (int) maxHeight;
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxHeight / actualHeight;
                    actualHeight = (int) (imgRatio * actualHeight);
                    actualWidth = (int) maxWidth;
                } else {
                    actualHeight = (int) maxHeight;
                    actualWidth = (int) maxWidth;

                }
            }

            //setting inSampleSize value allows to load a scaled down version of the original image
            options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

            //inJustDecodeBounds set to false to load the actual bitmap
            options.inJustDecodeBounds = false;

            //this options allow android to claim the bitmap memory if it runs low on memory
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inTempStorage = new byte[16 * 1024];

            try {
                //load the bitmap from its path
                bmp = BitmapFactory.decodeFile(filePathTmp, options);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();
            }

            try {
                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();
            }

            float ratioX = actualWidth / (float) options.outWidth;
            float ratioY = actualHeight / (float) options.outHeight;
            float middleX = actualWidth / 2.0f;
            float middleY = actualHeight / 2.0f;

            Matrix scaleMatrix = new Matrix();
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

            Canvas canvas = new Canvas(scaledBitmap);
            canvas.setMatrix(scaleMatrix);
            canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

            //check the rotation of the image and display it properly
            ExifInterface exif;
            try {
                exif = new ExifInterface(filePathTmp);

                int orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, 0);
                Log.d("EXIF", "Exif: " + orientation);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 3) {
                    matrix.postRotate(180);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 8) {
                    matrix.postRotate(270);
                    Log.d("EXIF", "Exif: " + orientation);
                }
                scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                        scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                        true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            FileOutputStream out;
            String newFile = filePath + File.separator + fileName;
            try {
                out = new FileOutputStream(newFile);

                //write the compressed bitmap at the destination specified by filename.
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            return newFile;
        }
    }

    protected void onStart() {
        super.onStart();
        mStatusChecker.run();
    }

    protected void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        lm.requestLocationUpdates(provider, 400, 1, locListener);
    }

    protected void onPause() {
        super.onPause();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        lm.removeUpdates(locListener);
        handler.removeCallbacks(mStatusChecker);
    }

    protected void onDestroy() {
        super.onDestroy();

        if(fileUri != null) {
            File deletedFile = new File(fileUri.getPath());
            deletedFile.delete();
            fileUri = null;
            id = null;
            datetime = null;
            fileName = null;
            fileTemp = null;
            filePath = null;
        }
    }

    private Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
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