package com.uniquevoices.uvapps;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;

public class VideoDownloaderActivity extends AppCompatActivity {

    TextView progress_one, progress_two, progress_three, progress_four, progress_five, progress_six;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_downloader);
        progress_one = (TextView) findViewById(R.id.progress_one);
        progress_two = (TextView) findViewById(R.id.progress_two);
        progress_three = (TextView) findViewById(R.id.progress_three);
        progress_four = (TextView) findViewById(R.id.progress_four);
        progress_six = (TextView) findViewById(R.id.progress_six);
        progress_five = (TextView) findViewById(R.id.progress_five);
    }

    public void startDownloadButton(View view) {
        downloadVideo_one();
        downloadVideo_two();
        downloadVideo_three();
        downloadVideo_four();
        downloadVideo_five();
        downloadVideo_six();
    }

    public void downloadVideo_one() {
        progress_one.setText("Starting...");
        String url = "http://uvstaging.youniquevoice.com/uvmobileappsrequirments/demoappforvideotest/VideoAnimationLatest1.mp4";
        String PATH = Environment.getExternalStorageDirectory().toString() + "/load";
        AndroidNetworking.download(url, PATH, "number_1")
                .setTag("downloadTest")
                .setPriority(Priority.MEDIUM)
                .build()
                .setDownloadProgressListener(new DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes) {
                        // do anything with progress
                        int progress = (int) (bytesDownloaded * 100 / totalBytes);
                        progress_one.setText("Progress = " + progress);
                    }
                })
                .startDownload(new DownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        // do anything after completion
                        progress_one.setText("Downloading Completed.");
                    }

                    @Override
                    public void onError(ANError error) {
                        Toast.makeText(VideoDownloaderActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void downloadVideo_two() {
        progress_two.setText("Starting...");
        String url = "http://uvstaging.youniquevoice.com/uvmobileappsrequirments/demoappforvideotest/VideoAnimationLatest2.mp4";
        String PATH = Environment.getExternalStorageDirectory().toString() + "/load";
        AndroidNetworking.download(url, PATH, "number_2")
                .setTag("downloadTest")
                .setPriority(Priority.MEDIUM)
                .build()
                .setDownloadProgressListener(new DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes) {
                        // do anything with progress
                        int progress = (int) (bytesDownloaded * 100 / totalBytes);
                        progress_two.setText("Progress = " + progress);
                    }
                })
                .startDownload(new DownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        // do anything after completion
                        progress_two.setText("Downloading Completed.");
                    }

                    @Override
                    public void onError(ANError error) {
                        Toast.makeText(VideoDownloaderActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void downloadVideo_three() {
        progress_three.setText("Starting...");
        String url = "http://uvstaging.youniquevoice.com/uvmobileappsrequirments/demoappforvideotest/VideoAnimationLatest3.mp4";
        String PATH = Environment.getExternalStorageDirectory().toString() + "/load";
        AndroidNetworking.download(url, PATH, "number_3")
                .setTag("downloadTest")
                .setPriority(Priority.MEDIUM)
                .build()
                .setDownloadProgressListener(new DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes) {
                        // do anything with progress
                        int progress = (int) (bytesDownloaded * 100 / totalBytes);
                        progress_three.setText("Progress = " + progress);
                    }
                })
                .startDownload(new DownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        // do anything after completion
                        progress_three.setText("Downloading Completed.");
                    }

                    @Override
                    public void onError(ANError error) {
                        Toast.makeText(VideoDownloaderActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void downloadVideo_four() {
        progress_four.setText("Starting...");
        String url = "http://uvstaging.youniquevoice.com/uvmobileappsrequirments/demoappforvideotest/VideoAnimationLatest4.mp4";
        String PATH = Environment.getExternalStorageDirectory().toString() + "/load";
        AndroidNetworking.download(url, PATH, "number_4")
                .setTag("downloadTest")
                .setPriority(Priority.MEDIUM)
                .build()
                .setDownloadProgressListener(new DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes) {
                        // do anything with progress
                        int progress = (int) (bytesDownloaded * 100 / totalBytes);
                        progress_four.setText("Progress = " + progress);
                    }
                })
                .startDownload(new DownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        // do anything after completion
                        progress_four.setText("Downloading Completed.");
                    }

                    @Override
                    public void onError(ANError error) {
                        Toast.makeText(VideoDownloaderActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void downloadVideo_five() {
        progress_five.setText("Starting...");
        String url = "http://uvstaging.youniquevoice.com/uvmobileappsrequirments/demoappforvideotest/VideoAnimationLatest5.mp4";
        String PATH = Environment.getExternalStorageDirectory().toString() + "/load";
        AndroidNetworking.download(url, PATH, "number_5")
                .setTag("downloadTest")
                .setPriority(Priority.MEDIUM)
                .build()
                .setDownloadProgressListener(new DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes) {
                        // do anything with progress
                        int progress = (int) (bytesDownloaded * 100 / totalBytes);
                        progress_five.setText("Progress = " + progress);
                    }
                })
                .startDownload(new DownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        // do anything after completion
                        progress_five.setText("Downloading Completed.");
                    }

                    @Override
                    public void onError(ANError error) {
                        Toast.makeText(VideoDownloaderActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void downloadVideo_six() {
        progress_six.setText("Starting...");
        String url = "http://uvstaging.youniquevoice.com/uvmobileappsrequirments/demoappforvideotest/Adele_Artist_Video_1.mp4";
        String PATH = Environment.getExternalStorageDirectory().toString() + "/load";
        AndroidNetworking.download(url, PATH, "number_6")
                .setTag("downloadTest")
                .setPriority(Priority.MEDIUM)
                .build()
                .setDownloadProgressListener(new DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes) {
                        // do anything with progress
                        int progress = (int) (bytesDownloaded * 100 / totalBytes);
                        progress_six.setText("Progress = " + progress);
                    }
                })
                .startDownload(new DownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        // do anything after completion
                        progress_six.setText("Downloading Completed.");
                    }

                    @Override
                    public void onError(ANError error) {
                        Toast.makeText(VideoDownloaderActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}