package com.kecipir.kecipir;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
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
import com.kecipir.kecipir.adapter.DataMemberAdapter;
import com.kecipir.kecipir.adapter.DepositAdapter;
import com.kecipir.kecipir.data.AppConfig;
import com.kecipir.kecipir.data.AppController;
import com.kecipir.kecipir.data.ClickListener;
import com.kecipir.kecipir.data.DataMember;
import com.kecipir.kecipir.data.DepositList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemberAgenActivity extends AppCompatActivity  implements ClickListener {


    RelativeLayout frameLoading,frameBuffer;
    RecyclerView recyclerView;
    TextView txtJmlMember;

    DataMemberAdapter adapter;
    SessionManager sessionManager;

    String email, id_user;
    String total;

    DataMember list;
    List<DataMember> data;

    private Tracker mTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_agen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        recyclerView = (RecyclerView) findViewById(R.id.recycler_member);
        frameLoading = (RelativeLayout) findViewById(R.id.frame_loading);
        frameBuffer = (RelativeLayout) findViewById(R.id.frame_buffer);
        txtJmlMember = (TextView) findViewById(R.id.txt_jmlmember);


        AppController appController = (AppController) getApplication();
        mTracker = appController.getDefaultTracker();
        sendScreenName();

        data = new ArrayList<>();
        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUser();
        email = user.get("email");
        id_user = user.get("uid");

        String loginAs = user.get("loginAs");

        parseMember(id_user);
        adapter = new DataMemberAdapter(this, data);

        adapter.setOnClickListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MemberAgenActivity.this, AddmemberActivity.class);
                startActivity(i);

//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

    }

    @Override
    public void itemClicked(View view, int position) {
    }

    private void parseMember(final String id) {

        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_MEMBER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "Member List Response: " + response.toString());
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    String jml_member = jsonObject.getString("jml_member");
                    txtJmlMember.setText(jml_member);
                    String listDeposit = jsonObject.getString("listmember");
                    JSONArray jsonArray = new JSONArray(listDeposit);
                    int length = jsonArray.length();
                    if (length == 0){
                        frameLoading.setVisibility(View.INVISIBLE);
                    }
                    else{
                        list = new DataMember();
                        for (int i =0;i<length;i++){
                            JSONObject jObj = (JSONObject) jsonArray.get(i);

                            String nama_member = jObj.getString("nama_member");
                            String jml_belanja = jObj.getString("jml_belanja");
                            String tgl_acc = jObj.getString("tgl_acc");
                            String no_telp = jObj.getString("no_telp");
                            String email = jObj.getString("email");
                            String alamat = jObj.getString("alamat");

                            list = new DataMember(nama_member, email, no_telp, alamat, tgl_acc, jml_belanja);
                            data.add(list);
                            // Error in login. Get the error message
//                                String errorMsg = jObj.getString("error_msg");
//                                Toast.makeText(ListDepositActivity.this, errorMsg, Toast.LENGTH_LONG).show();
//
                        }
                        frameLoading.setVisibility(View.INVISIBLE);

                        adapter = new DataMemberAdapter(MemberAgenActivity.this, data);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(MemberAgenActivity.this));
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
                Toast.makeText(MemberAgenActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams () {
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "viewlistmember");
                params.put("id_host", id);

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
        String name = "list member";

        // [START screen_view_hit]
        Log.i("List MemberAgen", "Screen name: " + name);
        mTracker.setScreenName("Screen" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // [END screen_view_hit]
    }
}
