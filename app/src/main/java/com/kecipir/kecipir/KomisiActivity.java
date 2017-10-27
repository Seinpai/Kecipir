package com.kecipir.kecipir;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import com.kecipir.kecipir.data.PembayaranList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class KomisiActivity extends AppCompatActivity {

    Toolbar toolbar;
    TableLayout tableLayout;

    SessionManager sessionManager;
    HashMap<String, String> user;
    private Tracker mTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_komisi);
        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AppController appController = (AppController) getApplication();
        mTracker = appController.getDefaultTracker();
        sendScreenName();

        sessionManager = new SessionManager(this);
        user = sessionManager.getUser();
        String email = user.get("email");
        String idUser = user.get("uid");

        tableLayout = (TableLayout) findViewById(R.id.table_komisi);

        parseKomisi(email, idUser);
//        for (int i = 0;i<5;i++) {
//            TableRow tr = new TableRow(this);
//            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
//            tr.setWeightSum(2);
//            Button b = new Button(this);
//            b.setText("Dynamic Button");
//            b.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
//            b.setPadding(8, 8, 8, 8);
//            Button cc = new Button(this);
//            cc.setText("Dynamic Button2");
//            cc.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
//            cc.setPadding(8, 8, 8, 8);
//            tr.addView(b);
//            tr.addView(cc);
//            tableLayout.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
//        }

    }

    private void parseKomisi(final String email, final String id) {

        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "pembayaran List Response: " + response.toString());
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    int length = jsonArray.length();
                    for (int i =0;i<length;i++){
                        JSONObject jObj = (JSONObject) jsonArray.get(i);
                        boolean error = jObj.getBoolean("error");
//                    // Check for error node in json
//                        list = new PembayaranList();
                        if (!error) {
                            String bulan = jObj.getString("bulan");
                            String total = jObj.getString("total_komisi");

                            TableRow tr = new TableRow(KomisiActivity.this);
                            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                            tr.setWeightSum(2);

                            TextView b = new TextView(KomisiActivity.this);
                            b.setText(bulan);
                            b.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
                            b.setPadding(8, 8, 8, 8);
                            b.setBackgroundDrawable(getResources().getDrawable(R.drawable.column_shape));
                            TextView cc = new TextView(KomisiActivity.this);
                            cc.setText("Rp. "+total);
                            cc.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
                            cc.setPadding(8, 8, 8, 8);
                            cc.setBackgroundDrawable(getResources().getDrawable(R.drawable.column_shape));
                            tr.addView(b);
                            tr.addView(cc);
                            tableLayout.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));


                        } else {
                            // Error in login. Get the error message
                            String errorMsg = jObj.getString("error_msg");
                            Toast.makeText(KomisiActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                        }
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
                Toast.makeText(KomisiActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams () {
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "viewkomisi");
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
        String name = "Komisi";

        // [START screen_view_hit]
        Log.i("Komisi", "Screen name: " + name);
        mTracker.setScreenName("Screen" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // [END screen_view_hit]
    }
}
