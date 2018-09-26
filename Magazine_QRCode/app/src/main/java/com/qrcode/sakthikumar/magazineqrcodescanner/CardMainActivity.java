package com.qrcode.sakthikumar.magazineqrcodescanner;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class CardMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_main);
    }

    //Button Action Methods
    public void actionOnVersionOne(View view) {
        Intent scanCardActivity = new Intent(getApplicationContext(), ScanCardActivity.class);
        startActivity(scanCardActivity);
    }

    public void actionOnVersionTwo(View view) {
        Intent scanCardActivity = new Intent(getApplicationContext(), ScanCardActivity.class);
        startActivity(scanCardActivity);
    }

    public void actionOnVersionThree(View view) {
        Intent scanCardActivity = new Intent(getApplicationContext(), ScanCardActivity.class);
        startActivity(scanCardActivity);
    }

    public void actionOnCancel(View view) {
        finish();
    }
}
