package com.qrcode.sakthikumar.magazineqrcodescanner;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ChatMessage {
    @SerializedName("msg_id")
    String id;
    @SerializedName("messages")
    String message;
    @SerializedName("msg_from_id")
    String msg_from_id;
    @SerializedName("msg_to_id")
    String msg_to_id;
    @SerializedName("dateTime")
    String dateTime;
    String status = "pending";
}

class ChatResponse implements Serializable {
    @SerializedName("userdata")
    String userdata = "";
    @SerializedName("status")
    String status = "";
    @SerializedName("message")
    String message = "";
    @SerializedName("freindslist")
    ArrayList<User> friendsList = new ArrayList<>();
    @SerializedName("chat_details_between_two_specific_users")
    ArrayList<ChatMessage> chatHistoryList = new ArrayList<>();
}

class ChatMessageResponse implements Serializable {
    @SerializedName("chat_creation_status")
    String chat_creation_status = "";
    @SerializedName("message")
    String message = "";
}