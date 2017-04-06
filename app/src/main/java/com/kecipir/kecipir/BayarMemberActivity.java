package com.kecipir.kecipir;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.kecipir.kecipir.adapter.CodMemberAdapter;
import com.kecipir.kecipir.adapter.DepositAdapter;
import com.kecipir.kecipir.data.AppConfig;
import com.kecipir.kecipir.data.AppController;
import com.kecipir.kecipir.data.ClickListener;
import com.kecipir.kecipir.data.CodMemberList;
import com.kecipir.kecipir.data.DepositList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BayarMemberActivity extends AppCompatActivity implements ClickListener{

    RelativeLayout frameLoading,frameBuffer, layoutSpanner, layoutDev;
    RecyclerView recyclerView;
    TextView txtMessage, txtTotal;

    CodMemberAdapter adapter;
    SessionManager sessionManager;
    ImageView imgSpanner;

    String email, id_user;
    String total;


    CodMemberList list;
    List<CodMemberList> data;

    boolean spanned = false;
    private Tracker mTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bayar_member);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        frameLoading = (RelativeLayout) findViewById(R.id.frame_loading);
        frameBuffer = (RelativeLayout) findViewById(R.id.frame_buffer);
        txtMessage = (TextView) findViewById(R.id.txt_message);
        txtTotal = (TextView) findViewById(R.id.txt_total_depo);
        layoutDev = (RelativeLayout) findViewById(R.id.layout_dev);

        AppController appController = (AppController) getApplication();
        mTracker = appController.getDefaultTracker();
        sendScreenName();

        data = new ArrayList<>();
        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUser();
        email = user.get("email");
        id_user = user.get("uid");

        parseDeposit(email, id_user);
        adapter = new CodMemberAdapter(this, data);

        adapter.setOnClickListener(this);
    }


    private void parseDeposit(final String email, final String id) {

        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_CODMEMBER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "greencash List Response: " + response.toString());
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean("error")) {
                        String totalrp = jsonObject.getString("total_deporp");
                        total = jsonObject.getString("total_depo");
                        if (total.equalsIgnoreCase("0")) {
//                        btnTarik.setVisibility(View.INVISIBLE);
                        }
                        String listDeposit = jsonObject.getString("listcod");

                        txtTotal.setText("Rp. " + totalrp);
                        JSONArray jsonArray = new JSONArray(listDeposit);
                        int length = jsonArray.length();
                        if (length == 0) {
                            frameLoading.setVisibility(View.INVISIBLE);
                            txtMessage.setVisibility(View.VISIBLE);
                            data = new ArrayList<>();
                            adapter = new CodMemberAdapter(BayarMemberActivity.this, data);
                            adapter.notifyDataSetChanged();
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(BayarMemberActivity.this));
                        } else {
                            txtMessage.setVisibility(View.INVISIBLE);
                            data = new ArrayList<>();
                            adapter = new CodMemberAdapter(BayarMemberActivity.this, data);
                            adapter.notifyDataSetChanged();
                            for (int i = 0; i < length; i++) {
                                JSONObject jObj = (JSONObject) jsonArray.get(i);
                                list = new CodMemberList();

                                String no_nota = jObj.getString("no_nota");
                                String tanggal = jObj.getString("tanggal");
                                String nama = jObj.getString("nama_member");
                                String ket = jObj.getString("ket");
                                String total = jObj.getString("total");
                                String id_host = jObj.getString("id_host");

                                list = new CodMemberList(no_nota, nama, tanggal, id_host, total, ket);
                                data.add(list);
                                // Error in login. Get the error message
//                                String errorMsg = jObj.getString("error_msg");
//                                Toast.makeText(ListDepositActivity.this, errorMsg, Toast.LENGTH_LONG).show();
//
                            }
                            frameLoading.setVisibility(View.INVISIBLE);
                            adapter.setOnClickListener(BayarMemberActivity.this);
//                        adapter = new DepositAdapter(ListDepositActivity.this, data);
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(BayarMemberActivity.this));


                        }
                    }
                    else{
                        frameLoading.setVisibility(View.INVISIBLE);
                        layoutDev.setVisibility(View.VISIBLE);
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
                Toast.makeText(BayarMemberActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams () {
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "viewcodmember");
                params.put("email", email);
                params.put("id", id);

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
        final int pos = position;
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi")
                .setMessage("Lunasi Pembayaran No. Nota "+data.get(position).getNo_nota()+" ?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        bayarCOD(data.get(pos).getNo_nota(), data.get(pos).getId_host(), data.get(pos).getTotal());
                        parseDeposit(email, id_user);
                        Log.d("BAYAR COD", "COD");
                    }
                })
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }


    private void bayarCOD(final String no_nota, final String id_host, final String total) {

        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_CODMEMBER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "View chckout Response: " + response.toString());
                try {
                    if (response.equalsIgnoreCase("")) {
//                        frameBuffer.setVisibility(View.INVISIBLE);
                    } else {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean error = jsonObject.getBoolean("error");
                        if (!error) {
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(BayarMemberActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(BayarMemberActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
//                frameBuffer.setVisibility(View.INVISIBLE);
//                retry();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                params.put("tag", "paycodmember");
                params.put("no_nota", no_nota);
                params.put("id_host", id_host);
                params.put("total", total);

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

    private void sendScreenName() {
        String name = "COD member";

        // [START screen_view_hit]
        Log.i("List COD member", "Screen name: " + name);
        mTracker.setScreenName("Screen" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // [END screen_view_hit]
    }
}
