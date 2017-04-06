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

public class TarikDepositActivity extends AppCompatActivity {

    TextView txtDepo;
    EditText edtTarik;
    Spinner spinnerRekening;
    Button btnTarik;

    RelativeLayout frameLoading, frameBuffer;
    SessionManager sessionManager;
    HashMap<String, String> user;

    String id_user, email;
    String rekeningID;

    CoordinatorLayout coordinatorLayout;

    String depo;
    private Tracker mTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarik_deposit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtDepo = (TextView) findViewById(R.id.txt_nominal_deposit);
        edtTarik = (EditText) findViewById(R.id.edt_nominal_tarik);
        spinnerRekening = (Spinner) findViewById(R.id.spinner_rekening_tarik);
        frameLoading = (RelativeLayout) findViewById(R.id.frame_loading);
        frameBuffer = (RelativeLayout) findViewById(R.id.frame_buffer);
        btnTarik = (Button) findViewById(R.id.btn_tarikdeposit);
        coordinatorLayout= (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        AppController appController = (AppController) getApplication();
        mTracker = appController.getDefaultTracker();
        sendScreenName();

        Bundle b = getIntent().getExtras();
        depo = b.getString("deposit");
        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUser();
        id_user = user.get("uid");
        email = user.get("email");

        viewTarik(id_user, email);

        btnTarik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tarik = edtTarik.getText().toString();

                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Tarik Deposit")
                        .build());

                if (Integer.parseInt(tarik) > Integer.parseInt(depo)){

                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Saldo deposit Kurang", Snackbar.LENGTH_LONG)
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
                else if(Integer.parseInt(tarik) < 50000){
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Tidak dapat menarik kurang dari 50000", Snackbar.LENGTH_LONG)
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
                else {
                    confirmPenarikan(edtTarik.getText().toString());
                }
            }
        });



    }

    private void viewTarik(final String id_user, final String email) {

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

                        JSONObject jsonObject = new JSONObject(response);
                        boolean error = jsonObject.getBoolean("error");

                        if (!error){

                            String bank = jsonObject.getString("bank");
                            String totaldepo = jsonObject.getString("total");
                            JSONArray jArrDel = new JSONArray(bank);
                            int lengthDel = jArrDel.length();
                            final String[] id_rekening = new String[lengthDel];
                            final String[] nama_bank = new String[lengthDel];
                            final String[] no_rekening = new String[lengthDel];
                            final String[] nama_rekening = new String[lengthDel];

                            List<String> rekeningList = new ArrayList<String>();

                            for (int i = 0; i < lengthDel; i++) {
                                JSONObject jObj = (JSONObject) jArrDel.get(i);
                                id_rekening[i] = jObj.getString("id_rekening");
                                nama_bank[i] = jObj.getString("bank");
                                no_rekening[i] = jObj.getString("norek");
                                nama_rekening[i] = jObj.getString("nama_rek");

                                if (id_rekening[i].equalsIgnoreCase("0")){
                                    rekeningList.add(nama_bank[i]);
                                }
                                else{
                                    rekeningList.add(nama_bank[i]+" "+no_rekening[i]+" a/n "+nama_rekening[i]);
                                }


                            }
                            ArrayAdapter<String> paymentAdapter = new ArrayAdapter<String>(TarikDepositActivity.this, android.R.layout.simple_spinner_item, rekeningList);
                            paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerRekening.setAdapter(paymentAdapter);

                            spinnerRekening.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    // On selecting a spinner item
                                    String item = parent.getItemAtPosition(position).toString();
                                    rekeningID = id_rekening[position];

//                                    Toast.makeText(TarikDepositActivity.this, rekeningID, Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                            depo = totaldepo;
                            txtDepo.setText(totaldepo);
                            frameLoading.setVisibility(View.INVISIBLE);
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
                Toast.makeText(TarikDepositActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
                frameLoading.setVisibility(View.INVISIBLE);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "viewtarikdeposit");
                params.put("id_user", id_user);
                params.put("email", email);

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

    private void Penarikan(final String id_user, final String email, final String rekening, final String total_tarik,
                              final String total_depo) {

        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_DEPOSIT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "View chckout Response: " + response.toString());
                try {
                    if (response.equalsIgnoreCase("")) {
                        frameBuffer.setVisibility(View.INVISIBLE);
                    }
                    else {
                        JSONObject jsonObject = new JSONObject(response);

                        boolean error = jsonObject.getBoolean("error");
                        String message = jsonObject.getString("message");

                        if (!error){
                            confirmFinish(message);
                        }
                        else {
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

                        frameBuffer.setVisibility(View.INVISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "View Checkout Error: " + error.getMessage());
                Toast.makeText(TarikDepositActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
                frameBuffer.setVisibility(View.INVISIBLE);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "tarikdeposit");
                params.put("id_user", id_user);
                params.put("email", email);
                params.put("rekening", rekening);
                params.put("total_tarik", total_tarik);
                params.put("total_depo", total_depo);

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

    private void confirmPenarikan(final String totalTarik) {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi")
                .setMessage("Anda Yakin mau melakukan penarikan?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        finish();
                        Penarikan(id_user, email, rekeningID, totalTarik, depo);
                    }
                })
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        finish();
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
                        finish();
                    }
                })
                .show();
    }
    private void sendScreenName() {
        String name = "Tarik Deposit";

        // [START screen_view_hit]
        Log.i("TarikDeposit", "Screen name: " + name);
        mTracker.setScreenName("Screen" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // [END screen_view_hit]
    }
}
