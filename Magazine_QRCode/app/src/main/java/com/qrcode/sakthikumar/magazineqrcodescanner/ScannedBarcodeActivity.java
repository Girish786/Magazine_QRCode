package com.qrcode.sakthikumar.magazineqrcodescanner;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
//import com.appunite.appunitevideoplayer.PlayerActivity;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ScannedBarcodeActivity extends AppCompatActivity {


    private CodeScanner mCodeScanner;
    private String skuNumber;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_barcode);
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        dialog = new ProgressDialog(this);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                ScannedBarcodeActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        skuNumber = result.getText();
                        getVideoUrl();
//                        Toast.makeText(ScannedBarcodeActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    public void getVideoUrl() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        showProgressDialog();

        StringRequest request = new StringRequest(Request.Method.POST, Constants.SCAN_BOARD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Response", response.toString());
                        hideProgressDialog();
                        processResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Response", error.toString());
                        hideProgressDialog();
                        Toast.makeText(getApplicationContext(), error.toString(),
                                Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("skunumber", skuNumber);
                return params;
            }
        };

        requestQueue.add(request);

    }


    public void showProgressDialog() {
        dialog.setMessage("loading...");
        dialog.show();
    }

    public void hideProgressDialog() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void processResponse(String response) {
        try {
            JSONObject obj = new JSONObject(response);
            JSONArray array = obj.getJSONArray("videodetails");

            if (array.length() != 0) {
                String urlString = array.getJSONObject(0).getString("magazine_video_link_to_play");
                Uri uri = Uri.parse(urlString);
                String server = uri.getAuthority();
                String path = uri.getPath();

                if (server.toLowerCase().contains("youniquevoices")) {
                    Log.e("Key", urlString);
//                    Intent intent = new Intent(ScannedBarcodeActivity.this, PlayerActivity.class);
//                    intent.putExtra("urlString", urlString);
//                    startActivity(intent);
//                    startActivity(PlayerActivity.getVideoPlayerIntent(ScannedBarcodeActivity.this, urlString, ""));
//                    isScanProcessing = false;
                } else if (server.toLowerCase().contains("youtube")) {
                    String key = uri.getQueryParameter("v");
                    Log.e("Key", key);
                    Intent intent = new Intent(ScannedBarcodeActivity.this, YoutubePlayerActivity.class);
                    intent.putExtra("urlKey", key);
                    startActivity(intent);
//                    isScanProcessing = false;
                } else {
                    String[] separated = path.split("/");
                    if (separated.length != 0) {
                        Log.e("Key", separated[separated.length - 1]);
                        Intent intent = new Intent(ScannedBarcodeActivity.this, VimeoPlayerActivity.class);
                        intent.putExtra("urlKey", separated[separated.length - 1]);
                        startActivity(intent);
                    }
//                    isScanProcessing = false;
                }
            } else {
//                isScanProcessing = false;
                Toast.makeText(getApplicationContext(), obj.getString("message"),
                        Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
//            isScanProcessing = false;
            Log.e("MYAPP", "Scanned SKU number is wrong.", e);
            Toast.makeText(getApplicationContext(), "Scanned SKU number is wrong.",
                    Toast.LENGTH_SHORT).show();
        }
    }

//    SurfaceView surfaceView;
//    private BarcodeDetector barcodeDetector;
//    private CameraSource cameraSource;
//    private static final int REQUEST_CAMERA_PERMISSION = 201;
////    private ProgressDialog dialog;
//    private String skuNumber;
//    private boolean isScanProcessing = false;
//
//    View progressView;
//    ProgressBar progressBar;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_scanned_barcode);
//
//        surfaceView = findViewById(R.id.scannedActivitySurfaceView);
////        dialog = new ProgressDialog(this);
//        progressView = (View) findViewById(R.id.view);
//        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
//        hideProgressDialog();
//    }
//
//    private void initialiseDetectorsAndSources() {
//        barcodeDetector = new BarcodeDetector.Builder(this)
//                .setBarcodeFormats(Barcode.ALL_FORMATS)
//                .build();
//
//        cameraSource = new CameraSource.Builder(this, barcodeDetector)
//                .setRequestedPreviewSize(1920, 1080)
//                .setAutoFocusEnabled(true) //you should add this feature
//                .build();
//
//        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
//            @Override
//            public void surfaceCreated(SurfaceHolder holder) {
//                try {
//                    if (ActivityCompat.checkSelfPermission(ScannedBarcodeActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//                        cameraSource.start(surfaceView.getHolder());
//                    } else {
//                        ActivityCompat.requestPermissions(ScannedBarcodeActivity.this, new
//                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
//                    }
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }
//
//            @Override
//            public void surfaceDestroyed(SurfaceHolder holder) {
//                cameraSource.stop();
//            }
//        });
//
//
//        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
//            @Override
//            public void release() { }
//
//            @Override
//            public void receiveDetections(Detector.Detections<Barcode> detections) {
//                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
//                if (barcodes.size() != 0) {
//                    isScanProcessing = true;
//                    skuNumber = barcodes.valueAt(0).displayValue;
//                    getVideoUrl();
//                }
//            }
//        });
//    }
//
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        cameraSource.release();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        initialiseDetectorsAndSources();
//    }
//
//    public void getVideoUrl() {
//        String url = "http://uvapps.youniquevoices.com/index.php/uvappsapicontrol/magazinevideoaccess";
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        showProgressDialog();
//
//        StringRequest request = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.e("Response", response.toString());
//                        hideProgressDialog();
//                        barcodeDetector.release();
//                        processResponse(response);
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.e("Response", error.toString());
//                        hideProgressDialog();
//                        isScanProcessing = false;
//                        Toast.makeText(getApplicationContext(), error.toString(),
//                                Toast.LENGTH_LONG).show();
//                    }
//                }) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("skunumber", skuNumber);
//                return params;
//            }
//        };
//
//        requestQueue.add(request);
//
//    }
//
//
//    public void showProgressDialog() {
////        dialog.setMessage("loading...");
////        dialog.show();
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {
//                progressView.setVisibility(View.VISIBLE);
//                progressBar.setVisibility(View.VISIBLE);
//            }
//        });
//
//    }
//
//    public void hideProgressDialog() {
//        progressView.setVisibility(View.INVISIBLE);
//        progressBar.setVisibility(View.INVISIBLE);
//
////        if (dialog.isShowing()) {
////            dialog.dismiss();
////        }
//    }
//
//    public void processResponse(String response) {
//        try {
//            JSONObject obj = new JSONObject(response);
//            JSONArray array = obj.getJSONArray("videodetails");
//
//            if (array.length() != 0) {
//                String urlString = array.getJSONObject(0).getString("magazine_video_link_to_play");
//                Uri uri = Uri.parse(urlString);
//                String server = uri.getAuthority();
//                String path = uri.getPath();
//
//                if (server.toLowerCase().contains("youniquevoices")) {
//                    Log.e("Key", urlString);
//                    Intent intent = new Intent(ScannedBarcodeActivity.this, PlayerActivity.class);
//                    intent.putExtra("urlString", urlString);
//                    startActivity(intent);
//                    isScanProcessing = false;
//                } else if (server.toLowerCase().contains("youtube")) {
//                    String key = uri.getQueryParameter("v");
//                    Log.e("Key", key);
//                    Intent intent = new Intent(ScannedBarcodeActivity.this, YoutubePlayerActivity.class);
//                    intent.putExtra("urlKey", key);
//                    startActivity(intent);
//                    isScanProcessing = false;
//                } else {
//                    String[] separated = path.split("/");
//                    if (separated.length != 0) {
//                        Log.e("Key", separated[separated.length - 1]);
//                        Intent intent = new Intent(ScannedBarcodeActivity.this, VimeoPlayerActivity.class);
//                        intent.putExtra("urlKey", separated[separated.length - 1]);
//                        startActivity(intent);
//                    }
//                    isScanProcessing = false;
//                }
//            } else {
//                isScanProcessing = false;
//                Toast.makeText(getApplicationContext(), obj.getString("message"),
//                        Toast.LENGTH_SHORT).show();
//            }
//        } catch (JSONException e) {
//            isScanProcessing = false;
//            Log.e("MYAPP", "Email Address or Password is wrong.", e);
//            Toast.makeText(getApplicationContext(), "Email Address or Password is wrong.",
//                    Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    public void actionOnClose(View v) {
//        finish();
//    }
}




