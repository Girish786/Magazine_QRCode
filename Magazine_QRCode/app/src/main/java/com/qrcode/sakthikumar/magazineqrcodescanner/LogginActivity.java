package com.qrcode.sakthikumar.magazineqrcodescanner;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
//import com.appunite.appunitevideoplayer.PlayerActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LogginActivity extends AppCompatActivity {

    VideoView vvVideo;
    EditText emailEditText, passwordEditText;
    TextView errorMessage;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        playBackgroudVideo();
        dialog = new ProgressDialog(this);

        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        errorMessage = (TextView) findViewById(R.id.errorMessage);

        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.login_constraint_layout);
        layout.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent ev)
            {
                hideKeyboard(view);
                return false;
            }
        });


        emailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if(hasFocus)  {
                   hideErrorMessage();
                }
            }
        });

        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if(hasFocus)  {
                    hideErrorMessage();
                }
            }
        });

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                hideErrorMessage();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                hideErrorMessage();
            }

            @Override
            public void afterTextChanged(Editable s) {
                hideErrorMessage();
            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                hideErrorMessage();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                hideErrorMessage();
            }

            @Override
            public void afterTextChanged(Editable s) {
                hideErrorMessage();
            }
        });
    }

    protected void hideKeyboard(View view) {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    protected void onResume() {
        super.onResume();
        emailEditText.setText("");
        passwordEditText.setText("");

        playBackgroudVideo();
    }

    @Override
    public void onBackPressed() {
    }

    // Initialize Methods
    public void playBackgroudVideo() {
        vvVideo = (VideoView) findViewById(R.id.vvVideo);
        vvVideo.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.first_screen_bg_video));

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
    public void actionOnLogin(View v) {
//        Intent intent = new Intent(LogginActivity.this, YoutubePlayerActivity.class);
//        intent.putExtra("urlKey", "fTKQvxYwvpc");
//        startActivity(intent);

//        Intent intent = new Intent(LogginActivity.this, VideoViewPlayerActivity.class);
//        intent.putExtra("urlString", "https://www.youniquevoices.com/assets/frontend/video/finalvideohome.mp4");
//        startActivity(intent);

//        startActivity(PlayerActivity.getVideoPlayerIntent(LogginActivity.this,"https://www.youniquevoices.com/assets/frontend/video/finalvideohome.mp4", ""));
//
//        Intent intent = new Intent(LogginActivity.this, VimeoPlayerActivity.class);
//        intent.putExtra("urlKey", "https://www.youniquevoices.com/assets/frontend/video/finalvideohome.mp4");
//        startActivity(intent);

//        Intent intent = new Intent(LogginActivity.this, VimeoPlayerActivity.class);
//        intent.putExtra("urlKey", "7993383");
//        startActivity(intent);


        if (emailEditText.getText().toString().matches("")) {
            showErrorMessage("enter your email address");
        } else if (!isValidEmail(emailEditText.getText())) {
            showErrorMessage("enter valid email address");
        } else if (passwordEditText.getText().toString().matches("")) {
            showErrorMessage("enter your password");
        } else {

            String url = "http://uvapps.youniquevoices.com/index.php/uvappsapicontrol/signin";
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            showProgressDialog();

            StringRequest request = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("Response", response.toString());
                            hideProgressDialog();
                            processResponse(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Response", error.toString());
                            hideProgressDialog();
                            showErrorMessage("log in - info entered incorrectly");
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("username", emailEditText.getText().toString());
                    params.put("password", passwordEditText.getText().toString());
                    return params;
                }
            };

            requestQueue.add(request);
        }
    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//
//        // Checks the orientation of the screen
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
//        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
//            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
//        }
//    }

    public void showErrorMessage(String message) {
        errorMessage.setText(message);
        errorMessage.setVisibility(View.VISIBLE);
    }

    public void hideErrorMessage() {
        errorMessage.setVisibility(View.GONE);
    }

    public void processResponse(String response) {
        try {
            JSONObject obj = new JSONObject(response);
            JSONArray array = obj.getJSONArray("userdata");

            if (array.length() != 0) {
                SharedPreferences preferences = this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("EmailId", array.getJSONObject(0).getString("user_email"));
                editor.commit();

                Intent goToNextActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(goToNextActivity);
            } else {
                showErrorMessage("log in - info entered incorrectly");
            }
        } catch (JSONException e) {
            Log.e("MYAPP", "Email Address or Password is wrong.", e);
            showErrorMessage("log in - info entered incorrectly");
        }
    }

    public void actionOnForgotPassword(View v) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youniquevoices.com/index.php/home/forget_password"));
        startActivity(browserIntent);
    }

    public void showProgressDialog() {
        dialog.setMessage("loading...");
        dialog.show();
    }

    public void hideProgressDialog() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void actionOnCreateAccount(View v) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youniquevoices.com/index.php/home/register"));
        startActivity(browserIntent);
    }


    // Other Methods
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

}