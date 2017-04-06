package com.kecipir.kecipir;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.analytics.Tracker;
import com.kecipir.kecipir.adapter.HistoryDetailAdapter;
import com.kecipir.kecipir.adapter.HistoryListAdapter;
import com.kecipir.kecipir.adapter.NotificationAdapter;
import com.kecipir.kecipir.data.AppConfig;
import com.kecipir.kecipir.data.AppController;
import com.kecipir.kecipir.data.ClickListener;
import com.kecipir.kecipir.data.HistoryDetail;
import com.kecipir.kecipir.data.HistoryList;
import com.kecipir.kecipir.data.Notification;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NotificationActivity extends AppCompatActivity implements ClickListener {

    Toolbar toolbar;


    String id_user, email;
    Notification list;
    List<Notification> data;

    RecyclerView recyclerView;
    RelativeLayout frameLoading;
    NotificationAdapter adapter;

    SessionManager sessionManager;



    private Tracker mTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_notification);
        frameLoading = (RelativeLayout) findViewById(R.id.frame_loading);
        data = new ArrayList<>();

        AppController appController = (AppController) getApplication();
        mTracker = appController.getDefaultTracker();

        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUser();
        email = user.get("email");
        id_user = user.get("uid");
        adapter = new NotificationAdapter(this, data);

        getNotification();
        adapter.setOnClickListener(this);
    }

    private void getNotification() {

        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.URL_NEWS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "History List Response: " + response.toString());
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    if (!error){
                        String post = jsonObject.getString("posts");
                        JSONArray jsonArray = new JSONArray(post);
                        int length = jsonArray.length();
                        for (int i =0;i<length;i++){
                            JSONObject jObj = (JSONObject) jsonArray.get(i);
                            list = new Notification();
                            String url = jObj.getString("url");
                            String title = jObj.getString("title");
                            String postDate = jObj.getString("date");
                            list = new Notification(title, url, postDate);
                            data.add(list);
                        }
                        frameLoading.setVisibility(View.INVISIBLE);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(NotificationActivity.this));


                    }
                    else{

                    }


                }
                catch(JSONException e) {
                    e.printStackTrace();
                }

            }
        },new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse (VolleyError error){
                Log.e("TAG", "Login Error: " + error.getMessage());
                Toast.makeText(NotificationActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        })
        {

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
        AppController.getInstance().

                addToRequestQueue(strReq, tag_string_req);

    }

    @Override
    public void itemClicked(View view, int position) {
        Intent intent = new Intent(NotificationActivity.this, NotificationDetailActivity.class);
        intent.putExtra("url", data.get(position).getUrl());
        intent.putExtra("title", "Berita");
        startActivity(intent);
    }
}
