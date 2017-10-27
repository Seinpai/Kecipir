package com.kecipir.kecipir;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.kecipir.kecipir.adapter.HistoryDetailAdapter;
import com.kecipir.kecipir.adapter.HistoryListAdapter;
import com.kecipir.kecipir.data.AppConfig;
import com.kecipir.kecipir.data.AppController;
import com.kecipir.kecipir.data.ClickListener;
import com.kecipir.kecipir.data.HistoryDetail;
import com.kecipir.kecipir.data.HistoryList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity implements ClickListener{

    Toolbar toolbar;


    String id_user, email;
    HistoryList list;
    List<HistoryList> data;

    RecyclerView recyclerView;
    RelativeLayout frameLoading;
    HistoryListAdapter adapter;

    SessionManager sessionManager;

    HistoryDetail detail;
    List<HistoryDetail> dataDialog;
    RecyclerView dialogRecyclerView;
    HistoryDetailAdapter dialogAdapter;
    RelativeLayout dialogFrameLoading;


    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_history);
        frameLoading = (RelativeLayout) findViewById(R.id.frame_loading);
        data = new ArrayList<>();

        AppController appController = (AppController) getApplication();
        mTracker = appController.getDefaultTracker();
        sendScreenName();

        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUser();
        email = user.get("email");
        id_user = user.get("uid");
        parseHistory(email, id_user);
        adapter = new HistoryListAdapter(this, data);

        adapter.setOnClickListener(this);
    }


    private void parseHistory(final String email, final String id) {

        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "History List Response: " + response.toString());
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    int length = jsonArray.length();
                    for (int i =0;i<length;i++){
                        JSONObject jObj = (JSONObject) jsonArray.get(i);
                        boolean error = jObj.getBoolean("error");
//                    // Check for error node in json
                        list = new HistoryList();
                        if (!error) {
                            String nota = jObj.getString("no_nota");
                            String qty = jObj.getString("qty");
                            String total_pembelianrp = jObj.getString("total_pembelianrp");
                            String tanggal = jObj.getString("tanggal");
                            String tgl_panen = jObj.getString("tgl_panen");
                            String nama_member = jObj.getString("nama_member");
                            String sts_bayar = jObj.getString("sts_bayar");

                            list = new HistoryList(nota, qty, total_pembelianrp, tanggal, tgl_panen, nama_member, sts_bayar);
                            data.add(list);
                        } else {
                            // Error in login. Get the error message
                            String errorMsg = jObj.getString("error_msg");
                            Toast.makeText(HistoryActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                        }
                    }
                    frameLoading.setVisibility(View.INVISIBLE);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(HistoryActivity.this));

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
                Toast.makeText(HistoryActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams () {
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "history");
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

        Intent intent = new Intent(HistoryActivity.this, HistoryDetailActivity.class);
        intent.putExtra("nota", data.get(position).getNota());
        startActivity(intent);

//        Dialog dialog = new Dialog(this, R.style.MyDialogTheme);
//        dialog.setContentView(R.layout.dialog_historydetail);
//        dialog.setTitle("Rincian Belanja");
//
//        TextView idNota = (TextView) dialog.findViewById(R.id.history_detailnota);
//        TextView subHarga = (TextView) dialog.findViewById(R.id.history_detailsubharga);
//        dialogFrameLoading = (RelativeLayout) dialog.findViewById(R.id.frame_loading);
//        idNota.setText("No Nota : "+data.get(position).getNota());
//        subHarga.setText("Total Pembelanjaan : Rp. "+data.get(position).getTotalPembelian());
//        mTracker.send(new HitBuilders.EventBuilder()
//                .setCategory("Action")
//                .setAction("Detail History")
//                .build());
//
//
//        dialogRecyclerView = (RecyclerView) dialog.findViewById(R.id.recycler_history_detail);
//        dataDialog = new ArrayList<>();
//        parseHistoryDetail(data.get(position).getNota());
//        dialogAdapter = new HistoryDetailAdapter(HistoryActivity.this, dataDialog);
//
//        dialog.show();

    }

    private void parseHistoryDetail(final String nota) {

        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "Login Response: " + response.toString());
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    int length = jsonArray.length();
                    for (int i =0;i<length;i++){
                        JSONObject jObj = (JSONObject) jsonArray.get(i);
                        boolean error = jObj.getBoolean("error");
//                    // Check for error node in json
                        detail = new HistoryDetail();
                        if (!error) {
                            String namaBarang = jObj.getString("nama_barang");
                            String foto = AppConfig.IMAGE_LINK+""+jObj.getString("foto");
                            String grade = jObj.getString("grade");
                            String satuan = jObj.getString("satuan");
                            String quantity = jObj.getString("qty");
                            String namaPetani = jObj.getString("nama_petani");
                            String total_pembelianrp = jObj.getString("total_pembelianrp");
                            String harga = jObj.getString("harga_jualrp");
                            String total = jObj.getString("subtotalrp");

                            detail = new HistoryDetail(foto, namaBarang, namaPetani, grade, satuan, quantity, harga, total);
                            dataDialog.add(detail);
                        } else {
                            // Error in login. Get the error message
                            String errorMsg = jObj.getString("error_msg");
                            Toast.makeText(HistoryActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                        }
                    }

                    dialogFrameLoading.setVisibility(View.INVISIBLE);
                    dialogRecyclerView.setAdapter(dialogAdapter);
                    dialogRecyclerView.setLayoutManager(new LinearLayoutManager(HistoryActivity.this));

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
                Toast.makeText(HistoryActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams () {
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "historydetail");
                params.put("nota", nota);

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
        AppController.getInstance(). addToRequestQueue(strReq, tag_string_req);

    }

    private void sendScreenName() {
        String name = "History ";

        // [START screen_view_hit]
        Log.i("History", "Screen name: " + name);
        mTracker.setScreenName("Screen" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // [END screen_view_hit]
    }
}
