package com.kecipir.kecipir;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.view.View;
import android.widget.AdapterView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddGreencashActivity extends AppCompatActivity {

    RelativeLayout frameLoading, frameBuffer;
    Button btnAddDepo;
    String paymentType;
    EditText edtNominal, edtKet;
    CoordinatorLayout coordinatorLayout;

    String id_user,id_host , email, as;

    Spinner sp_pembayaran;

    SessionManager sessionManager;
    HashMap<String, String> user;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_greencash);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        frameLoading = (RelativeLayout) findViewById(R.id.frame_loading);
        frameBuffer = (RelativeLayout) findViewById(R.id.frame_buffer);
        btnAddDepo = (Button) findViewById(R.id.btn_tbhdeposit);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        edtNominal = (EditText) findViewById(R.id.edt_nominal_deposit);

        AppController appController = (AppController) getApplication();
        mTracker = appController.getDefaultTracker();
        sendScreenName();

        sp_pembayaran = (Spinner)findViewById(R.id.sp_pembayaran);

        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUser();

        id_user = user.get("uid");
        email = user.get("email");
        as = user.get("loginAs");

        Bundle b = getIntent().getExtras();
        id_host = b.getString("id_host");


        Log.i("host:","id_host :"+id_host+ "user :"+id_user + "email :"+email + " Login As " + as);


        JSONObject Pem2 = new JSONObject();
        try {
            Pem2.put("id", "12");
            Pem2.put("nama", "BCA Bank Transfer");
            Pem2.put("desc", "Pembayaran Melalui Virtual Account Bank BCA");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject Pem3 = new JSONObject();
        try {
            Pem3.put("id", "13");
            Pem3.put("nama", "Mandiri Bank Transfer");
            Pem3.put("desc", "Pembayaran Melalui Virtual Account Bank Mandiri");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject Pem4 = new JSONObject();
        try {
            Pem4.put("id", "14");
            Pem4.put("nama", "BNI Bank Transfer");
            Pem4.put("desc", "Pembayaran Melalui Virtual Account Bank BNI");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject Pem5 = new JSONObject();
        try {
            Pem5.put("id", "15");
            Pem5.put("nama", "BRI Bank Transfer");
            Pem5.put("desc", "Pembayaran Melalui Virtual Account Bank BRI");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject Pem6 = new JSONObject();
        try {
            Pem6.put("id", "16");
            Pem6.put("nama", "CIMB Niaga Bank Transfer");
            Pem6.put("desc", "Pembayaran Melalui Virtual Account Bank CIMB Niaga");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject Pem7 = new JSONObject();
        try {
            Pem7.put("id", "17");
            Pem7.put("nama", "Permata Bank Transfer");
            Pem7.put("desc", "Pembayaran Melalui Virtual Account Bank Permata");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject Pem8 = new JSONObject();
        try {
            Pem8.put("id", "18");
            Pem8.put("nama", "BII Maybank Transfer");
            Pem8.put("desc", "Pembayaran Melalui Virtual Account Bank BII Maybank");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject Pem9 = new JSONObject();
        try {
            Pem9.put("id", "19");
            Pem9.put("nama", "Danamon Bank Transfer");
            Pem9.put("desc", "Pembayaran Melalui Virtual Account Bank Danamon");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject Pem10 = new JSONObject();
        try {
            Pem10.put("id", "20");
            Pem10.put("nama", "KEB HANA Bank Transfer");
            Pem10.put("desc", "Pembayaran Melalui Virtual Account KEB HANA Bank");
        } catch (JSONException e) {
            e.printStackTrace();
        }



        JSONArray jsonArray1 = new JSONArray();
        jsonArray1.put(Pem2);
        jsonArray1.put(Pem3);
        jsonArray1.put(Pem4);
        jsonArray1.put(Pem5);
        jsonArray1.put(Pem6);
        jsonArray1.put(Pem7);
        jsonArray1.put(Pem8);
        jsonArray1.put(Pem9);
        jsonArray1.put(Pem10);

        final int lengthPay = jsonArray1.length();
        final List<String> paymentList = new ArrayList<String>();
        final String[] id_payment = new String[lengthPay];
        final String[] descripion_payment = new String[lengthPay];
        final String[] nama_bank = new String[lengthPay];
        for (int i = 0; i < lengthPay; i++) {
            JSONObject jObj = null;
            try {
                jObj = (JSONObject) jsonArray1.get(i);
                paymentList.add(jObj.getString("nama"));
                id_payment[i] = jObj.getString("id");
                descripion_payment[i] = jObj.getString("desc");
                nama_bank[i] = jObj.getString("nama");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        ArrayAdapter<String> paymentAdapter = new ArrayAdapter<String>(AddGreencashActivity.this, android.R.layout.simple_spinner_item, paymentList);
        paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_pembayaran.setAdapter(paymentAdapter);

        sp_pembayaran.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // On selecting a spinner item
                String item = parent.getItemAtPosition(position).toString();
                paymentType = id_payment[position];
                //Toast.makeText(AddGreencashActivity.this, paymentType, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        //Toast.makeText(AddGreencashActivity.this, id_host, Toast.LENGTH_SHORT).show();


        btnAddDepo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO adddeposit
                if (as.equals("host")){
                    id_user = "0";
                }
                frameBuffer.setVisibility(View.VISIBLE);
                String tbh_deposit = edtNominal.getText().toString();
                addDeposit(id_user, id_host, email, tbh_deposit, paymentType);
            }
        });

    }

    private void addDeposit(final String id_user, final String id_host, final String email, final String tbh_deposit,
                            final String payment_type) {

        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_NICEPAYCHECKOUT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "View chckout Response: " + response.toString());
                try {
                    if (response.equalsIgnoreCase("")) {
                        frameBuffer.setVisibility(View.INVISIBLE);
                    } else {
                        frameBuffer.setVisibility(View.INVISIBLE);
                        JSONObject jsonObject = new JSONObject(response);

//                        boolean error = jsonObject.getBoolean("error");
                        String success = jsonObject.getString("success");

                        if (success.equals("1")) {
                            mTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("Action")
                                    .setAction("Tambah Deposit")
                                    .build());
                            Intent intent = new Intent(AddGreencashActivity.this, DetailGreencashActivity.class);
                            intent.putExtra("virtual", jsonObject.getString("virtual"));
                            intent.putExtra("amount", jsonObject.getString("amount"));
                            intent.putExtra("nota", jsonObject.getString("nota"));
                            intent.putExtra("expired", jsonObject.getString("expired"));
                            intent.putExtra("paymentType", paymentType);
                            startActivity(intent);
                            finish();
                        } else {
                            Snackbar snackbar = Snackbar
                                    .make(coordinatorLayout, success, Snackbar.LENGTH_LONG)
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
                Log.e("TAG", "View Checkout Error: " + error.getMessage());
                Toast.makeText(AddGreencashActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
                frameBuffer.setVisibility(View.INVISIBLE);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "add_greencash");
                params.put("id_member", id_user);
                params.put("id_host", id_host);
                params.put("email", email);
                params.put("tbh_deposit", tbh_deposit);
                params.put("payment_type", payment_type);

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
                .setTitle("Transaksi Berhasil")
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
        String name = "Tambah Deposit";

        // [START screen_view_hit]
        Log.i("TambahDeposit", "Screen name: " + name);
        mTracker.setScreenName("Screen" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // [END screen_view_hit]
    }
}
