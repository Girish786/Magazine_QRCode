package com.qrcode.sakthikumar.magazineqrcodescanner;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
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
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
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
    Boolean isAllDownloaded = false;

    static Uri firstLocalVideo;
    static Uri secondLocalVideo;
    static Uri thirdLocalVideo;
    static Uri fourthLocalVideo;
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
    String versionNumber = "";

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
        if (intent != null) {
            scanCardSKUNumber = intent.getStringExtra("scanCardSKUNumber");
            versionNumber = intent.getStringExtra("versionNumber");
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
    protected void onDestroy() {
        super.onDestroy();
        audioUri = null;
        audioUrl = null;
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

        if (fourthLocalVideo != null) {
            File file = new File(fourthLocalVideo.getPath());
            file.delete();
            fourthLocalVideo = null;
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
//                    dialog.show();
                    playLoaderVideo();
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
        if (event.getAction() == MotionEvent.ACTION_DOWN && !dialog.isShowing() && (videoJsonArray.length() + 1) == videoUrls.size() && audioUri != null && isAllDownloaded) {
            if (versionNumber.equals("1")) {
                if (currentVideoPlaying <= 3) {
                    currentVideoPlaying = 6;
                    playBackgroudVideo();
                } else if (currentVideoPlaying == 6) {
                    currentVideoPlaying = 1;
                    if (redirect_url != null) {
                        vibratePhone();
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(redirect_url));
                        startActivity(browserIntent);
                    }
                }
            } else if (versionNumber.equals("2")) {
                if (currentVideoPlaying == 1) {
                    currentVideoPlaying = 2;
                    playBackgroudVideo();
                } else if (currentVideoPlaying == 2) {
                    currentVideoPlaying = 3;
                    playBackgroudVideo();
                } else if (currentVideoPlaying == 3) {
                    currentVideoPlaying = 6;
                    playBackgroudVideo();
                } else if (currentVideoPlaying == 6) {
                    currentVideoPlaying = 1;
                    if (redirect_url != null) {
                        vibratePhone();
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(redirect_url));
                        startActivity(browserIntent);
                    }
                }
            } else if (versionNumber.equals("3")) {
                if (currentVideoPlaying <= 5) {
                    currentVideoPlaying = 6;
                    playBackgroudVideo();
                } else if (currentVideoPlaying == 6) {
                    currentVideoPlaying = 1;
                    if (redirect_url != null) {
                        vibratePhone();
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(redirect_url));
                        startActivity(browserIntent);
                    }
                }
            }


            /*
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
                    currentVideoPlaying = 4;
                    nextPlay = 5;
                    startTimer();
                    playBackgroudVideo();
                } else if (nextPlay == 5) {
                    nextPlay = 2;
                    currentVideoPlaying = 1;
                    if (redirect_url != null) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(redirect_url));
                        startActivity(browserIntent);
                    }
                }
            }*/
        }
        return super.onTouchEvent(event);
    }

    public void actionOnCheckPermission(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            permissionBtn.setVisibility(View.GONE);
            if (isNetworkConnected()) {
//                dialog.show();
//                actioOnGetAudioUrl();
                playLoaderVideo();
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
//                        fullScreeGifView.setVisibility(View.GONE);
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

        Log.i("", "CURRENT VIDEO PLAYING INDEX: " + currentVideoPlaying);
        vibratePhone();
        vvVideo.setVideoURI(videoUrls.get(currentVideoPlaying - 1).uri);
        vvVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setVolume(0, 0);
                mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
                mp.setLooping(false);
                vvVideo.start();
            }
        });

        vvVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
