package com.kecipir.kecipir;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kecipir.kecipir.data.AppConfig;
import com.kecipir.kecipir.data.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckOutActivity extends AppCompatActivity {

    Toolbar toolbar;

    TextView totOrder, txtOngkir, txtDeskripsiPembayaran, txtDeskripsiPengantaran;
    String totalOrder;
    String diskon, preorder;
    Button btnCheckout, btnKupon;
    EditText namaUser, alamatUser, notelpUser, kuponTxt;
    String nama, alamat, notelp, id_user, email, loginAs, deliveryType, paymentType, kuponPublic = "", ongkir = "0", diskon_ongkir = "0";
    SessionManager sessionManager;
    Spinner deliverySpinner, paymentSpinner;

    ImageView kuponIndicator;

    RelativeLayout frameLoading, frameBuffer;


    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        namaUser = (EditText) findViewById(R.id.txt_checkout_nama);
        notelpUser = (EditText) findViewById(R.id.txt_checkout_notelp);
        alamatUser = (EditText) findViewById(R.id.txt_checkout_alamat);
        deliverySpinner = (Spinner) findViewById(R.id.spinner_delivery);
        paymentSpinner = (Spinner) findViewById(R.id.spinner_pembayaran);
        txtDeskripsiPembayaran = (TextView) findViewById(R.id.deskripsi_pembayaran);
        txtDeskripsiPengantaran = (TextView) findViewById(R.id.deskripsi_pengantaran);

        kuponTxt = (EditText) findViewById(R.id.txt_checkout_kupon);

        kuponIndicator = (ImageView) findViewById(R.id.kupon_indicator);
        btnKupon = (Button) findViewById(R.id.btn_checkout_kupon);

        frameLoading = (RelativeLayout) findViewById(R.id.frame_loading);
        frameBuffer = (RelativeLayout) findViewById(R.id.frame_buffer);
        txtOngkir = (TextView) findViewById(R.id.txt_checkout_ongkir);

        AppController appController = (AppController) getApplication();
        mTracker = appController.getDefaultTracker();
        sendScreenName();

        Bundle b = getIntent().getExtras();
        totalOrder = b.getString("subtotal");
        preorder = totalOrder;
        diskon = "0";

        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUser();
        id_user = user.get("uid");
        email = user.get("email");
        loginAs = user.get("loginAs");
        viewCheckout(id_user, email, loginAs);

        totOrder = (TextView) findViewById(R.id.txt_checkout_harga_total);
        btnCheckout = (Button) findViewById(R.id.btn_check_out);
        totOrder.setText(totalOrder);

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                nama = namaUser.getText().toString();
                alamat = alamatUser.getText().toString();
                notelp = notelpUser.getText().toString();

                if (nama.equalsIgnoreCase("")||alamat.equalsIgnoreCase("")||notelp.equalsIgnoreCase("")){
                    Toast.makeText(CheckOutActivity.this, "Kolom Alamat dan No Telp tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }
                else {
                    if(paymentType.equalsIgnoreCase("8")){
                        if (Integer.parseInt(totalOrder) < 20000){
                            Toast.makeText(CheckOutActivity.this, "Bank Transfer minimal belanja adalah Rp. 20.000", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            new AlertDialog.Builder(CheckOutActivity.this)
                                .setTitle("Konfirmasi Belanja")
                                .setMessage("Apakah Anda yakin dengan belanjaan anda?\nTransaksi tidak dapat dibatalkan setelah anda melakukan konfirmasi")
                                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        frameBuffer.setVisibility(View.VISIBLE);
                                        totalOrder = totOrder.getText().toString();

                                        toXendit(id_user, email, nama, alamat, notelp, paymentType, deliveryType, totalOrder, kuponPublic, String.valueOf(Integer.parseInt(ongkir) - Integer.parseInt(diskon_ongkir)), preorder, diskon);
                                    }
                                })
                                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                })
                                .show();
                        }

                    }
                    else{
                        confirmCheckout();
                    }
                }
            }
        });

        btnKupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kuponPublic = kuponTxt.getText().toString();
                if (kuponPublic.equalsIgnoreCase("")){
                    Toast.makeText(CheckOutActivity.this, "Kupon kosong", Toast.LENGTH_SHORT).show();
                }
                else {
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Action")
                            .setAction("Apply Coupon")
                            .build());
                    applyCoupon(id_user, email, totalOrder, kuponPublic, deliveryType);
                }
