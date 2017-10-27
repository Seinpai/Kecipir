package com.kecipir.kecipir;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import com.kecipir.kecipir.adapter.AkunBankAdapter;
import com.kecipir.kecipir.adapter.DepositAdapter;
import com.kecipir.kecipir.data.AkunBankList;
import com.kecipir.kecipir.data.AppConfig;
import com.kecipir.kecipir.data.AppController;
import com.kecipir.kecipir.data.ClickListener;
import com.kecipir.kecipir.data.DepositList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AkunBankActivity extends AppCompatActivity implements ClickListener{


    RelativeLayout frameLoading,frameBuffer;
    RecyclerView recyclerView;
    Button btnTarik;

    AkunBankAdapter adapter;
    SessionManager sessionManager;

    String email, id_user;
    String total;


    AkunBankList list;
    List<AkunBankList> data;

    private Tracker mTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_akun_bank);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_akunbank);
        frameLoading = (RelativeLayout) findViewById(R.id.frame_loading);

        AppController appController = (AppController) getApplication();
        mTracker = appController.getDefaultTracker();
        sendScreenName();


        data = new ArrayList<>();
        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUser();
        email = user.get("email");
        id_user = user.get("uid");
        parseAkunBank(email, id_user);
        adapter = new AkunBankAdapter(this, data);

        adapter.setOnClickListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AkunBankActivity.this, AddAkunBankActivity.class);
                startActivity(i);

//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

    }

    private void parseAkunBank(final String email, final String id) {

        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_BANK, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "Akun Bank List Response: " + response.toString());
                try {

                    JSONArray jsonArray = new JSONArray(response);
                    int length = jsonArray.length();
                    if (length == 0){
                        frameLoading.setVisibility(View.INVISIBLE);
                    }
                    else{
                        for (int i =0;i<length;i++){
                            JSONObject jObj = (JSONObject) jsonArray.get(i);
                            list = new AkunBankList();

                            String nama_rek = jObj.getString("nama_rek");
                            String bank = jObj.getString("bank");
                            String no_rek = jObj.getString("no_rek");

                            list = new AkunBankList(nama_rek, bank, no_rek);
                            data.add(list);

//
                        }
                        frameLoading.setVisibility(View.INVISIBLE);

//                        adapter = new AkunBankAdapter(this, data);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(AkunBankActivity.this));
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
                Toast.makeText(AkunBankActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams () {
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "viewakunbank");
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

    private void sendScreenName() {
        String name = "List Akun Bank";

        // [START screen_view_hit]
        Log.i("List Akun Bank", "Screen name: " + name);
        mTracker.setScreenName("Screen " + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // [END screen_view_hit]
    }

    @Override
    public void itemClicked(View view, int position) {

    }
}
