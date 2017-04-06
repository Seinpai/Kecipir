package com.kecipir.kecipir;

import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
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
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.google.android.gms.common.api.GoogleApiClient;
import com.kecipir.kecipir.data.AppConfig;
import com.kecipir.kecipir.data.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KonfirmDepositActivity extends AppCompatActivity {


    RelativeLayout frameLoading, frameBuffer, layoutBtn;
    LinearLayout layoutRekening;
    Spinner spinnerRekening;

    EditText edtNamaPemilik, edtNoRek, edtNamaBank, edtTotal, edtTglBayar, edtKet;

    String idRekening, namaBank, namaRekening, noRekening;
    String id_user, email, no_deposit;

    SessionManager sessionManager;
    HashMap<String, String> user;

    CoordinatorLayout coordinatorLayout;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konfirm_deposit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        frameLoading = (RelativeLayout) findViewById(R.id.frame_loading);
        frameBuffer = (RelativeLayout) findViewById(R.id.frame_buffer);
        layoutRekening = (LinearLayout) findViewById(R.id.layout_rekening);
        layoutBtn = (RelativeLayout) findViewById(R.id.layout_btn_konfirm_deposit);
        spinnerRekening = (Spinner) findViewById(R.id.spinner_rekening);

        edtNamaPemilik = (EditText) findViewById(R.id.edt_konfirmdeposit_pemilik);
        edtNoRek = (EditText) findViewById(R.id.edt_konfirmdeposit_rekening);
        edtNamaBank = (EditText) findViewById(R.id.edt_konfirmdeposit_bank);
        edtTotal = (EditText) findViewById(R.id.edt_konfirmdeposit_total);
        edtTglBayar = (EditText) findViewById(R.id.edt_konfirmdeposit_tglbyr);
        edtKet = (EditText) findViewById(R.id.edt_konfirmdeposit_ket);

        AppController appController = (AppController) getApplication();
        mTracker = appController.getDefaultTracker();
        sendScreenName();

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);

        Bundle b = getIntent().getExtras();
        no_deposit = b.getString("no_deposit");
        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUser();
        id_user = user.get("uid");
        email = user.get("email");

        viewKonfirmDeposit(id_user, email, no_deposit);

        layoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edtNamaPemilik.getText().toString().equalsIgnoreCase("") ||
                        edtNoRek.getText().toString().equalsIgnoreCase("") ||
                        edtNamaBank.getText().toString().equalsIgnoreCase("") ||
                        edtTotal.getText().toString().equalsIgnoreCase("") ||
                        edtTglBayar.getText().toString().equalsIgnoreCase("")) {
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Ada Kolom yang Kosong", Snackbar.LENGTH_LONG)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                }
                            });
                    View snackbarView = snackbar.getView();
                    TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.WHITE);
                    snackbar.show();
                } else {
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Action")
                            .setAction("Konfirmasi Deposit")
                            .build());

                    frameBuffer.setVisibility(View.VISIBLE);
                    confirmDeposit(id_user, email, no_deposit, idRekening, edtTglBayar.getText().toString(),
                            edtTotal.getText().toString(), edtNamaPemilik.getText().toString(), edtNoRek.getText().toString(),
                            edtNamaBank.getText().toString());
                }
            }
        });
    }

    private void viewKonfirmDeposit(final String id_user, final String email, final String no_deposit) {

        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_DEPOSIT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "View chckout Response: " + response.toString());
                try {
                    if (response.equalsIgnoreCase("")) {
                        frameLoading.setVisibility(View.INVISIBLE);
                    } else {
                        JSONObject jsonObject = new JSONObject(response);
//
//                        String alamat = jsonObject.getString("alamat");
//                        String nama = jsonObject.getString("nama");
//                        String noTelp = jsonObject.getString("no_telp");
//                        String delivery = jsonObject.getString("delivery");
                        String bank = jsonObject.getString("bank");
                        String total = jsonObject.getString("total");
                        String tgl_bayar = jsonObject.getString("tgl_bayar");
                        String ket = jsonObject.getString("ket");


                        edtTotal.setText(total);
                        edtTglBayar.setText(tgl_bayar);
                        edtKet.setText(ket);
//

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

                            if (id_rekening[i].equalsIgnoreCase("0")) {
                                rekeningList.add(nama_bank[i]);
                            } else {
                                rekeningList.add(nama_bank[i] + " " + no_rekening[i] + " a/n " + nama_rekening[i]);
                            }


                        }

                        ArrayAdapter<String> rekeningAdapter = new ArrayAdapter<String>(KonfirmDepositActivity.this, android.R.layout.simple_spinner_item, rekeningList);
                        rekeningAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerRekening.setAdapter(rekeningAdapter);

                        spinnerRekening.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                // On selecting a spinner item
                                String item = parent.getItemAtPosition(position).toString();
                                idRekening = id_rekening[position];
                                namaBank = nama_bank[position];
                                namaRekening = nama_rekening[position];
                                noRekening = no_rekening[position];

//                                cekDelivery(ongkir);
                                if (idRekening.equalsIgnoreCase("0")) {
                                    layoutRekening.setVisibility(View.VISIBLE);
                                    edtNamaPemilik.setText("");
                                    edtNoRek.setText("");
                                    edtNamaBank.setText("");
                                } else {
                                    layoutRekening.setVisibility(View.GONE);
                                    edtNamaPemilik.setText(namaRekening);
                                    edtNoRek.setText(noRekening);
                                    edtNamaBank.setText(namaBank);
                                }
//                                Toast.makeText(KonfirmPembayaranActivity.this, deliveryType+"-"+ongkir, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(KonfirmDepositActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
//                frameLoading.setVisibility(View.INVISIBLE);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "viewconfirmdeposit");
                params.put("id_user", id_user);
                params.put("email", email);
                params.put("no_deposit", no_deposit);

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

    private void confirmDeposit(final String id_user, final String email, final String no_deposit,
                                final String rekening, final String tgl_bayar, final String total,
                                final String nama, final String no_rek, final String bank) {

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
                Toast.makeText(KonfirmDepositActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
                frameBuffer.setVisibility(View.INVISIBLE);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "confirmdeposit");
                params.put("id_user", id_user);
                params.put("email", email);
                params.put("no_deposit", no_deposit);
                params.put("rekening", rekening);
                params.put("tglbyr", tgl_bayar);
                params.put("total", total);
                params.put("nama_pemilik", nama);
                params.put("nama_bank", bank);
                params.put("no_rek", no_rek);

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
        String name = "Konfirmasi Deposit";

        // [START screen_view_hit]
        Log.i("Konfirm Deposit", "Screen name: " + name);
        mTracker.setScreenName("Screen" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // [END screen_view_hit]
    }


}
