package com.uniquevoices.uvapps;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

public class DecoderActivity extends AppCompatActivity implements QRCodeReaderView.OnQRCodeReadListener {

    private QRCodeReaderView qrCodeReaderView;
    private PointsOverlayView pointsOverlayView;
    private CheckBox flashlightCheckBox;
    private final String GOT_RESULT = "com.blikoon.qrcodescanner.got_qr_scan_relult";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decoder);

        flashlightCheckBox = (CheckBox) findViewById(R.id.flashlight_checkbox);
        pointsOverlayView = (PointsOverlayView) findViewById(R.id.points_overlay_view);
        qrCodeReaderView = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
        qrCodeReaderView.setOnQRCodeReadListener(this);

        // Use this function to enable/disable decoding
        qrCodeReaderView.setQRDecodingEnabled(true);

        // Use this function to change the autofocus interval (default is 5 secs)
        qrCodeReaderView.setAutofocusInterval(2000L);

        // Use this function to set front camera preview
        qrCodeReaderView.setFrontCamera();

        // Use this function to set back camera preview
        qrCodeReaderView.setBackCamera();

        flashlightCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                qrCodeReaderView.setTorchEnabled(isChecked);
            }
        });
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
//        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        pointsOverlayView.setPoints(points);

        Intent data = new Intent();
        data.putExtra(GOT_RESULT, text);
        setResult(Activity.RESULT_OK,data);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        qrCodeReaderView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        qrCodeReaderView.stopCamera();
    }
}
