package com.kecipir.kecipir.gcm;

import android.content.Intent;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.iid.InstanceIDListenerService;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.kecipir.kecipir.SessionManager;
import com.kecipir.kecipir.data.AppConfig;
import com.kecipir.kecipir.data.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

//public class MyInstanceIDListenerService extends InstanceIDListenerService {
public class MyInstanceIDListenerService extends FirebaseInstanceIdService {
    /**
     * Called if InstanceID token is updated.
     * May occur if the security of the previous token had been compromised
     * and is initiated by the InstanceID provider.
     */
    SessionManager sessionManager;
    HashMap<String, String> user;


    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("FireBase", "Refreshed token: " + refreshedToken);
        String id_user, email;
        sessionManager = new SessionManager(this);
        user = sessionManager.getUser();
        id_user = user.get("uid");
        email = user.get("email");
        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(id_user, email, refreshedToken);
    }
//    @Override
//    public void onTokenRefresh() {
//        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
//        startService(new Intent(this, RegistrationIntentService.class));
//    }

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