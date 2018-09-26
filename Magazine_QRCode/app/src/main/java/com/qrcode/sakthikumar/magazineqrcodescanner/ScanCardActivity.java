package com.qrcode.sakthikumar.magazineqrcodescanner;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cunoraz.gifview.library.GifView;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ScanCardActivity extends AppCompatActivity {

    VideoView vvVideo;
    private static MediaPlayer mediaPlayer;
    int currentVideoPlaying = 1;
    int nextPlay = 2;
    String audioUrl;
    GifView fullScreeGifView;
    Button permissionBtn;

    static Uri firstLocalVideo;
    static Uri secondLocalVideo;
    static Uri thirdLocalVideo;
    static Uri audioUri;
    static String redirect_url;
    String scanCardSKUNumber;
    ArrayList<VideoURI> videoUrls = new ArrayList<>();
    JSONArray videoJsonArray = new JSONArray();

    boolean isFirstVideoDownloaded = false;
    boolean isSecondVideoDownloaded = false;
    boolean isThirdVideoDownloaded = false;
    boolean isAudioDownloaded = false;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_card);
        vvVideo = (VideoView) findViewById(R.id.vvVideo);
        fullScreeGifView = (GifView) findViewById(R.id.gifPlayer_eight);
        permissionBtn = (Button) findViewById(R.id.storage_permission_btn);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Loading...");

        isFirstVideoDownloaded = false;
        isSecondVideoDownloaded = false;
        isThirdVideoDownloaded = false;

        actionOnCheckPermission(permissionBtn);

        Intent intent = getIntent();
        if (intent!=null) {
            scanCardSKUNumber = intent.getStringExtra("scanCardSKUNumber");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        playBackgroudVideo();
        try {
            if (audioUri != null) {
                playAudio(audioUri);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        killMediaPlayer();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (firstLocalVideo != null) {
            File file = new File(firstLocalVideo.getPath());
            file.delete();
            firstLocalVideo = null;
            if (file.exists()) {
                try {
                    file.getCanonicalFile().delete();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (file.exists()) {
                    getApplicationContext().deleteFile(file.getName());
                }

            }
        }

        if (secondLocalVideo != null) {
            File file = new File(secondLocalVideo.getPath());
            file.delete();
            secondLocalVideo = null;
            if (file.exists()) {
                try {
                    file.getCanonicalFile().delete();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (file.exists()) {
                    getApplicationContext().deleteFile(file.getName());
                }

            }
        }

        if (thirdLocalVideo != null) {
            File file = new File(thirdLocalVideo.getPath());
            file.delete();
            thirdLocalVideo = null;
            if (file.exists()) {
                try {
                    file.getCanonicalFile().delete();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (file.exists()) {
                    getApplicationContext().deleteFile(file.getName());
                }

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionBtn.setVisibility(View.GONE);
                    dialog.show();
                    actioOnGetAudioUrl();
                    getVideoUrlsFromServer();
                } else {
                    // Permission Denied
                    permissionBtn.setVisibility(View.VISIBLE);
                    Toast.makeText(ScanCardActivity.this, "We cannot load the video without giving permissiong.", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                permissionBtn.setVisibility(View.VISIBLE);
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && !dialog.isShowing()) {

            if (currentVideoPlaying == 1) {
                vibratePhone();
                if (nextPlay == 2) {
                    currentVideoPlaying = 2;
                    nextPlay = 3;
                    startTimer();
                    playBackgroudVideo();
                } else if (nextPlay == 3) {
                    currentVideoPlaying = 3;
                    nextPlay = 4;
                    startTimer();
                    playBackgroudVideo();
                } else if (nextPlay == 4) {
                    nextPlay = 2;
                    currentVideoPlaying = 1;
                    if (redirect_url != null) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(redirect_url));
                        startActivity(browserIntent);
                    }
                }
            }
        }
        return super.onTouchEvent(event);
    }

    public void actionOnCheckPermission(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            permissionBtn.setVisibility(View.GONE);
            if (isNetworkConnected()) {
                dialog.show();
                actioOnGetAudioUrl();
                getVideoUrlsFromServer();
            } else {
                permissionBtn.setVisibility(View.VISIBLE);
                permissionBtn.setText("Try Again");
                Toast.makeText(this, "There is no internet available. Please connect internet", Toast.LENGTH_LONG).show();
            }
        } else {
            permissionBtn.setVisibility(View.VISIBLE);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private Timer mTimer1;
    private TimerTask mTt1;
    private Handler mTimerHandler = new Handler();

    private Timer mTimer2;
    private TimerTask mTt2;
    private Handler m2TimerHandler = new Handler();


    private void stopTimer() {
        if (mTimer1 != null) {
            mTimer1.cancel();
            mTimer1.purge();
        }
    }

    private void stopM2Timer() {
        if (mTimer2 != null) {
            mTimer2.cancel();
            mTimer2.purge();
        }
    }

    private void startTimer() {
        mTimer1 = new Timer();
        mTt1 = new TimerTask() {
            public void run() {
                mTimerHandler.post(new Runnable() {
                    public void run() {
                        currentVideoPlaying = 1;
                        playBackgroudVideo();
                        stopTimer();
                    }
                });
            }
        };

        mTimer1.schedule(mTt1, 5000, 15000);
    }

    private void startTimerForMiddleView() {
        mTimer2 = new Timer();
        mTt2 = new TimerTask() {
            public void run() {
                m2TimerHandler.post(new Runnable() {
                    public void run() {
                        fullScreeGifView.setVisibility(View.GONE);
                        stopM2Timer();
                    }
                });
            }
        };

        mTimer2.schedule(mTt2, 1000, 1000);
    }

    // Initialize Methods
    public void playBackgroudVideo() {
        if (videoUrls.isEmpty()) {
            return;
        }

        vvVideo.setVideoURI(videoUrls.get(currentVideoPlaying - 1).uri);
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

    public void playAudio(Uri url) throws Exception {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, url);
            mediaPlayer.setLooping(true);
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
            }
        });
        mediaPlayer.start();
    }

    private void killMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void vibratePhone() {
        fullScreeGifView.setVisibility(View.VISIBLE);
        startTimerForMiddleView();

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(1000);
        }
    }


    public void hideProgressView() {
        if (dialog.isShowing() && videoJsonArray.length() == videoUrls.size()) {
            dialog.dismiss();
            playBackgroudVideo();
        }
    }

    //------

    public void actioOnGetAudioUrl() {
        String url = "https://uvapps.youniquevoices.com/index.php/Uvappsapicontrol/backgroundmusicaccess";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Response", response.toString());

                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray array = obj.getJSONArray("musicfile");

                            if (array.length() != 0) {
                                audioUrl = array.getJSONObject(0).getString("bgmusic_file_url");
                                downloadAudioFromUrl();
                            }
                        } catch (JSONException e) {
                            Log.e("MYAPP", "Email Address or Password is wrong.", e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Response", error.toString());
                    }
                }) {
        };

        requestQueue.add(request);
    }


    private void downloadAudioFromUrl() {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    URL url = new URL(audioUrl);
                    HttpURLConnection c = (HttpURLConnection) url.openConnection();
                    c.setRequestMethod("GET");
                    c.setDoOutput(true);
                    c.connect();

                    String PATH = Environment.getExternalStorageDirectory().toString()
                            + "/load";
                    Log.v("LOG_TAG", "PATH: " + PATH);

                    File file = new File(PATH);
                    file.mkdirs();
                    File outputFile = new File(file, "background_audio.mp3");
                    FileOutputStream fos = new FileOutputStream(outputFile);
                    InputStream is = c.getInputStream();

                    byte[] buffer = new byte[4096];
                    int len1 = 0;

                    while ((len1 = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, len1);
                    }

                    fos.close();
                    is.close();
                    audioUri = Uri.parse(Environment.getExternalStorageDirectory().toString() + "/load/" + "background_audio.mp3");
                    try {
                        if (audioUri != null) {
                            playAudio(audioUri);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } catch (final IOException e) {
                    e.printStackTrace();
                }

            }
        });

        thread.start();
    }


    public void getVideoUrlsFromServer() {
        String url = "https://uvapps.youniquevoices.com/index.php/Uvappsapicontrol/scanacardvideoaccess";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Response", response.toString());
                        try {
                            JSONObject obj = new JSONObject(response);
                            String message = obj.getString("message");
                            if (obj.isNull("videodetails")) {
                                hideProgressView();
                                killMediaPlayer();
                                AlertDialog.Builder builder;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    builder = new AlertDialog.Builder(ScanCardActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                                } else {
                                    builder = new AlertDialog.Builder(ScanCardActivity.this);
                                }
                                builder.setTitle("Alert")
                                        .setMessage(message)
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            } else {
                                redirect_url = obj.getString("redirect_url");
                                videoJsonArray = obj.getJSONArray("videodetails");

                                for (int i = 0, size = videoJsonArray.length(); i < size; i++) {
                                    downloadAllVideos(videoJsonArray.getJSONObject(i).getString("scancard_video_url"), videoJsonArray.getJSONObject(i).getString("scancard_id"));
                                }
                            }
                        } catch (JSONException e) {
                            Log.e("MYAPP", "Email Address or Password is wrong.", e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Response", error.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("scancardskunumber", scanCardSKUNumber != null ? scanCardSKUNumber : "");
                return params;
            }
        };

        requestQueue.add(request);
    }

    private void downloadAllVideos(final String videoUrl, final String name) {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    URL url = new URL(videoUrl);
                    HttpURLConnection c = (HttpURLConnection) url.openConnection();
                    c.setRequestMethod("GET");
                    c.setDoOutput(true);
                    c.connect();

                    String PATH = Environment.getExternalStorageDirectory().toString()
                            + "/load";
                    Log.v("LOG_TAG", "PATH: " + PATH);

                    File file = new File(PATH);
                    file.mkdirs();
                    File outputFile = new File(file, name + ".mp4");
                    FileOutputStream fos = new FileOutputStream(outputFile);
                    InputStream is = c.getInputStream();

                    byte[] buffer = new byte[4096];
                    int len1 = 0;

                    while ((len1 = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, len1);
                    }

                    fos.close();
                    is.close();
                    VideoURI videoURI = new VideoURI();
                    videoURI.name = name;
                    videoURI.uri = Uri.parse(Environment.getExternalStorageDirectory().toString() + "/load/" + name + ".mp4");
                    videoUrls.add(videoURI);
                    Collections.sort(videoUrls, new CustomComparator());
                    runOnUiThread(new Runnable() {
                        public void run() {
                            hideProgressView();
                        }
                    });
                } catch (final IOException e) {
                    e.printStackTrace();
                }

            }
        });

        thread.start();
    }
}

class CustomComparator implements Comparator<VideoURI> {
    @Override
    public int compare(VideoURI o1, VideoURI o2) {
        int namevalue = Integer.parseInt(o1.name);
        int nameValue2 = Integer.parseInt(o2.name);
        return o1.name.compareTo(o2.name);
    }
}

class VideoURI {
    String name = "";
    Uri uri;
}