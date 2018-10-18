package com.qrcode.sakthikumar.magazineqrcodescanner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        WebView browser = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = browser.getSettings();
        webSettings.setJavaScriptEnabled(true);
        browser.loadUrl("http://radio.garden/live/");
    }
}
