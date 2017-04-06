package com.kecipir.kecipir.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.kecipir.kecipir.DaftarMemberActivity;
import com.kecipir.kecipir.SessionManager;
import com.kecipir.kecipir.data.AppConfig;
import com.kecipir.kecipir.data.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegistrationIntentService extends IntentService {
    private static final String TAG="RegistrationIntent";

    SessionManager sessionManager;
    HashMap<String, String> user;

    public RegistrationIntentService() {
        super(TAG);
    }
 
    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String id_user, email;
        sessionManager = new SessionManager(this);
        user = sessionManager.getUser();
        id_user = user.get("uid");
        email = user.get("email");

        try {
            synchronized (TAG) {
                InstanceID instanceID = InstanceID.getInstance(this);
                String token = instanceID.getToken("677361563777", GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                Log.i(TAG, "GCM Registration Token: " + token);
//                Toast.makeText(getApplicationContext(),"GCM Registration Token: " + token, Toast.LENGTH_SHORT ).show();

                sendRegistrationToServer(id_user, email, token );

                sharedPreferences.edit().putBoolean("SENT_TOKEN_TO_SERVER", true).apply();
            }
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            sharedPreferences.edit().putBoolean("SENT_TOKEN_TO_SERVER", false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("REGISTRATION_COMPLETE"));
    }

    private void sendRegistrationToServer(final String id_user, final String email, final String token) {

        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "Daftar GCM Response: " + response.toString());
                try {

                    JSONObject jsonObject = new JSONObject(response);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Daftar GCM Response " + error.getMessage());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                params.put("tag", "daftargcm");
                params.put("id_user", id_user);
                params.put("email", email);
                params.put("token", token);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s","green","web-indonesia");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }

        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}