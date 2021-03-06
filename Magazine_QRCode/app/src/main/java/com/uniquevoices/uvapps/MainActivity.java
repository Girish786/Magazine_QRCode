package com.uniquevoices.uvapps;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.VideoView;

//import com.crashlytics.android.Crashlytics;
//
//import io.fabric.sdk.android.Fabric;


public class MainActivity extends AppCompatActivity {

    VideoView vvVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // [START crash_enable_debug_mode]
//        final Fabric fabric = new Fabric.Builder(this)
//                .kits(new Crashlytics())
//                .debuggable(true)  // Enables Crashlytics debugger
//                .build();
//        Fabric.with(fabric);
//        // [END crash_enable_debug_mode]
//        Fabric.with(this, new Crashlytics());
        playBackgroudVideo();

        Toolbar topToolBar = (Toolbar) findViewById(R.id.toptoolbar);
        setSupportActionBar(topToolBar);
        ActionBar actionBar = getSupportActionBar();
        ;
        actionBar.setDisplayHomeAsUpEnabled(false);
    }


    @Override
    protected void onResume() {
        super.onResume();
        playBackgroudVideo();
    }

    @Override
    public void onBackPressed() {
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
    public void actionOnScanACard(View v) {
        Intent cardActivityIntent = new Intent(getApplicationContext(), CardMainActivity.class);
        startActivity(cardActivityIntent);

//        Intent cardActivityIntent = new Intent(getApplicationContext(), VideoDownloaderActivity.class);
//        startActivity(cardActivityIntent);
    }

    public void actionOnChatWithFriends(View v) {
        Intent cardActivityIntent = new Intent(getApplicationContext(), ChatMainActivity.class);
        startActivity(cardActivityIntent);
    }

    public void actionOnScanAMagazine(View v) {
        Intent goToNextActivity = new Intent(getApplicationContext(), QRCodeScanActivity.class);
        startActivity(goToNextActivity);
    }

    public void actionOnWorldRadio(View v) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://radio.garden/"));
        startActivity(browserIntent);
//        Intent goToNextActivity = new Intent(getApplicationContext(), WebViewActivity.class);
//        startActivity(goToNextActivity);
    }

    public void actionOnLogOut(View v) {
//        PrefManager.getInstance(getApplicationContext()).setUserId("");
//        PrefManager.getInstance(getApplicationContext()).setUserName("");
//        ActivityManager mngr = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
//        List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);
//        if (taskList.get(0).numActivities == 1 && taskList.get(0).topActivity.getClassName().equals(this.getClass().getName())) {
            Intent goToNextActivity = new Intent(getApplicationContext(), LogginActivity.class);
            startActivity(goToNextActivity);
//        } else {
//            finish();
//        }
    }
}