//                veriTransInitialized();
            }
        });

    }

//    @Override
//    public void onBackPressed(){
//        super.onBackPressed();
//    }
//    private void SavePreferences(){
//        SharedPreferences SP = getPreferences(MODE_PRIVATE);
//        SharedPreferences.Editor editor = getSharedPreferences().edit();
//    }  BELOM KELAR

    private void viewCheckout(final String id_user, final String email, final String loginAs) {

        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "View chckout Response: " + response.toString());
                try {
                    if (response.equalsIgnoreCase("")) {
                        frameLoading.setVisibility(View.INVISIBLE);
                    }
                    else {
                        JSONObject jsonObject = new JSONObject(response);

                        String alamat = jsonObject.getString("alamat");
                        String nama = jsonObject.getString("nama");
                        String noTelp = jsonObject.getString("no_telp");
                        String delivery = jsonObject.getString("delivery");
                        final String payment = jsonObject.getString("payment");

                        namaUser.setText(nama);
                        alamatUser.setText(alamat);
                        notelpUser.setText(noTelp);

                        if (nama.equalsIgnoreCase("") || nama.equalsIgnoreCase("null")){
                            namaUser.setBackgroundColor(Color.parseColor("#ffffff"));
                            namaUser.setFocusableInTouchMode(true);
                            namaUser.setFocusable(true);
                            namaUser.setText("");
                        }
                        if (alamat.equalsIgnoreCase("") || alamat.equalsIgnoreCase("null")){
                            alamatUser.setBackgroundColor(Color.parseColor("#ffffff"));
                            alamatUser.setFocusableInTouchMode(true);
                            alamatUser.setFocusable(true);
                            alamatUser.setText("");
                        }
                        if (noTelp.equalsIgnoreCase("") || noTelp.equalsIgnoreCase("null")){
                            notelpUser.setBackgroundColor(Color.parseColor("#ffffff"));
                            notelpUser.setFocusableInTouchMode(true);
                            notelpUser.setFocusable(true);
                            notelpUser.setText("");
                        }
                        JSONArray jArrDel = new JSONArray(delivery);
                        int lengthDel = jArrDel.length();
                        final String[] id_delivery = new String[lengthDel];
                        final String[] nama_delivery = new String[lengthDel];
                        final String[] cost = new String[lengthDel];
                        final String[] ket = new String[lengthDel];
                        
                        List<String> deliveryList = new ArrayList<String>();
                        for (int i = 0; i < lengthDel; i++) {
                            JSONObject jObj = (JSONObject) jArrDel.get(i);
                            deliveryList.add(jObj.getString("delivery_name"));

                            id_delivery[i] = jObj.getString("delivery_type");
                            nama_delivery[i] = jObj.getString("delivery_name");
                            cost[i] = jObj.getString("ongkir");
                            ket[i] = jObj.getString("ket");
                        }
                        final ArrayAdapter<String> deliveryAdapter = new ArrayAdapter<String>(CheckOutActivity.this, android.R.layout.simple_spinner_item, deliveryList);
                        deliveryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        deliverySpinner.setAdapter(deliveryAdapter);


                        
                        final JSONArray jArrPay = new JSONArray(payment);
                        final int lengthPay = jArrPay.length();
                        final List<String> paymentList = new ArrayList<String>();
                        final String[] id_payment = new String[lengthPay];
                        final String[] nama_payment = new String[lengthPay];
                        final String[] descripion_payment = new String[lengthPay];

                        deliverySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                // On selecting a spinner item
                                String item = parent.getItemAtPosition(position).toString();
                                deliveryType = id_delivery[position];
                                ongkir = cost[position];
                                txtDeskripsiPengantaran.setText(ket[position]);
//                                cekDelivery(ongkir);
                                if (deliveryType.equalsIgnoreCase("1")){
                                    txtOngkir.setVisibility(View.INVISIBLE);
                                    final List<String> paymentList = new ArrayList<String>();
                                    for (int i = 0; i < lengthPay; i++) {
                                        JSONObject jObj = null;
                                        try {
                                            jObj = (JSONObject) jArrPay.get(i);
                                            paymentList.add(jObj.getString("payment_name"));
                                            id_payment[i] = jObj.getString("payment_type");
                                            descripion_payment[i] = jObj.getString("description");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                    ArrayAdapter<String> paymentAdapter = new ArrayAdapter<String>(CheckOutActivity.this, android.R.layout.simple_spinner_item, paymentList);
                                    paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    paymentSpinner.setAdapter(paymentAdapter);
                                }
                                else{
                                    txtOngkir.setVisibility(View.VISIBLE);
                                    final List<String> paymentList = new ArrayList<String>();
                                    String Diskonongkir = String.valueOf(Integer.parseInt(ongkir) - Integer.parseInt(diskon_ongkir));
                                    txtOngkir.setText("+ Rp. "+Diskonongkir);
                                    int x = 0;
                                    for (int i = 0; i < lengthPay; i++) {
                                        JSONObject jObj = null;
                                        try {
                                            jObj = (JSONObject) jArrPay.get(i);
                                            if (!jObj.getString("payment_type").equalsIgnoreCase("1")){

                                                paymentList.add(jObj.getString("payment_name"));
                                                id_payment[x] = jObj.getString("payment_type");
                                                descripion_payment[x] = jObj.getString("description");
                                                x++;
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                    ArrayAdapter<String> paymentAdapter = new ArrayAdapter<String>(CheckOutActivity.this, android.R.layout.simple_spinner_item, paymentList);
                                    paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    paymentSpinner.setAdapter(paymentAdapter);
                                }
//                                Toast.makeText(CheckOutActivity.this, deliveryType+"-"+ongkir, Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                        paymentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                // On selecting a spinner item
                                String item = parent.getItemAtPosition(position).toString();
//                                if (ongkir.equalsIgnoreCase("0")){
//
                                    paymentType = id_payment[position];
                                    txtDeskripsiPembayaran.setText(descripion_payment[position]);
//                                }
//                                else{
//                                    paymentType = id_payment[position+1];
//                                    txtDeskripsiPembayaran.setText(descripion_payment[position+1]);
//                                }

//                                Toast.makeText(CheckOutActivity.this, paymentType, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(CheckOutActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
//                frameLoading.setVisibility(View.INVISIBLE);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "viewcheckout");
                params.put("id_user", id_user);
                params.put("email", email);
                params.put("loginAs", loginAs);

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

    private void checkOut(final String id_user, final String email, final String nama, final String alamat,
                          final String notelp, final String paymentType, final String deliveryType, final String totalOrder,
                          final String kupon, final String ongkir, final String preorder, final String diskon) {

        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

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

                        if (!error){
                            frameBuffer.setVisibility(View.INVISIBLE);
                            mTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("Action")
                                    .setAction("Checkout")
                                    .build());
                            checkoutFinish();
                        }
                        else {
                            frameBuffer.setVisibility(View.INVISIBLE);
                            Toast.makeText(CheckOutActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    retry();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Checkout Error: " + error.getMessage());
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                        finish();
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String status = response.getString("status");
                        String message = response.getString("message");

                        Log.e("Error Status", status);
                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message+" Please retry again";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message+ " error 400";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message+" Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
                frameBuffer.setVisibility(View.INVISIBLE);
//                retry();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                params.put("tag", "checkout");
                params.put("id_user", id_user);
                params.put("email", email);
                params.put("nama", nama);
                params.put("alamat", alamat);
                params.put("no_telp", notelp);
                params.put("payment_type", paymentType);
                params.put("delivery_type", deliveryType);
                params.put("subtotal", totalOrder);
                params.put("kupon", kupon);
                params.put("ongkir", ongkir);
                params.put("preorder", preorder);
                params.put("diskon", diskon);

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
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void confirmCheckout() {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Belanja")
                .setMessage("Apakah Anda yakin dengan belanjaan anda?\nTransaksi tidak dapat dibatalkan setelah anda melakukan konfirmasi")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        frameBuffer.setVisibility(View.VISIBLE);
//                        if (paymentType.equals("6")){
//                            Toast.makeText(CheckOutActivity.this, "Do PaymentGateWay", Toast.LENGTH_SHORT).show();
//                        }
//                        else {
                            totalOrder = totOrder.getText().toString();
                            checkOut(id_user, email, nama, alamat, notelp, paymentType, deliveryType, totalOrder, kuponPublic, String.valueOf(Integer.parseInt(ongkir) - Integer.parseInt(diskon_ongkir)), preorder, diskon);
//                        }
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .show();
    }

    private void checkoutFinish() {
        new AlertDialog.Builder(this)
                .setTitle("Transaksi Berhasil")
                .setMessage("Terima Kasih telah berbelanja di Kecipir")
                .setPositiveButton("Lihat Riwayat Pembelian", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(CheckOutActivity.this, HistoryActivity.class );
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("Lanjut Belanja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(CheckOutActivity.this, MainActivity.class );
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                })
                .show();
    }

    private void retry() {

        frameBuffer.setVisibility(View.INVISIBLE);
        new AlertDialog.Builder(this)
                .setTitle("Koneksi Bermasalah ")
                .setMessage("Coba lagi")
                .setPositiveButton("Coba lagi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        confirmCheckout();
                    }
                })
                .setNegativeButton("batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .show();
    }

    private void applyCoupon(final String id_user, final String email, final String totalOrder, final String kupon, final String delivery_type) {

        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN,
//                "http://192.168.0.25/kecipir_5/api/tesapi.php",
                new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "Coupon Response: " + response.toString());
                try {
                    if (response.equalsIgnoreCase("")) {
                        frameBuffer.setVisibility(View.INVISIBLE);
                    }
                    else {

                        JSONObject jsonObject = new JSONObject(response);
                        boolean error = jsonObject.getBoolean("error");

                        if (!error){
                            frameBuffer.setVisibility(View.INVISIBLE);
                            diskon = jsonObject.getString("diskon");
                            diskon_ongkir = jsonObject.getString("diskon_ongkir");
//                            preorder = totalOrder;
                            int potongDiskon = Integer.parseInt(totalOrder) - Integer.parseInt(diskon);
                            String diskonOngkir  = String.valueOf(Integer.parseInt(ongkir) - Integer.parseInt(diskon_ongkir));
                            txtOngkir.setText("+ Rp. "+diskonOngkir);

                            totOrder.setText(potongDiskon+"");
                            kuponIndicator.setImageResource(R.drawable.ic_check);
                            kuponPublic = kupon;
                            btnKupon.setEnabled(false);
                            kuponTxt.setEnabled(false);
                            Toast.makeText(CheckOutActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                        else {
                            kuponIndicator.setImageResource(R.drawable.ic_cross);
                            Toast.makeText(CheckOutActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    retry();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", " Coupon Error: " + error.getMessage());
                Toast.makeText(CheckOutActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
                frameBuffer.setVisibility(View.INVISIBLE);
                retry();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                params.put("tag", "applycoupon");
                params.put("id_user", id_user);
                params.put("email", email);
                params.put("subtotal", totalOrder);
                params.put("kupon", kupon);
                params.put("delivery_type", delivery_type);

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


    private void toXendit(final String id_user, final String email, final String nama, final String alamat,
                          final String notelp, final String paymentType, final String deliveryType, final String totalOrder,
                          final String kupon, final String ongkir, final String preorder, final String diskon) {

        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_XENDITCHECKOUT, new Response.Listener<String>() {

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

                        if (!error){
                            frameBuffer.setVisibility(View.INVISIBLE);
                            mTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("Action")
                                    .setAction("Xendit Checkout")
                                    .build());

                            Intent intent = new Intent(CheckOutActivity.this, XenditConfirmationActivity.class);
                            intent.putExtra("action", "checkout");
                            intent.putExtra("no_nota", jsonObject.getString("no_nota"));
                            startActivity(intent);
                        }
                        else {
                            frameBuffer.setVisibility(View.INVISIBLE);
                            Toast.makeText(CheckOutActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    retry();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Checkout Error: " + error.getMessage());
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                        finish();
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String status = response.getString("status");
                        String message = response.getString("message");

                        Log.e("Error Status", status);
                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message+" Please retry again";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message+ " error 400";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message+" Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
                frameBuffer.setVisibility(View.INVISIBLE);
//                retry();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                params.put("tag", "submit");
                params.put("id_user", id_user);
                params.put("email", email);
                params.put("nama", nama);
                params.put("alamat", alamat);
                params.put("no_telp", notelp);
                params.put("payment_type", paymentType);
                params.put("delivery_type", deliveryType);
                params.put("subtotal", totalOrder);
                params.put("kupon", kupon);
                params.put("ongkir", ongkir);
                params.put("preorder", preorder);
                params.put("diskon", diskon);
                params.put("platform", "1");

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
        String name = "Checkout";

        // [START screen_view_hit]
        Log.i("Checkout", "Screen name: " + name);
        mTracker.setScreenName("Screen" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // [END screen_view_hit]
    }


}
