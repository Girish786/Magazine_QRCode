package com.qrcode.sakthikumar.magazineqrcodescanner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

//
// Tutorial Link
// https://github.com/smukov/AvI/wiki/Android-Chat-Screen
//

public class ChatActivity extends AppCompatActivity {

    private EditText messageET;
    private TextView titleTextView;
    private ListView messagesContainer;
    private ImageButton sendBtn;
    private ChatAdapter adapter;
    private ArrayList<ChatMessage> chatHistory;
    private String receiverId;
    private ProgressDialog dialog;

    private Timer mTimer1;
    private TimerTask mTt1;
    private Handler mTimerHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initControls();
        getAllChatMessages();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    private void stopTimer(){
        if(mTimer1 != null){
            mTimer1.cancel();
            mTimer1.purge();
        }
    }

    private void startTimer(){
        mTimer1 = new Timer();
        mTt1 = new TimerTask() {
            public void run() {
                mTimerHandler.post(new Runnable() {
                    public void run(){
                        stopTimer();
                        getAllChatMessages();
                    }
                });
            }
        };

        mTimer1.schedule(mTt1, 5000, 5000);
    }

    private void initControls() {
        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        messageET = (EditText) findViewById(R.id.messageEdit);
        sendBtn = (ImageButton) findViewById(R.id.chatSendButton);
        titleTextView = (TextView) findViewById(R.id.chat_title_textView);
        dialog = new ProgressDialog(this);

        Intent intent = getIntent();
        if (intent != null) {
            titleTextView.setText(intent.getStringExtra("title"));
            receiverId = intent.getStringExtra("receiver_user_id");
        }

        RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
        configureChatMessages();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(messageET.getText().toString())) {
                    return;
                }

                sendChatMessages();
            }
        });
    }

    public void displayMessage(ChatMessage message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        scroll();
    }

    private void scroll() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }

    private void configureChatMessages() {
        chatHistory = new ArrayList<ChatMessage>();
        adapter = new ChatAdapter(ChatActivity.this, new ArrayList<ChatMessage>());
        messagesContainer.setAdapter(adapter);
    }

    public void displayAllMessages() {
        for (int i = 0; i < chatHistory.size(); i++) {
            ChatMessage message = chatHistory.get(i);
            displayMessage(message);
        }
    }

    public void showProgressDialog() {
        dialog.setMessage("loading...");
        dialog.setCancelable(false);
        dialog.show();
    }

    public void hideProgressDialog() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    // SERVICE API CALL
    public void getAllChatMessages() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        if (chatHistory.isEmpty()) showProgressDialog();

        StringRequest request = new StringRequest(Request.Method.POST, Constants.ALL_CHAT_MESSAGES_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideProgressDialog();
                        Log.e("Response", response.toString());
                        Gson gson = new Gson();
                        ChatResponse chatResponse = gson.fromJson(response.toString(), ChatResponse.class);
                        chatHistory.clear();
                        chatHistory.addAll(chatResponse.chatHistoryList);
                        displayAllMessages();
                        startTimer();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideProgressDialog();
                        Log.e("Response", error.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("loginuser", PrefManager.getInstance(getApplicationContext()).getUserId());
                params.put("loginuserfriend", receiverId);
                return params;
            }
        };

        requestQueue.add(request);
    }

    public void sendChatMessages() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        showProgressDialog();

        StringRequest request = new StringRequest(Request.Method.POST, Constants.CREATE_CHAT_ROOM_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideProgressDialog();
                        Log.e("Response", response.toString());
                        Gson gson = new Gson();
                        ChatMessageResponse chatResponse = gson.fromJson(response.toString(), ChatMessageResponse.class);
                        if (chatResponse.chat_creation_status.contains("Chat created successfully. Chat entered into DB properly")) {
                            ChatMessage chatMessage = new ChatMessage();
                            chatMessage.id = Long.toString(System.currentTimeMillis());
                            chatMessage.message = messageET.getText().toString();
                            chatMessage.dateTime = "";
                            chatMessage.msg_from_id = PrefManager.getInstance(getApplicationContext()).getUserId();
                            chatMessage.msg_to_id = receiverId;
                            displayMessage(chatMessage);
                            messageET.setText("");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideProgressDialog();
                        Log.e("Response", error.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_from_id", PrefManager.getInstance(getApplicationContext()).getUserId());
                params.put("user_to_id", receiverId);
                params.put("user_message", messageET.getText().toString());
                return params;
            }
        };

        requestQueue.add(request);
    }
}