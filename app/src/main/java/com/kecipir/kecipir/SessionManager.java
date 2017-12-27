package com.kecipir.kecipir;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

import java.util.HashMap;

/**
 * Created by Albani on 10/19/2015.
 */
public class SessionManager {
    private static String TAG = SessionManager.class.getSimpleName();

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    Context context;

    int PRIVATE_MODE =0;

    private static final String PREF_NAME = "Preferences";
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String KEY_TABEL = "tabel";
    private static final String LOGINAS = "loginAs";
    private static final String KEY_TGLPANEN = "tglpanen";

    private static final String KEY_PASSWORD = "password";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_UID = "uid";
    private static final String KEY_HOST = "id_host";

    private static final String TEMP_IS_LOGIN = "temp_IsLoggedIn";
    private static final String TEMP_LOGINAS = "temp_loginAs";
    private static final String TEMP_KEY_TGLPANEN = "temp_tglpanen";

    private static final String TEMP_KEY_UID = "temp_uid";
    private static final String TEMP_KEY_HOST = "temp_id_host";

    public SessionManager(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void setLogin (boolean isLoggedIn, String id, String tabel,String username, String email, String password, String id_host, String loginAs, String tgl_panen){
        editor.putBoolean(IS_LOGIN, isLoggedIn);
        editor.putString(KEY_UID, id);
        editor.putString(KEY_TABEL, tabel);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_HOST, id_host);
        editor.putString(LOGINAS, loginAs);
        editor.putString(KEY_TGLPANEN, tgl_panen);
        editor.commit();
    }

    public void setTempLogin(boolean isLoggedIn, String id, String id_host, String tgl_panen){
        editor.putBoolean(TEMP_IS_LOGIN, isLoggedIn);
        editor.putString(TEMP_KEY_UID, id);
        editor.putString(TEMP_KEY_HOST, id_host);
        editor.putString(TEMP_KEY_TGLPANEN, tgl_panen);
        editor.putString(TEMP_LOGINAS, "member");
        editor.commit();
    }

    public HashMap<String, String> getUser(){
        HashMap<String,String> user = new HashMap<String , String >();
        user.put(KEY_TABEL, sharedPreferences.getString(KEY_TABEL, null));
        user.put(KEY_PASSWORD, sharedPreferences.getString(KEY_PASSWORD, null));
        user.put(KEY_USERNAME, sharedPreferences.getString(KEY_USERNAME, null));
        user.put(KEY_EMAIL, sharedPreferences.getString(KEY_EMAIL, null));
        user.put(KEY_UID, sharedPreferences.getString(KEY_UID, null));
        user.put(KEY_HOST, sharedPreferences.getString(KEY_HOST, null));
        user.put(LOGINAS, sharedPreferences.getString(LOGINAS, null));
        user.put(KEY_TGLPANEN, sharedPreferences.getString(KEY_TGLPANEN, null));

        user.put(TEMP_KEY_UID, sharedPreferences.getString(TEMP_KEY_UID, null));
        user.put(TEMP_KEY_HOST, sharedPreferences.getString(TEMP_KEY_HOST, null));
        user.put(TEMP_LOGINAS, sharedPreferences.getString(TEMP_LOGINAS, null));
        user.put(TEMP_KEY_TGLPANEN, sharedPreferences.getString(TEMP_KEY_TGLPANEN, null));

        return user;
    }

    public void logoutUser(){

        FacebookSdk.sdkInitialize(context);
        LoginManager.getInstance().logOut();
//        if (mGoogleApiClient.isConnected()){
//        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
//                new ResultCallback<Status>() {
//                    @Override
//                    public void onResult(Status status) {
//                        Toast.makeText(context, "Logged Out", Toast.LENGTH_SHORT).show();
//                    }
//                });
//        }
        editor.clear();
        editor.commit();

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);
        ((Activity)context).finish();

    }

    public boolean isLoggedIn(){
        return sharedPreferences.getBoolean(IS_LOGIN, false);
    }
}
