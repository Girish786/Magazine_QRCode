package com.uniquevoices.uvapps;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {
    private Context context;
    private static PrefManager prefManager = null;
    private SharedPreferences pref;

    public static final String LOGGEDIN_USER_ID = "LOGGEDIN_USER_ID";
    public static final String LOGGEDIN_USER_NAME = "LOGGEDIN_USER_NAME";

    public static PrefManager getInstance(Context context) {
        if (prefManager == null)
            prefManager = new PrefManager(context);
        return prefManager;
    }

    private PrefManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences("MyPref", 0);
    }

    public void setUserId(String userId) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(LOGGEDIN_USER_ID, userId);
        editor.apply();
    }

    public String getUserId() {
        return pref.getString(LOGGEDIN_USER_ID, "");
    }

    public void setUserName(String name) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(LOGGEDIN_USER_NAME, name);
        editor.apply();
    }

    public String getUserName() {
        return pref.getString(LOGGEDIN_USER_NAME, "");
    }
}
