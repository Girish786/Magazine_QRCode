package com.uniquevoices.uvapps;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class VimeoPlayerActivity extends AppCompatActivity {

    WebView browser;
    String frame;
    ProgressDialog progressDialog;

    @Override
    public void onBackPressed() {
        goToMainActivity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vimeo_player);

        Intent intent = getIntent();
        String video_url_key = intent.getStringExtra("urlKey");

        frame = "<iframe src=\"https://player.vimeo.com/video/"+video_url_key+"\" width=\"100%\" height=\"100%\" autoplay=\"1\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>";

        browser = (WebView) findViewById(R.id.webView);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.getSettings().setAppCacheEnabled(true);
        browser.getSettings().setDomStorageEnabled(true);
        browser.getSettings().setPluginState(WebSettings.PluginState.ON);
        browser.loadData(frame, "text/html", "UTF-8");

        progressDialog = new ProgressDialog(VimeoPlayerActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        browser.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadData(frame, "text/html", "UTF-8");
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(VimeoPlayerActivity.this, "Error:" + description, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(browser != null){
            browser.onPause();
            browser.pauseTimers();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(browser != null){
            browser.onResume();
            browser.resumeTimers();
        }
    }

    public void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
