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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddDepositActivity extends AppCompatActivity {


    RelativeLayout frameLoading, frameBuffer;
    Spinner paymentSpinner;
    Button btnAddDepo;
    String paymentType;
    EditText edtNominal, edtKet;
    CoordinatorLayout coordinatorLayout;

    String id_user, email;

    SessionManager sessionManager;
    HashMap<String, String> user;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_deposit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        frameLoading = (RelativeLayout) findViewById(R.id.frame_loading);
        frameBuffer = (RelativeLayout) findViewById(R.id.frame_buffer);
        paymentSpinner = (Spinner) findViewById(R.id.spinner_payment_deposit);
        btnAddDepo = (Button) findViewById(R.id.btn_tbhdeposit);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        edtNominal = (EditText) findViewById(R.id.edt_nominal_deposit);
        edtKet = (EditText) findViewById(R.id.edt_ket_deposit);

        AppController appController = (AppController) getApplication();
        mTracker = appController.getDefaultTracker();
        sendScreenName();

        viewCheckout();

        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUser();

        id_user = user.get("uid");
        email = user.get("email");
        btnAddDepo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO adddeposit
                frameBuffer.setVisibility(View.VISIBLE);
                String tbh_deposit = edtNominal.getText().toString();
                String ket = edtKet.getText().toString();
                addDeposit(id_user, email,tbh_deposit,paymentType, ket);
            }
        });

    }

    private void viewCheckout() {

        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_DEPOSIT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "View deposit Response: " + response.toString());
                try {
                    if (response.equalsIgnoreCase("")) {
                        frameLoading.setVisibility(View.INVISIBLE);
                    }
                    else {

                        JSONArray jArrPay = new JSONArray(response);
                        int lengthPay = jArrPay.length();
                        List<String> paymentList = new ArrayList<String>();
                        final String[] id_payment = new String[lengthPay];
                        final String[] nama_payment = new String[lengthPay];
                        for (int i = 0; i < lengthPay; i++) {
                            JSONObject jObj = (JSONObject) jArrPay.get(i);
                            paymentList.add(jObj.getString("payment_name"));
                            id_payment[i] = jObj.getString("payment_type");

                        }
                        ArrayAdapter<String> paymentAdapter = new ArrayAdapter<String>(AddDepositActivity.this, android.R.layout.simple_spinner_item, paymentList);
                        paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        paymentSpinner.setAdapter(paymentAdapter);

                        paymentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                // On selecting a spinner item
                                String item = parent.getItemAtPosition(position).toString();
                                paymentType = id_payment[position];

//                                Toast.makeText(AddDepositActivity.this, paymentType, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        frameLoading.setVisibility(View.INVISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "View Checkout Error: " + error.getMessage());
                Toast.makeText(AddDepositActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
//                frameLoading.setVisibility(View.INVISIBLE);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "viewadddeposit");

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

    private void addDeposit(final String id_user, final String email, final String tbh_deposit,
                                final String payment_type, final String ket) {

        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_DEPOSIT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "View chckout Response: " + response.toString());
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
                                    .setAction("Tambah Deposit")
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
                Log.e("TAG", "View Checkout Error: " + error.getMessage());
                Toast.makeText(AddDepositActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
                frameBuffer.setVisibility(View.INVISIBLE);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "adddeposit");
                params.put("id_user", id_user);
                params.put("email", email);
                params.put("tbh_deposit", tbh_deposit);
                params.put("ket", ket);
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
