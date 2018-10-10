package com.qrcode.sakthikumar.magazineqrcodescanner;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import com.google.gson.annotations.SerializedName;

public class User implements Serializable {

    @SerializedName("user_main_id")
    String id = "";
    @SerializedName("name")
    String name = "";
    @SerializedName("email")
    String email = "";
    @SerializedName("phone")
    String phone = "";
    @SerializedName("dob")
    String dob = "";
    @SerializedName("street_address")
    String street_address = "";
    @SerializedName("country")
    String country = "";
    @SerializedName("state")
    String state = "";
    @SerializedName("city_or_town")
    String city_or_town = "";
    @SerializedName("profile_pic")
    String profile_pic = "";
}


class UserResponse implements Serializable {

    @SerializedName("userdata")
    String userdata = "";
    @SerializedName("status")
    String status = "";
    @SerializedName("message")
    String message = "";
    @SerializedName("freindslist")
    ArrayList<User> friendsList = new ArrayList<>();
    @SerializedName("mychattab_freindslist")
    ArrayList<User> conversationList = new ArrayList<>();
}

