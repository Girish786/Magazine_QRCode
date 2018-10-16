package com.qrcode.sakthikumar.magazineqrcodescanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.blikoon.qrcodescanner.QrCodeActivity;

public class CardMainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_QR_SCAN = 101;
    public String versionNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_main);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode != Activity.RESULT_OK) {
            Log.d("","COULD NOT GET A GOOD RESULT.");
            if(data==null)
                return;
            String result = data.getStringExtra("com.blikoon.qrcodescanner.error_decoding_image");
            if( result!=null)
            {
                AlertDialog alertDialog = new AlertDialog.Builder(CardMainActivity.this).create();
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

        if(requestCode == REQUEST_CODE_QR_SCAN) {
            if(data==null)
                return;
            String result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
            Log.d("","Have scan result in your app activity :"+ result);
            Intent intent = new Intent(CardMainActivity.this, ScanCardActivity.class);
            intent.putExtra("scanCardSKUNumber", result);
            intent.putExtra("versionNumber", versionNumber);
            startActivity(intent);
        }
    }

    //Button Action Methods
    public void actionOnVersionOne(View view) {
        versionNumber = "1";
        Intent i = new Intent(CardMainActivity.this, QrCodeActivity.class);
        startActivityForResult( i,REQUEST_CODE_QR_SCAN);
    }

    public void actionOnVersionTwo(View view) {
        versionNumber = "2";
        Intent i = new Intent(CardMainActivity.this, QrCodeActivity.class);
        startActivityForResult( i,REQUEST_CODE_QR_SCAN);
    }

    public void actionOnVersionThree(View view) {
        versionNumber = "3";
        Intent i = new Intent(CardMainActivity.this, QrCodeActivity.class);
        startActivityForResult( i,REQUEST_CODE_QR_SCAN);
    }

//    public void actionOnVersionFour(View view) {
//        versionNumber = "4";
//        Intent i = new Intent(CardMainActivity.this, DecoderActivity.class);
//        startActivityForResult( i,REQUEST_CODE_QR_SCAN);
//    }

    public void actionOnCancel(View view) {
        finish();
    }
}
