package com.kecipir.kecipir;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kecipir.kecipir.data.AppConfig;
import com.kecipir.kecipir.data.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddAkunBankActivity extends AppCompatActivity {

    RelativeLayout frameLoading, frameBuffer;
    Button btnAddBank;
    EditText edtNamaRek, edtNoRek, edtBank;
    CoordinatorLayout coordinatorLayout;

    String id_user, email;

    SessionManager sessionManager;
    HashMap<String, String> user;


    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_akun_bank);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        frameBuffer = (RelativeLayout) findViewById(R.id.frame_buffer);
        btnAddBank = (Button) findViewById(R.id.btn_tbhakunbank);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        edtNamaRek = (EditText) findViewById(R.id.edt_nama_pemilik_rekening);
        edtBank = (EditText) findViewById(R.id.edt_nama_bank);
        edtNoRek = (EditText) findViewById(R.id.edt_nomor_rekening);

        AppController appController = (AppController) getApplication();
        mTracker = appController.getDefaultTracker();
        sendScreenName();

        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUser();

        id_user = user.get("uid");
        email = user.get("email");
        btnAddBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO adddeposit
                frameBuffer.setVisibility(View.VISIBLE);
                String nama_rekening = edtNamaRek.getText().toString();
                String nomor_rekening = edtNoRek.getText().toString();
                String nama_bank = edtBank.getText().toString();
                addDeposit(id_user, email,nama_rekening,nomor_rekening, nama_bank);
            }
        });
    }

    private void addDeposit(final String id_user, final String email, final String nama_rekening,
                            final String nomor_rekening, final String nama_bank) {

        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_BANK, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "View tambah bank Response: " + response.toString());
                try {
                    if (response.equalsIgnoreCase("")) {
                        frameBuffer.setVisibility(View.INVISIBLE);
                    } else {
                        frameBuffer.setVisibility(View.INVISIBLE);
                        JSONObject jsonObject = new JSONObject(response);

                        boolean error = jsonObject.getBoolean("error");
                        String message = jsonObject.getString("message");

                        if (!error) {
                            mTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("Action")
                                    .setAction("Tambah Bank")
                                    .build());
                            confirmFinish(message);
                        } else {
                            Snackbar snackbar = Snackbar
                                    .make(coordinatorLayout, message, Snackbar.LENGTH_LONG)
                                    .setAction("OK", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                        }
                                    });
                            View snackbarView = snackbar.getView();
                            TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                            textView.setTextColor(Color.WHITE);
                            snackbar.show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "View Tambah Bank Error: " + error.getMessage());
                Toast.makeText(AddAkunBankActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
                frameBuffer.setVisibility(View.INVISIBLE);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "tambahakunbank");
                params.put("id_user", id_user);
                params.put("email", email);
                params.put("nama_pemilik", nama_rekening);
                params.put("nama_bank", nomor_rekening);
                params.put("no_rek", nama_bank);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s", "green", "web-indonesia");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }

        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(AppConfig.TIMEOUT_NETWORK, AppConfig.RETRY_NETWORK, AppConfig.MULTI_NETWORK));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void confirmFinish(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Tambah Berhasil")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .show();
    }

    private void sendScreenName() {
        String name = "Tambah Akun Bank";

        // [START screen_view_hit]
        Log.i("Tambah AkunBank", "Screen name: " + name);
        mTracker.setScreenName("Screen " + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // [END screen_view_hit]
    }
}
