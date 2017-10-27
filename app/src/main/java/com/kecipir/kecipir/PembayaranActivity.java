package com.kecipir.kecipir;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
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
import com.kecipir.kecipir.adapter.HistoryListAdapter;
import com.kecipir.kecipir.adapter.PembayaranAdapter;
import com.kecipir.kecipir.data.AppConfig;
import com.kecipir.kecipir.data.AppController;
import com.kecipir.kecipir.data.ClickListener;
import com.kecipir.kecipir.data.HistoryList;
import com.kecipir.kecipir.data.PembayaranList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PembayaranActivity extends AppCompatActivity implements ClickListener{


    PembayaranList list;
    List<PembayaranList> data;

    RecyclerView recyclerView;
    RelativeLayout frameLoading,frameBuffer;
    PembayaranAdapter adapter;
    TextView txtMessage;

    SessionManager sessionManager;
    Toolbar toolbar;

    String email, id_user;

    String no_nota, nama, total, tujuan;
    private Tracker mTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembayaran);
        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        recyclerView = (RecyclerView) findViewById(R.id.recycler_pembayaran);
        frameLoading = (RelativeLayout) findViewById(R.id.frame_loading);
        frameBuffer = (RelativeLayout) findViewById(R.id.frame_buffer);
        txtMessage = (TextView) findViewById(R.id.txt_message);

        data = new ArrayList<>();

        AppController appController = (AppController) getApplication();
        mTracker = appController.getDefaultTracker();
        sendScreenName();


        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUser();
        email = user.get("email");
        id_user = user.get("uid");
        parsePembayaran(email, id_user);
        adapter = new PembayaranAdapter(this, data);

        adapter.setOnClickListener(this);
    }

    private void parsePembayaran(final String email, final String id) {

        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_PAYMENT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "pembayaran List Response: " + response.toString());
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    int length = jsonArray.length();
                    if (length == 0){
                        frameLoading.setVisibility(View.INVISIBLE);
                        txtMessage.setVisibility(View.VISIBLE);
                    }
                    else{
                        txtMessage.setVisibility(View.INVISIBLE);
                        for (int i =0;i<length;i++){
                            JSONObject jObj = (JSONObject) jsonArray.get(i);
                            boolean error = jObj.getBoolean("error");
//                          // Check for error node in json
                            list = new PembayaranList();
                            if (!error) {
                                String nota = jObj.getString("no_nota");
                                String total = jObj.getString("total");
                                String nama = jObj.getString("nama");
                                String tgl_transaksi = jObj.getString("tgl_transaksi");
                                String tgl_panen = jObj.getString("tgl_panen");
                                String payment = jObj.getString("payment");
                                String payment_type = jObj.getString("payment_type");

                                list = new PembayaranList(nota, total, nama, tgl_transaksi, tgl_panen, payment, payment_type);
                                data.add(list);
                            } else {
                                // Error in login. Get the error message
                                String errorMsg = jObj.getString("error_msg");
                                Toast.makeText(PembayaranActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                            }
                        }
                        frameLoading.setVisibility(View.INVISIBLE);
//                    adapter = new PembayaranAdapter(PembayaranActivity.this, data);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(PembayaranActivity.this));
                    }

                }
                catch(JSONException e)
                {
                    e.printStackTrace();
                }

            }
        },new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse (VolleyError error){
                Log.e("TAG", "Login Error: " + error.getMessage());
                Toast.makeText(PembayaranActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams () {
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "confirmpaymentlist");
                params.put("email", email);
                params.put("id_user", id);

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

    @Override
    public void itemClicked(View view, int position) {
        no_nota = data.get(position).getNoNota();
        nama = data.get(position).getNama();
        total = data.get(position).getTotal();
        tujuan = data.get(position).getPayment();

//        confirmPembayaran("Konfirmasi pembayaran untuk belanja no nota "+no_nota+"?");

        if (data.get(position).getPaymentType().equalsIgnoreCase("8")){

            Intent i = new Intent(PembayaranActivity.this, DetailPembayaranActivity.class);
            i.putExtra("no_nota", no_nota);
            i.putExtra("nama", nama);
            i.putExtra("total", total);
            i.putExtra("tujuan", tujuan);
            startActivity(i);

        }
        else{
            Intent i = new Intent(PembayaranActivity.this, KonfirmPembayaranActivity.class);
            i.putExtra("no_nota", no_nota);
            i.putExtra("nama", nama);
            i.putExtra("total", total);
            i.putExtra("tujuan", tujuan);
            startActivity(i);

        }
    }

    private void konfirmasi(final String no_nota, final String nama, final String tujuan, final String total) {

        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "View konfirmasi Response: " + response.toString());
                try {
                    if (response.equalsIgnoreCase("")) {
                        frameBuffer.setVisibility(View.INVISIBLE);
                    }
                    else {

                        JSONObject jsonObject = new JSONObject(response);
                        boolean error = jsonObject.getBoolean("error");

                        if (!error){
                            frameBuffer.setVisibility(View.INVISIBLE);
                            confirmFinish(jsonObject.getString("message"));
                        }
                        else {
                            frameBuffer.setVisibility(View.INVISIBLE);
                            Toast.makeText(PembayaranActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
//                    retry();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "View Checkout Error: " + error.getMessage());
                Toast.makeText(PembayaranActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
//                frameBuffer.setVisibility(View.INVISIBLE);
//                retry();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                params.put("tag", "confirm_payment");
                params.put("no_nota", no_nota);
                params.put("tujuan", tujuan);
                params.put("nama", nama);
                params.put("total", total);

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

    private void confirmPembayaran(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        frameBuffer.setVisibility(View.VISIBLE);
                        konfirmasi(no_nota, nama, tujuan, total);
                    }
                })
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private void confirmFinish(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Berhasil")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .show();
    }
    private void sendScreenName() {
        String name = "Pembayaran";

        // [START screen_view_hit]
        Log.i("Pembayaran", "Screen name: " + name);
        mTracker.setScreenName("Screen" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // [END screen_view_hit]
    }
}
