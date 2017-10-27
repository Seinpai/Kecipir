package com.kecipir.kecipir;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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

public class AddmemberActivity extends AppCompatActivity {

    Toolbar toolbar;
    SessionManager sessionManager;
    HashMap<String, String> user;

    String email, nama, id_user;
    EditText edtNama, edtEmail;
    Button btnTambahMember;


    RelativeLayout frameBuffer;
    private Tracker mTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addmember);
        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edtNama = (EditText) findViewById(R.id.edt_add_namamember);
        edtEmail = (EditText) findViewById(R.id.edt_add_emailmember);

        btnTambahMember = (Button) findViewById(R.id.btn_addmember);
        frameBuffer = (RelativeLayout) findViewById(R.id.frame_buffer);

        AppController appController = (AppController) getApplication();
        mTracker = appController.getDefaultTracker();
        sendScreenName();

        sessionManager = new SessionManager(this);
        user = sessionManager.getUser();

        id_user = user.get("uid");

        btnTambahMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = edtEmail.getText().toString();
                nama = edtNama.getText().toString();

                if (email.isEmpty() || email.equalsIgnoreCase("")||nama.isEmpty() || nama.equalsIgnoreCase("")){
                    Toast.makeText(AddmemberActivity.this, "Oops! nama dan email masih kosong", Toast.LENGTH_SHORT).show();
                }
                else{
                    frameBuffer.setVisibility(View.VISIBLE);
                    addMemberHost(nama, email, id_user);
                }
            }
        });

    }

    private void addMemberHost(final String nama, final String email, final String id_host) {

        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "Daftar Response: " + response.toString());
                try {
                    if (response.equalsIgnoreCase("")) {
                        frameBuffer.setVisibility(View.INVISIBLE);
                    }
                    else {

                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("error")){
                            frameBuffer.setVisibility(View.INVISIBLE);
                            Toast.makeText(AddmemberActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                        else {
                            mTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("Action")
                                    .setAction("Tambah member")
                                    .build());
                            frameBuffer.setVisibility(View.INVISIBLE);
                            addFinish(jsonObject.getString("message"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    frameBuffer.setVisibility(View.INVISIBLE);
                    retryAddMember();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "View Checkout Error: " + error.getMessage());
                Toast.makeText(AddmemberActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
                retryAddMember();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                params.put("tag", "addmemberhost");
                params.put("id_host", id_host);
                params.put("email_member", email);
                params.put("nama_member", nama);

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

        strReq.setRetryPolicy(new DefaultRetryPolicy(AppConfig.TIMEOUT_NETWORK, AppConfig.RETRY_NETWORK, AppConfig.MULTI_NETWORK));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void retryAddMember() {


        new AlertDialog.Builder(this)
                .setTitle("Koneksi Bermasalah ")
                .setMessage("Coba lagi?")
                .setPositiveButton("Coba lagi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        frameBuffer.setVisibility(View.VISIBLE);
                        addMemberHost(nama, email, id_user);
                    }
                })
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .show();
    }


    private void addFinish(String message) {

        frameBuffer.setVisibility(View.INVISIBLE);
        new AlertDialog.Builder(this)
                .setTitle("Tambah Member Berhasil")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        edtNama.setText("");
                        edtEmail.setText("");
                    }
                })
                .show();
    }


    private void sendScreenName() {
        String name = "Tambah Member";

        // [START screen_view_hit]
        Log.i("AddMemberActivity", "Screen name: " + name);
        mTracker.setScreenName("Screen" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // [END screen_view_hit]
    }
}

