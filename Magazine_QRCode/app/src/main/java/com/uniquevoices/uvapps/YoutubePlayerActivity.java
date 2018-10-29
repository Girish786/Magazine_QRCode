package com.uniquevoices.uvapps;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class YoutubePlayerActivity extends YouTubeBaseActivity {

    String videoID = "";
    private MyPlayerStateChangeListener myPlayerStateChangeListener;

    @Override
    public void onBackPressed() {
        goToMainActivity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_player);

        myPlayerStateChangeListener = new MyPlayerStateChangeListener();
        myPlayerStateChangeListener.context = YoutubePlayerActivity.this;

        Intent intent = getIntent();
        videoID = intent.getStringExtra("urlKey");

        YouTubePlayerView youTubePlayerView =
                (YouTubePlayerView) findViewById(R.id.youtube_player);

        youTubePlayerView.initialize("AIzaSyChtqxu-SLXP0R9oI2vdc6X8j2i1NVP_mo",
                new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                        YouTubePlayer youTubePlayer, boolean b) {
                        youTubePlayer.setPlayerStateChangeListener(myPlayerStateChangeListener);
                        youTubePlayer.loadVideo(videoID);
                    }
                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                        YouTubeInitializationResult youTubeInitializationResult) {

                    }


                });
    }

    public void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}



final class MyPlayerStateChangeListener implements YouTubePlayer.PlayerStateChangeListener {

    public static YoutubePlayerActivity context;

    private void updateLog(String prompt){
//        log +=  "MyPlayerStateChangeListeneristener" + "\n" +
//                prompt + "\n\n=====";
    };

    @Override
    public void onAdStarted() {
        updateLog("onAdStarted()");
    }

    @Override
    public void onError(
            com.google.android.youtube.player.YouTubePlayer.ErrorReason arg0) {
        updateLog("onError(): " + arg0.toString());
    }

    @Override
    public void onLoaded(String arg0) {
        updateLog("onLoaded(): " + arg0);
    }

    @Override
    public void onLoading() {
        updateLog("onLoading()");
    }

    @Override
    public void onVideoEnded() {
        context.goToMainActivity();
    }

    @Override
    public void onVideoStarted() {
        updateLog("onVideoStarted()");
    }

}