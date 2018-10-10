package com.qrcode.sakthikumar.magazineqrcodescanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.blikoon.qrcodescanner.QrCodeActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class QRCodeScanActivity extends AppCompatActivity {

    VideoView vvVideo;
    private static final int REQUEST_CODE_QR_SCAN = 101;
    private ProgressDialog dialog;
    private String skuNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_scan);
        dialog = new ProgressDialog(this);
        playBackgroudVideo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        playBackgroudVideo();
    }

    // Initialize Methods
    public void playBackgroudVideo() {
        vvVideo = (VideoView) findViewById(R.id.vvVideo);
        vvVideo.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.second_screen_bg_video));

        vvVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setVolume(0, 0);
                mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
                mp.setLooping(true);
                vvVideo.start();
            }
        });
    }


    // Action Methods
    public void actionOnScanQRCode(View v) {
//        Intent scanBarCodeActivity = new Intent(getApplicationContext(), ScannedBarcodeActivity.class);
//        startActivity(scanBarCodeActivity );
        Intent i = new Intent(QRCodeScanActivity.this, QrCodeActivity.class);
        startActivityForResult( i,REQUEST_CODE_QR_SCAN);
    }

    // Action Methods
    public void actionOnCancel(View v) {
        finish();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode != Activity.RESULT_OK)
        {
            Log.d("","COULD NOT GET A GOOD RESULT.");
            if(data==null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.error_decoding_image");
            if( result!=null)
            {
                AlertDialog alertDialog = new AlertDialog.Builder(QRCodeScanActivity.this).create();
                alertDialog.setTitle("Scan Error");
                alertDialog.setMessage("QR Code could not be scanned");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
            return;

        }
        if(requestCode == REQUEST_CODE_QR_SCAN)
        {
            if(data==null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
            Log.d("","Have scan result in your app activity :"+ result);
            skuNumber = result;
            getVideoUrl();
//            AlertDialog alertDialog = new AlertDialog.Builder(QRCodeScanActivity.this).create();
//            alertDialog.setTitle("Scan result");
//            alertDialog.setMessage(result);
//            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
//            alertDialog.show();

        }
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
                    Intent intent = new Intent(QRCodeScanActivity.this, VideoViewPlayerActivity.class);
                    intent.putExtra("urlString", urlString);
                    startActivity(intent);
//                    startActivity(PlayerActivity.getVideoPlayerIntent(ScannedBarcodeActivity.this, urlString, ""));
//                    isScanProcessing = false;
                } else if (server.toLowerCase().contains("youtube")) {
                    String key = uri.getQueryParameter("v");
                    Log.e("Key", key);
                    Intent intent = new Intent(QRCodeScanActivity.this, YoutubePlayerActivity.class);
                    intent.putExtra("urlKey", key);
                    startActivity(intent);
//                    isScanProcessing = false;
                } else {
                    String[] separated = path.split("/");
                    if (separated.length != 0) {
                        Log.e("Key", separated[separated.length - 1]);
                        Intent intent = new Intent(QRCodeScanActivity.this, VimeoPlayerActivity.class);
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
}
