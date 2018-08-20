package com.example.sucianalf.grouptracking.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by PERSONAL on 6/12/2018.
 */

public class SessionManager {

    private static String TAG = SessionManager.class.getSimpleName();
    // Shared Preferences
    SharedPreferences pref;

    SharedPreferences.Editor editor;
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;
    private static final String IS_LOGGEDIN = "isLoggedIn";
    private static final String USERNAME= "username";
    private static final String EMAIL= "email";
    private static final String NO_TELP= "no_telp";
    private static final String ALAMAT= "alamat";
    private static final String IMAGE= "image_user";
    private static final String PASSWORD= "password";



    private static final String PREF_NAME = "GROUPTRACK";
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, _context.MODE_MULTI_PROCESS);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn) {

        editor.putBoolean(IS_LOGGEDIN, isLoggedIn);

        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified! >>>>>>>>>");
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGGEDIN, false);
    }

    public void setUsername(String username) {

        editor.putString(USERNAME, username);

        // commit changes
        editor.commit();

        Log.d(TAG, "username session added! >>>>>>>>>");
    }

    public String getUsername(){
        return pref.getString(USERNAME,"");
    }

    public void setAlamat(String username) {

        editor.putString(ALAMAT, username);

        // commit changes
        editor.commit();

        Log.d(TAG, "username session added! >>>>>>>>>");
    }

    public String getAlamat(){
        return pref.getString(ALAMAT,"");
    }

    public void setEmail(String username) {

        editor.putString(EMAIL, username);

        // commit changes
        editor.commit();

        Log.d(TAG, "username session added! >>>>>>>>>");
    }

    public String getEmail(){
        return pref.getString(EMAIL,"");
    }

    public void setNoTelp(String username) {

        editor.putString(NO_TELP, username);

        // commit changes
        editor.commit();

        Log.d(TAG, "username session added! >>>>>>>>>");
    }

    public String getNoTelp(){
        return pref.getString(NO_TELP,"");
    }

    public void setImage(String username) {

        editor.putString(IMAGE, username);

        // commit changes
        editor.commit();

        Log.d(TAG, "username session added! >>>>>>>>>");
    }

    public String getImage(){
        return pref.getString(IMAGE,"");
    }

    public void setPassword(String username) {

        editor.putString(PASSWORD, username);

        // commit changes
        editor.commit();

        Log.d(TAG, "username session added! >>>>>>>>>");
    }

    public String getPassword(){
        return pref.getString(PASSWORD,"");
    }
}
