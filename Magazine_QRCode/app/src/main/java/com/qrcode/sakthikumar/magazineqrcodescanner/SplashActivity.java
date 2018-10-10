package com.qrcode.sakthikumar.magazineqrcodescanner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        String emaidId = PrefManager.getInstance(getApplicationContext()).getUserId();
        Intent intent;

        if (emaidId != null && emaidId != "") {
            intent = new Intent(this,MainActivity.class);
        } else {
            intent = new Intent(this,LogginActivity.class);
        }

        startActivity(intent);
        finish();
    }
}