//                if (currentVideoPlaying <= 5) {
//                    currentVideoPlaying += 1;
//                    playBackgroudVideo();
//                } else if (currentVideoPlaying == 6) {
//                    currentVideoPlaying = 1;
//                    if (redirect_url != null) {
//                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(redirect_url));
//                        startActivity(browserIntent);
//                    }
//                }


                if (versionNumber.equals("1")) {
                    if (currentVideoPlaying <= 2) {
                        currentVideoPlaying += 1;
                        playBackgroudVideo();
                    } else if (currentVideoPlaying == 3) {
                        currentVideoPlaying = 6;
                        playBackgroudVideo();
                    } else if (currentVideoPlaying == 6) {
                        currentVideoPlaying = 1;
                        if (redirect_url != null) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(redirect_url));
                            startActivity(browserIntent);
                        }
                    }
                } else if (versionNumber.equals("2")) {
                    if (currentVideoPlaying == 1) {
                        currentVideoPlaying = 2;
                        playBackgroudVideo();
                    } else if (currentVideoPlaying == 6) {
                        currentVideoPlaying = 1;
                        if (redirect_url != null) {
                            vibratePhone();
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(redirect_url));
                            startActivity(browserIntent);
                        }
                    } else {
                        currentVideoPlaying = 6;
                        playBackgroudVideo();
                    }
                } else if (versionNumber.equals("3")) {
                    if (currentVideoPlaying <= 5) {
                        currentVideoPlaying += 1;
                        playBackgroudVideo();
                    } else if (currentVideoPlaying == 6) {
                        currentVideoPlaying = 1;
                        if (redirect_url != null) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(redirect_url));
                            startActivity(browserIntent);
                        }
                    }
                }
            }
        });
    }

    public void playLoaderVideo() {
        vvVideo.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.loading_video));

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
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(versionNumber.equals("3") ? 2000 : 1000, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(versionNumber.equals("3") ? 2000 : 1000);
        }
    }


    public void hideProgressView() {
        if ((videoJsonArray.length() + 1) == videoUrls.size() && audioUri != null && !isAllDownloaded) {
//            dialog.dismiss();
//            Toast.makeText(ScanCardActivity.this, "Downloaded all the videos and audio and ready to play...", Toast.LENGTH_SHORT).show();
            isAllDownloaded = true;
            playBackgroudVideo();

            try {
                playAudio(audioUri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //------

//    public void actioOnGetAudioUrl() {
//        String url = "https://uvapps.youniquevoices.com/index.php/Uvappsapicontrol/backgroundmusicaccess";
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//
//        StringRequest request = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.e("Response", response.toString());
//
//                        try {
//                            JSONObject obj = new JSONObject(response);
//                            JSONArray array = obj.getJSONArray("musicfile");
//
//                            if (array.length() != 0) {
//                                audioUrl = array.getJSONObject(0).getString("bgmusic_file_url");
//                                downloadAudioFromUrl();
//                            }
//                        } catch (JSONException e) {
//                            Log.e("MYAPP", "Email Address or Password is wrong.", e);
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.e("Response", error.toString());
//                    }
//                }) {
//        };
//
//        requestQueue.add(request);
//    }


    private void downloadAudioFromUrl() {
//        Toast.makeText(ScanCardActivity.this, "Start downloading audio url: ", Toast.LENGTH_SHORT).show();
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
//                    try {
//                        if ((videoJsonArray.length() + 1) == videoUrls.size() && (audioUri != null)) {
//                            playAudio(audioUri);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }

                    runOnUiThread(new Runnable() {
                        public void run() {
//                            Toast.makeText(ScanCardActivity.this, "Got Audio file from server: ", Toast.LENGTH_SHORT).show();
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


    public void getVideoUrlsFromServer() {
//        Toast.makeText(this, "Started calling get videos url api....", Toast.LENGTH_SHORT).show();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, Constants.SCAN_CARD_VIDEO_ACCESS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Response", response.toString());
                        try {
//                            Toast.makeText(ScanCardActivity.this, "Got all videos urls from api....", Toast.LENGTH_SHORT).show();
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
//                                Toast.makeText(ScanCardActivity.this, "Got redirect_url from api....", Toast.LENGTH_SHORT).show();
                                switch (versionNumber) {
                                    case "1":
                                        audioUrl = obj.getString("bgmusic_url");
                                        downloadAudioFromUrl();
                                        downloadVideothrdPartyApi(obj.getString("downloaded_artist_video"), "6");
                                        break;

                                    case "2":
                                        audioUrl = "http://uvstaging.youniquevoice.com/uvmobileappsrequirments/demoappforvideotest/Adele_Song_2.mp3";
                                        downloadAudioFromUrl();
                                        downloadVideothrdPartyApi("http://uvstaging.youniquevoice.com/uvmobileappsrequirments/demoappforvideotest/Adele_Artist_Video_2.mp4", "6");
                                        break;

                                    case "3":
                                        audioUrl = "http://uvstaging.youniquevoice.com/uvmobileappsrequirments/demoappforvideotest/Adele_Song_2.mp3";
                                        downloadAudioFromUrl();
                                        downloadVideothrdPartyApi("http://uvstaging.youniquevoice.com/uvmobileappsrequirments/demoappforvideotest/Adele_Artist_Video_2.mp4", "6");
                                        break;

                                    case "4":
                                        audioUrl = "http://uvstaging.youniquevoice.com/uvmobileappsrequirments/demoappforvideotest/adelemusic4.mp3";
                                        downloadAudioFromUrl();
                                        downloadVideothrdPartyApi(obj.getString("downloaded_artist_video"), "6");
                                        break;

                                    default:
                                        audioUrl = obj.getString("bgmusic_url");
                                        downloadAudioFromUrl();
                                        break;
                                }

                                videoJsonArray = obj.getJSONArray("videodetails");
                                for (int i = 0, size = videoJsonArray.length(); i < size; i++) {
                                    downloadVideothrdPartyApi(videoJsonArray.getJSONObject(i).getString("scancard_video_url"), videoJsonArray.getJSONObject(i).getString("scancard_serial_no"));
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

    public void downloadVideothrdPartyApi(final String videoUrl, final String name) {
        String PATH = Environment.getExternalStorageDirectory().toString() + "/load";
        AndroidNetworking.download(videoUrl, PATH, name + ".mp4")
                .setTag("downloadTest")
                .setPriority(Priority.MEDIUM)
                .build()
                .setDownloadProgressListener(new DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes) {
//                        int progress = (int) (bytesDownloaded * 100 / totalBytes);
//                        progress_three.setText("Progress = " + progress);
                    }
                })
                .startDownload(new DownloadListener() {
                    @Override
                    public void onDownloadComplete() {
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
                    }

                    @Override
                    public void onError(ANError error) {
//                        Toast.makeText(VideoDownloaderActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void downloadAllVideos(final String videoUrl, final String name) {
//        Toast.makeText(ScanCardActivity.this, "Started video downloading: " + name, Toast.LENGTH_SHORT).show();
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
//                    arrangeUrlsBasedOnVersionNumber();
                    runOnUiThread(new Runnable() {
                        public void run() {
//                            Toast.makeText(ScanCardActivity.this, "Got video: " + name, Toast.LENGTH_SHORT).show();
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

    public ArrayList<VideoURI> arragenVideos_one(ArrayList<VideoURI> versione) {
        ArrayList<VideoURI> versionOne = versione;
        for (VideoURI uri : videoUrls) {
            if (uri.name.equals("1")) {
                if (versionOne.isEmpty()) {
                    versionOne.add(uri);
                }
            } else if (uri.name.equals("2")) {
                if (versionOne.size() == 1) {
                    versionOne.add(uri);
                }
            } else if (uri.name.equals("3")) {
                if (versionOne.size() == 2) {
                    versionOne.add(uri);
                }
            } else if (uri.name.equals("4")) {
                if (versionOne.size() == 3) {
                    versionOne.add(uri);
                }
            }
        }

        if (versionOne.size() != 4) {
            arragenVideos_one(versionOne);
        }
        return versionOne;
    }

    public ArrayList<VideoURI> arragenVideos_two(ArrayList<VideoURI> versione) {
        ArrayList<VideoURI> versionTwo = versione;
        for (VideoURI uri : videoUrls) {
            if (uri.name.equals("2")) {
                if (versionTwo.isEmpty()) {
                    versionTwo.add(uri);
                }
            } else if (uri.name.equals("3")) {
                if (versionTwo.size() == 1) {
                    versionTwo.add(uri);
                }
            } else if (uri.name.equals("4")) {
                if (versionTwo.size() == 2) {
                    versionTwo.add(uri);
                }
            } else if (uri.name.equals("1")) {
                if (versionTwo.size() == 3) {
                    versionTwo.add(uri);
                }
            }
        }

        if (versionTwo.size() != 4) {
            arragenVideos_two(versionTwo);
        }
        return versionTwo;
    }

    public ArrayList<VideoURI> arragenVideos_three(ArrayList<VideoURI> versione) {
        ArrayList<VideoURI> versionThree = versione;
        for (VideoURI uri : videoUrls) {
            if (uri.name.equals("3")) {
                if (versionThree.isEmpty()) {
                    versionThree.add(uri);
                }
            } else if (uri.name.equals("4")) {
                if (versionThree.size() == 1) {
                    versionThree.add(uri);
                }
            } else if (uri.name.equals("1")) {
                if (versionThree.size() == 2) {
                    versionThree.add(uri);
                }
            } else if (uri.name.equals("2")) {
                if (versionThree.size() == 3) {
                    versionThree.add(uri);
                }
            }
        }

        if (versionThree.size() != 4) {
            arragenVideos_three(versionThree);
        }
        return versionThree;
    }

    public ArrayList<VideoURI> arragenVideos_four(ArrayList<VideoURI> versione) {
        ArrayList<VideoURI> versionFour = versione;
        for (VideoURI uri : videoUrls) {
            if (uri.name.equals("4")) {
                if (versionFour.isEmpty()) {
                    versionFour.add(uri);
                }
            } else if (uri.name.equals("3")) {
                if (versionFour.size() == 1) {
                    versionFour.add(uri);
                }
            } else if (uri.name.equals("2")) {
                if (versionFour.size() == 2) {
                    versionFour.add(uri);
                }
            } else if (uri.name.equals("1")) {
                if (versionFour.size() == 3) {
                    versionFour.add(uri);
                }
            }
        }

        if (versionFour.size() != 4) {
            arragenVideos_four(versionFour);
        }
        return versionFour;
    }


    public void arrangeUrlsBasedOnVersionNumber() {
        if (videoUrls.size() != 4) {
            return;
        }

        switch (versionNumber) {
            case "1":
                videoUrls = arragenVideos_one(new ArrayList<VideoURI>());
                Log.i("", "arrangeUrlsBasedOnVersionNumber: ");
                break;

            case "2":
                videoUrls = arragenVideos_two(new ArrayList<VideoURI>());
                Log.i("", "arrangeUrlsBasedOnVersionNumber: ");
                break;

            case "3":
                videoUrls = arragenVideos_three(new ArrayList<VideoURI>());
                Log.i("", "arrangeUrlsBasedOnVersionNumber: ");
                break;

            case "4":
                videoUrls = arragenVideos_four(new ArrayList<VideoURI>());
                Log.i("", "arrangeUrlsBasedOnVersionNumber: ");
                break;

            default:
                break;
        }
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