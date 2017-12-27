package com.kecipir.kecipir;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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

public class CheckOutActivity extends AppCompatActivity {

    Toolbar toolbar;

    TextView totOrder, txtOngkir, txtDeskripsiPembayaran, txtDeskripsiPengantaran;
    String totalOrder;
    String totalOrderfix;
    String diskon, preorder;
    Button btnCheckout, btnKupon;
    EditText namaUser, alamatUser, notelpUser, kuponTxt;
    String nama, alamat, notelp, id_user, email, loginAs, deliveryType, paymentType, kuponPublic = "", ongkir = "0", diskon_ongkir = "0";

    String chck_idUser,chck_namaBank, chck_nama, chck_email, chck_alamat, chck_telp, chck_paymentType, chck_deliveryType, chck_ongkir, chck_platform, chck_typeHost, chck_diskon, chck_subTotal, chck_subTotalFinal;
    SessionManager sessionManager;
    Spinner deliverySpinner, paymentSpinner;

    ImageView kuponIndicator;

    RelativeLayout frameLoading, frameBuffer;

    String ccUrl;
    int c;


    private WebView webView;


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
        totalOrderfix = b.getString("subtotalFix");

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

        Log.i("diskon bayar1","total - " + totalOrder);
        Log.i("diskon bayar1","total - " + totalOrderfix);


        JSONObject Pem1 = new JSONObject();
        try {
            Pem1.put("id", "11");
            Pem1.put("nama", "GreenCash");
            Pem1.put("desc", "Pembayaran Melalui Saldo GreenCash");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject Pem2 = new JSONObject();
        try {
            Pem2.put("id", "22");
            Pem2.put("nama", "Credit Card");
            Pem2.put("desc", "Pembayaran Melalui Kartu Kredit");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject Pem3 = new JSONObject();
        try {
            Pem3.put("id", "12");
            Pem3.put("nama", "BCA Bank Transfer");
            Pem3.put("desc", "Pembayaran Melalui Virtual Account Bank BCA");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject Pem7 = new JSONObject();
        try {
            Pem7.put("id", "13");
            Pem7.put("nama", "Mandiri Bank Transfer");
            Pem7.put("desc", "Pembayaran Melalui Virtual Account Bank Mandiri");
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

        JSONObject Pem11 = new JSONObject();
        try {
            Pem11.put("id", "21");
            Pem11.put("nama", "ATM BERSAMA");
            Pem11.put("desc", "Pembayaran Melalui ATM BERSAMA");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JSONObject Pem12 = new JSONObject();
        try {
            Pem12.put("id", "17");
            Pem12.put("nama", "Permata Bank Transfer");
            Pem12.put("desc", "Pembayaran Melalui Virtual Account Bank Permata");
        } catch (JSONException e) {
            e.printStackTrace();
        }



        JSONArray jsonArray1 = new JSONArray();
        jsonArray1.put(Pem1);
        jsonArray1.put(Pem2);
        jsonArray1.put(Pem3);
        jsonArray1.put(Pem4);
        jsonArray1.put(Pem5);
        jsonArray1.put(Pem6);
        jsonArray1.put(Pem7);
        jsonArray1.put(Pem8);
        jsonArray1.put(Pem9);
        jsonArray1.put(Pem10);
        jsonArray1.put(Pem11);
        jsonArray1.put(Pem12);

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
        ArrayAdapter<String> paymentAdapter = new ArrayAdapter<String>(CheckOutActivity.this, android.R.layout.simple_spinner_item, paymentList);
        paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paymentSpinner.setAdapter(paymentAdapter);

        paymentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // On selecting a spinner item
                String item = parent.getItemAtPosition(position).toString();
                paymentType = id_payment[position];
                txtDeskripsiPembayaran.setText(descripion_payment[position]);
                chck_namaBank = nama_bank[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });




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
                    totalOrder = totOrder.getText().toString();

                    chck_email = email ;
                    chck_nama = nama ;
                    chck_alamat = alamat ;
                    chck_telp = notelp;
                    chck_paymentType = paymentType ;
                    chck_deliveryType = deliveryType  ;
                    chck_idUser = id_user  ;
                    chck_ongkir = ongkir ;
                    chck_platform = "1";
                    chck_typeHost = "member";
                    chck_diskon = diskon;
                    chck_subTotal = totalOrder;
                    chck_subTotalFinal = totalOrder;
                    final int finalTotal = Integer.parseInt(chck_subTotalFinal) + Integer.parseInt(chck_ongkir);

                    Log.i("Post to Process :->",
                            "email->" + chck_email +
                                    "nama->" + chck_nama +
                                    "alamat->" + chck_alamat +
                                    "telp->" + chck_telp +
                                    "paytype->" + chck_paymentType +
                                    "deltype->" + chck_deliveryType +
                                    "iduser->" + chck_idUser +
                                    "ongkir->" + chck_ongkir +
                                    "platform->" + chck_platform +
                                    "typehost->" + chck_typeHost +
                                    "diskon->" + chck_diskon +
                                    "subtotal->" + chck_subTotal +
                                    "subfinal->" + chck_subTotalFinal+
                                    "namabank->"+chck_namaBank
                    );

                    new AlertDialog.Builder(CheckOutActivity.this)
                            .setTitle("Konfirmasi Belanja")
                            .setMessage("Apakah Anda yakin dengan belanjaan anda?\nTransaksi tidak dapat dibatalkan setelah anda melakukan konfirmasi")
                            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    frameBuffer.setVisibility(View.VISIBLE);


                                    if(paymentType.equalsIgnoreCase("22")){
//                                        Toast.makeText(CheckOutActivity.this, "CC", Toast.LENGTH_LONG).show();
                                        readCCCheckout(chck_email,chck_nama,chck_alamat,chck_telp,chck_paymentType,chck_deliveryType,
                                                        chck_idUser,chck_ongkir,chck_platform,chck_typeHost,chck_diskon,chck_subTotal,Integer.parseInt(chck_subTotalFinal));
                                    }else if (paymentType.equalsIgnoreCase("11")){
//                                        Toast.makeText(CheckOutActivity.this, "GC", Toast.LENGTH_LONG).show();
                                        greenCashCheckout("",chck_email,chck_nama,chck_alamat,chck_telp,chck_paymentType,chck_deliveryType,
                                                chck_idUser,chck_ongkir,chck_platform,chck_typeHost,chck_diskon,chck_subTotal,chck_subTotalFinal);

                                    }else {
//                                        Toast.makeText(CheckOutActivity.this, "VA", Toast.LENGTH_LONG).show();
                                        readVACheckout("",chck_email,chck_nama,chck_alamat,chck_telp,chck_paymentType,chck_deliveryType,
                                                chck_idUser,chck_ongkir,chck_platform,chck_typeHost,chck_diskon,chck_subTotal,chck_subTotalFinal);
                                    }

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
        });




        btnKupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kuponPublic = kuponTxt.getText().toString();
                if (kuponPublic.equalsIgnoreCase("")){
                    Toast.makeText(CheckOutActivity.this, "Kupon kosong", Toast.LENGTH_SHORT).show();
//                    Toast.makeText(CheckOutActivity.this, paymentType, Toast.LENGTH_SHORT).show();

                }
                else {
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Action")
                            .setAction("Apply Coupon")
                            .build());
                    if (totalOrder.equals(totalOrderfix)){
                        applyCoupon(id_user, email, totalOrder, kuponPublic, deliveryType);
//                        Toast.makeText(CheckOutActivity.this, "Apply 1", Toast.LENGTH_SHORT).show();
                    }else {
                        applyCoupon(id_user, email, totalOrderfix, kuponPublic, deliveryType);
//                        Toast.makeText(CheckOutActivity.this, "Apply 2", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }



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
//                                    final List<String> paymentList = new ArrayList<String>();
//                                    for (int i = 0; i < lengthPay; i++) {
//                                        JSONObject jObj = null;
//                                        try {
//                                            jObj = (JSONObject) jArrPay.get(i);
//                                            paymentList.add(jObj.getString("payment_name"));
//                                            id_payment[i] = jObj.getString("payment_type");
//                                            descripion_payment[i] = jObj.getString("description");
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
//
//                                    }
//                                    ArrayAdapter<String> paymentAdapter = new ArrayAdapter<String>(CheckOutActivity.this, android.R.layout.simple_spinner_item, paymentList);
//                                    paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                                    paymentSpinner.setAdapter(paymentAdapter);
                                }
                                else{
                                    txtOngkir.setVisibility(View.VISIBLE);
                                    final List<String> paymentList = new ArrayList<String>();
                                    String Diskonongkir = String.valueOf(Integer.parseInt(ongkir) - Integer.parseInt(diskon_ongkir));
                                    txtOngkir.setText("+ Rp. "+Diskonongkir);
                                    int x = 0;
//                                    for (int i = 0; i < lengthPay; i++) {
//                                        JSONObject jObj = null;
//                                        try {
//                                            jObj = (JSONObject) jArrPay.get(i);
//                                            if (!jObj.getString("payment_type").equalsIgnoreCase("1")){
//
//                                                paymentList.add(jObj.getString("payment_name"));
//                                                id_payment[x] = jObj.getString("payment_type");
//                                                descripion_payment[x] = jObj.getString("description");
//                                                x++;
//                                            }
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
//
//                                    }
//                                    ArrayAdapter<String> paymentAdapter = new ArrayAdapter<String>(CheckOutActivity.this, android.R.layout.simple_spinner_item, paymentList);
//                                    paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                                    paymentSpinner.setAdapter(paymentAdapter);
                                }
//                                Toast.makeText(CheckOutActivity.this, deliveryType+"-"+ongkir, Toast.LENGTH_SHORT).show();

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





    private void confirmCheckout() {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Belanja")
                .setMessage("Apakah Anda yakin dengan belanjaan anda?\nTransaksi tidak dapat dibatalkan setelah anda melakukan konfirmasi")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        frameBuffer.setVisibility(View.VISIBLE);
                            totalOrder = totOrder.getText().toString();
//                            checkoutFinish();
//                            checkOut(id_user, email, nama, alamat, notelp, paymentType, deliveryType, totalOrder, kuponPublic, String.valueOf(Integer.parseInt(ongkir) - Integer.parseInt(diskon_ongkir)), preorder, diskon);
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

                            if(totalOrder.equals(totalOrderfix)){

                                int potongDiskon = Integer.parseInt(totalOrder) - Integer.parseInt(diskon);
                                String diskonOngkir  = String.valueOf(Integer.parseInt(ongkir) - Integer.parseInt(diskon_ongkir));
                                txtOngkir.setText("+ Rp. " + diskonOngkir);

                                totOrder.setText(potongDiskon+"");
                                kuponIndicator.setImageResource(R.drawable.ic_check);
                                kuponPublic = kupon;
                                btnKupon.setVisibility(View.INVISIBLE);
                                kuponTxt.setEnabled(false);

                                Log.i("tes", " " + totalOrder + " || " + totalOrderfix);
                                Log.i("diskon bayar", " " + potongDiskon + " = " + totalOrder + " diskon  "  + diskon);
                                Log.i("diskon ongkir", " " + diskonOngkir + " = " + totalOrder + " ongkir "  + diskon_ongkir);

                                Toast.makeText(CheckOutActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                            }else{

                                int potongDiskon = Integer.parseInt(totalOrderfix) - Integer.parseInt(diskon);
                                String diskonOngkir  = String.valueOf(Integer.parseInt(ongkir) - Integer.parseInt(diskon_ongkir));
                                txtOngkir.setText("+ Rp. " + diskonOngkir);

                                totOrder.setText(potongDiskon+"");
                                kuponIndicator.setImageResource(R.drawable.ic_check);
                                kuponPublic = kupon;
                                btnKupon.setVisibility(View.INVISIBLE);
                                kuponTxt.setEnabled(false);

                                Log.i("tes", " " + totalOrder + " || " + totalOrderfix);
                                Log.i("diskon ongkir2 ", " bayar = " + diskonOngkir + " ongkir - " + ongkir + " diskon ongkir "  + diskonOngkir);
                                Log.i("diskon bayar2 ", " bayar = " + diskonOngkir + " total - " + totalOrder + " diskon "  + diskon);

                                Toast.makeText(CheckOutActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                            }
//                            int potongDiskon = Integer.parseInt(totalOrder) - Integer.parseInt(diskon);
//                            String diskonOngkir  = String.valueOf(Integer.parseInt(ongkir) - Integer.parseInt(diskon_ongkir));
//                            txtOngkir.setText("+ Rp. " + diskonOngkir);
//
//                            totOrder.setText(potongDiskon+"");
//                            kuponIndicator.setImageResource(R.drawable.ic_check);
//                            kuponPublic = kupon;
//                            btnKupon.setVisibility(View.INVISIBLE);
//                            kuponTxt.setEnabled(false);
//                            Toast.makeText(CheckOutActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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





    private void readCCCheckout(final String VA_email, final String VA_nama, final String VA_alamat
            ,final String VA_telp, final String VA_payType, final String VA_delType
            , final String VA_idUser, final String VA_ongkir, final String VA_platform
            , final String VA_typeHost, final String VA_diskon, final String VA_subTotal, final int finalTotal)
    {

        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_NICEPAYCHECKOUT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("TAG", "Login Response: " + response.toString());
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    String error = jsonObject.getString("error");

                    if (error.equals("false")){
                        ccUrl = jsonObject.getString("link_cc");

                        Toast.makeText(CheckOutActivity.this, "Mohon tunggu", Toast.LENGTH_SHORT).show();

                        Log.i("link - " ," link-> " + ccUrl);
                        frameBuffer.setVisibility(View.INVISIBLE);


                        Intent intent = new Intent(CheckOutActivity.this, CreditCardActivity.class);
                        intent.putExtra("url", ccUrl);
                        startActivity(intent);
                    }else if(error.equals("true")) {
                        Toast.makeText(CheckOutActivity.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        frameBuffer.setVisibility(View.INVISIBLE);
                    }




                } catch (JSONException e) {
                    e.printStackTrace();
                    frameLoading.setVisibility(View.INVISIBLE);
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Login Error: " + error.getMessage());
                Toast.makeText(CheckOutActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "credit_card");
                params.put("email", VA_email);
                params.put("nama", VA_nama);
                params.put("alamat", VA_alamat);
                params.put("no_telp", VA_telp);
                params.put("payment_type", VA_payType);
                params.put("delivery_type", VA_delType);
                params.put("id_user", VA_idUser);
                params.put("ongkir", VA_ongkir);
                params.put("platform", VA_platform);
                params.put("type_host", VA_typeHost);
                params.put("diskon", VA_diskon);
                params.put("subtotal", VA_subTotal);
                params.put("subtotalfinal", String.valueOf(finalTotal));


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







    private void greenCashCheckout(final String VA_tag, final String VA_email, final String VA_nama, final String VA_alamat
            ,final String VA_telp, final String VA_payType, final String VA_delType
            , final String VA_idUser, final String VA_ongkir, final String VA_platform
            , final String VA_typeHost, final String VA_diskon, final String VA_subTotal, final String VA_subTotalFinal)
    {

        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_NICEPAYCHECKOUT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("TAG", "Login Response: " + response.toString());
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    new AlertDialog.Builder(CheckOutActivity.this)
                            .setTitle("Konfirmasi Belanja")
                            .setMessage(jsonObject.getString("msg"))
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(CheckOutActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .show();


                    frameBuffer.setVisibility(View.INVISIBLE);


                } catch (JSONException e) {
                    e.printStackTrace();
                    frameLoading.setVisibility(View.INVISIBLE);
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Login Error: " + error.getMessage());
                Toast.makeText(CheckOutActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "green_cash");
                params.put("email", VA_email);
                params.put("nama", VA_nama);
                params.put("alamat", VA_alamat);
                params.put("no_telp", VA_telp);
                params.put("payment_type", VA_payType);
                params.put("delivery_type", VA_delType);
                params.put("id_user", VA_idUser);
                params.put("ongkir", VA_ongkir);
                params.put("platform", VA_platform);
                params.put("type_host", VA_typeHost);
                params.put("diskon", VA_diskon);
                params.put("subtotal", VA_subTotal);
                params.put("subtotalfinal", VA_subTotalFinal);

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



    private void readVACheckout(final String VA_tag, final String VA_email, final String VA_nama, final String VA_alamat
            ,final String VA_telp, final String VA_payType, final String VA_delType
            , final String VA_idUser, final String VA_ongkir, final String VA_platform
            , final String VA_typeHost, final String VA_diskon, final String VA_subTotal, final String VA_subTotalFinal)
    {

        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_NICEPAYCHECKOUT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("TAG", "Login Response: " + response.toString());
                try {
                    JSONObject jsonObject = new JSONObject(response);


                    if (jsonObject.has("success")){
                        String success = jsonObject.getString("success");
                        if (success.equals("1")){
                            frameBuffer.setVisibility(View.INVISIBLE);
                            Intent intent = new Intent(CheckOutActivity.this, XenditConfirmationActivity.class);
                            intent.putExtra("nota", jsonObject.getString("nota"));
                            intent.putExtra("amount", jsonObject.getString("amount"));
                            intent.putExtra("virtual", jsonObject.getString("virtual"));
                            intent.putExtra("expired", jsonObject.getString("expired"));
                            intent.putExtra("namabank", chck_namaBank);
                            intent.putExtra("paytype", chck_paymentType);
                            startActivity(intent);
                            finish();

                        }
                    }else if (jsonObject.has("error")){
                        String error = jsonObject.getString("error");
                        if (error.equals("true")){
                            Toast.makeText(CheckOutActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            frameBuffer.setVisibility(View.INVISIBLE);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    frameLoading.setVisibility(View.INVISIBLE);
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Login Error: " + error.getMessage());
                Toast.makeText(CheckOutActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "virtual_account");
                params.put("email", VA_email);
                params.put("nama", VA_nama);
                params.put("alamat", VA_alamat);
                params.put("no_telp", VA_telp);
                params.put("payment_type", VA_payType);
                params.put("delivery_type", VA_delType);
                params.put("id_user", VA_idUser);
                params.put("ongkir", VA_ongkir);
                params.put("platform", VA_platform);
                params.put("type_host", VA_typeHost);
                params.put("diskon", VA_diskon);
                params.put("subtotal", VA_subTotal);
                params.put("subtotalfinal", VA_subTotalFinal);

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




//        btnCheckout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                nama = namaUser.getText().toString();
//                alamat = alamatUser.getText().toString();
//                notelp = notelpUser.getText().toString();
//                if (nama.equalsIgnoreCase("")||alamat.equalsIgnoreCase("")||notelp.equalsIgnoreCase("")){
//                    Toast.makeText(CheckOutActivity.this, "Kolom Alamat dan No Telp tidak boleh kosong", Toast.LENGTH_SHORT).show();
//                }
//                else {
//                    totalOrder = totOrder.getText().toString();
//
//                    if(!paymentType.equalsIgnoreCase("22")){
//
//                        if (paymentType.equalsIgnoreCase("12")){
//                            if (Integer.parseInt(totalOrder) < 50000){
//                                Toast.makeText(CheckOutActivity.this, "Bank BCA Transfer minimal belanja adalah Rp. 50.000", Toast.LENGTH_LONG).show();
//                                Log.i("bayar bca", " " + totalOrder);
//                            }else {
//                                new AlertDialog.Builder(CheckOutActivity.this)
//                                        .setTitle("Konfirmasi Belanja")
//                                        .setMessage("Apakah Anda yakin dengan belanjaan anda?\nTransaksi tidak dapat dibatalkan setelah anda melakukan konfirmasi")
//                                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialogInterface, int i) {
//                                                frameBuffer.setVisibility(View.VISIBLE);
////                                        totalOrder = totOrder.getText().toString();
//
//                                                chck_email = email ;
//                                                chck_nama = nama ;
//                                                chck_alamat = alamat ;
//                                                chck_telp = notelp;
//                                                chck_paymentType = paymentType ;
//                                                chck_deliveryType = deliveryType  ;
//                                                chck_idUser = id_user  ;
//                                                chck_ongkir = ongkir ;
//                                                chck_platform = "1";
//                                                chck_typeHost = "member";
//                                                chck_diskon = diskon;
//                                                chck_subTotal = totalOrder;
//                                                chck_subTotalFinal = totalOrder;
//
//
//                                                Log.i("Post to va :->",
//                                                        "email->" + chck_email +
//                                                                "nama->" + chck_nama +
//                                                                "alamat->" + chck_alamat +
//                                                                "telp->" + chck_telp +
//                                                                "paytype->" + chck_paymentType +
//                                                                "deltype->" + chck_deliveryType +
//                                                                "iduser->" + chck_idUser +
//                                                                "ongkir->" + chck_ongkir +
//                                                                "platform->" + chck_platform +
//                                                                "typehost->" + chck_typeHost +
//                                                                "diskon->" + chck_diskon +
//                                                                "subtotal->" + chck_subTotal +
//                                                                "subfinal->" + chck_subTotalFinal+"namabank"+chck_namaBank
//                                                );
//
//
//                                                Intent intent = new Intent(CheckOutActivity.this, XenditConfirmationActivity.class);
//                                                intent.putExtra("tag", "virtual_account");
//                                                intent.putExtra("email", chck_email);
//                                                intent.putExtra("nama", chck_nama);
//                                                intent.putExtra("alamat", chck_alamat);
//                                                intent.putExtra("notelp", chck_telp);
//                                                intent.putExtra("paytype", chck_paymentType);
//                                                intent.putExtra("deltype", chck_deliveryType);
//                                                intent.putExtra("iduser", chck_idUser);
//                                                intent.putExtra("ongkir", chck_ongkir);
//                                                intent.putExtra("platform", chck_platform);
//                                                intent.putExtra("typehost", chck_typeHost);
//                                                intent.putExtra("diskon", chck_diskon);
//                                                intent.putExtra("subtotal", chck_subTotal);
//                                                intent.putExtra("subtotalfinal", chck_subTotalFinal);
//                                                intent.putExtra("namabank", chck_namaBank);
//
//
//                                                startActivity(intent);
//                                                finish();
//
//                                            }
//                                        })
//                                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialogInterface, int i) {
//                                                finish();
//                                            }
//                                        })
//                                        .show();
//                            }
//                        }
//                        else if (paymentType.equalsIgnoreCase("11")){
////                                Toast.makeText(CheckOutActivity.this, "Process Greencash", Toast.LENGTH_LONG).show();
//                            new AlertDialog.Builder(CheckOutActivity.this)
//                                    .setTitle("Konfirmasi Belanja")
//                                    .setMessage("Apakah Anda yakin dengan belanjaan anda?\nTransaksi tidak dapat dibatalkan setelah anda melakukan konfirmasi")
//                                    .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialogInterface, int i) {
//                                            frameBuffer.setVisibility(View.VISIBLE);
////                                        totalOrder = totOrder.getText().toString();
//
//                                            chck_email = email ;
//                                            chck_nama = nama ;
//                                            chck_alamat = alamat ;
//                                            chck_telp = notelp;
//                                            chck_paymentType = paymentType ;
//                                            chck_deliveryType = deliveryType  ;
//                                            chck_idUser = id_user  ;
//                                            chck_ongkir = ongkir ;
//                                            chck_platform = "1";
//                                            chck_typeHost = "member";
//                                            chck_diskon = diskon;
//                                            chck_subTotal = totalOrder;
//                                            chck_subTotalFinal = totalOrder;
//
//
//                                            Log.i("Post to va :->",
//                                                    "email->" + chck_email +
//                                                            "nama->" + chck_nama +
//                                                            "alamat->" + chck_alamat +
//                                                            "telp->" + chck_telp +
//                                                            "paytype->" + chck_paymentType +
//                                                            "deltype->" + chck_deliveryType +
//                                                            "iduser->" + chck_idUser +
//                                                            "ongkir->" + chck_ongkir +
//                                                            "platform->" + chck_platform +
//                                                            "typehost->" + chck_typeHost +
//                                                            "diskon->" + chck_diskon +
//                                                            "subtotal->" + chck_subTotal +
//                                                            "subfinal->" + chck_subTotalFinal+"namabank"+chck_namaBank
//                                            );
//
//
//                                            Intent intent = new Intent(CheckOutActivity.this, XenditConfirmationActivity.class);
//                                            intent.putExtra("tag", "green_cash");
//                                            intent.putExtra("email", chck_email);
//                                            intent.putExtra("nama", chck_nama);
//                                            intent.putExtra("alamat", chck_alamat);
//                                            intent.putExtra("notelp", chck_telp);
//                                            intent.putExtra("paytype", chck_paymentType);
//                                            intent.putExtra("deltype", chck_deliveryType);
//                                            intent.putExtra("iduser", chck_idUser);
//                                            intent.putExtra("ongkir", chck_ongkir);
//                                            intent.putExtra("platform", chck_platform);
//                                            intent.putExtra("typehost", chck_typeHost);
//                                            intent.putExtra("diskon", chck_diskon);
//                                            intent.putExtra("subtotal", chck_subTotal);
//                                            intent.putExtra("subtotalfinal", chck_subTotalFinal);
//                                            intent.putExtra("namabank", chck_namaBank);
//
//                                            startActivity(intent);
//                                            finish();
//
//                                        }
//                                    })
//                                    .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialogInterface, int i) {
//                                            finish();
//                                        }
//                                    })
//                                    .show();
//                        }
//                        else if (Integer.parseInt(totalOrder) < 30000){
//                            Toast.makeText(CheckOutActivity.this, "Bank Transfer minimal belanja adalah Rp. 30.000", Toast.LENGTH_LONG).show();
//                        }
//                        else{
//                            new AlertDialog.Builder(CheckOutActivity.this)
//                                .setTitle("Konfirmasi Belanja")
//                                .setMessage("Apakah Anda yakin dengan belanjaan anda?\nTransaksi tidak dapat dibatalkan setelah anda melakukan konfirmasi")
//                                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                        frameBuffer.setVisibility(View.VISIBLE);
////                                        totalOrder = totOrder.getText().toString();
//
//                                        chck_email = email ;
//                                        chck_nama = nama ;
//                                        chck_alamat = alamat ;
//                                        chck_telp = notelp;
//                                        chck_paymentType = paymentType ;
//                                        chck_deliveryType = deliveryType  ;
//                                        chck_idUser = id_user  ;
//                                        chck_ongkir = ongkir ;
//                                        chck_platform = "1";
//                                        chck_typeHost = "member";
//                                        chck_diskon = diskon;
//                                        chck_subTotal = totalOrder;
//                                        chck_subTotalFinal = totalOrder;
//
//
//
//
//                                        Log.i("Post to va :->",
//                                                "email->" + chck_email +
//                                                        "nama->" + chck_nama +
//                                                        "alamat->" + chck_alamat +
//                                                        "telp->" + chck_telp +
//                                                        "paytype->" + chck_paymentType +
//                                                        "deltype->" + chck_deliveryType +
//                                                        "iduser->" + chck_idUser +
//                                                        "ongkir->" + chck_ongkir +
//                                                        "platform->" + chck_platform +
//                                                        "typehost->" + chck_typeHost +
//                                                        "diskon->" + chck_diskon +
//                                                        "subtotal->" + chck_subTotal +
//                                                        "subfinal->" + chck_subTotalFinal+"namabank"+chck_namaBank
//                                        );
//
//
//                                        Intent intent = new Intent(CheckOutActivity.this, XenditConfirmationActivity.class);
//                                        intent.putExtra("tag", "virtual_account");
//                                        intent.putExtra("email", chck_email);
//                                        intent.putExtra("nama", chck_nama);
//                                        intent.putExtra("alamat", chck_alamat);
//                                        intent.putExtra("notelp", chck_telp);
//                                        intent.putExtra("paytype", chck_paymentType);
//                                        intent.putExtra("deltype", chck_deliveryType);
//                                        intent.putExtra("iduser", chck_idUser);
//                                        intent.putExtra("ongkir", chck_ongkir);
//                                        intent.putExtra("platform", chck_platform);
//                                        intent.putExtra("typehost", chck_typeHost);
//                                        intent.putExtra("diskon", chck_diskon);
//                                        intent.putExtra("subtotal", chck_subTotal);
//                                        intent.putExtra("subtotalfinal", chck_subTotalFinal);
//                                        intent.putExtra("namabank", chck_namaBank);
//
//                                        startActivity(intent);
//                                        finish();
//
////                                        confirmCheckout();
////                                        toXendit(id_user, email, nama, alamat, notelp, paymentType, deliveryType, totalOrder, kuponPublic, String.valueOf(Integer.parseInt(ongkir) - Integer.parseInt(diskon_ongkir)), preorder, diskon);
//                                    }
//                                })
//                                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                        finish();
//                                    }
//                                })
//                                .show();
//                        }
//                    }
//                    else if (Integer.parseInt(totalOrder) < 100000){
//                        Toast.makeText(CheckOutActivity.this, "Pembayaran Kartu Kredit minimal belanja adalah Rp. 100.000", Toast.LENGTH_LONG).show();
//                    }
//                    else{
//
//                        new AlertDialog.Builder(CheckOutActivity.this)
//                                .setTitle("Konfirmasi Belanja")
//                                .setMessage("Apakah Anda yakin dengan belanjaan anda?\nTransaksi tidak dapat dibatalkan setelah anda melakukan konfirmasi")
//                                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                        frameBuffer.setVisibility(View.VISIBLE);
////                                        totalOrder = totOrder.getText().toString();
////                                        int calc = Integer.parseInt(totalOrder)+Integer.parseInt(ongkir);
//
//                                        chck_email = email ;
//                                        chck_nama = nama ;
//                                        chck_alamat = alamat ;
//                                        chck_telp = notelp;
//                                        chck_paymentType = paymentType ;
//                                        chck_deliveryType = deliveryType  ;
//                                        chck_idUser = id_user  ;
//                                        chck_ongkir = ongkir ;
//                                        chck_platform = "1";
//                                        chck_typeHost = "member";
//                                        chck_diskon = diskon;
//                                        chck_subTotal = totalOrder;
//                                        chck_subTotalFinal = totalOrder;
//
//
//
//
//                                        Log.i("Post to cc :->",
//                                                "email->" + chck_email +
//                                                        "nama->" + chck_nama +
//                                                        "alamat->" + chck_alamat +
//                                                        "telp->" + chck_telp +
//                                                        "paytype->" + chck_paymentType +
//                                                        "deltype->" + chck_deliveryType +
//                                                        "iduser->" + chck_idUser +
//                                                        "ongkir->" + chck_ongkir +
//                                                        "platform->" + chck_platform +
//                                                        "typehost->" + chck_typeHost +
//                                                        "diskon->" + chck_diskon +
//                                                        "subtotal->" + chck_subTotal +
//                                                        "subfinal->" + chck_subTotalFinal+"namabank"+chck_namaBank
//                                        );
//
//
//                                        Intent intent = new Intent(CheckOutActivity.this, CreditCardActivity.class);
//                                        intent.putExtra("tag", "credit_card");
//                                        intent.putExtra("email", chck_email);
//                                        intent.putExtra("nama", chck_nama);
//                                        intent.putExtra("alamat", chck_alamat);
//                                        intent.putExtra("notelp", chck_telp);
//                                        intent.putExtra("paytype", chck_paymentType);
//                                        intent.putExtra("deltype", chck_deliveryType);
//                                        intent.putExtra("iduser", chck_idUser);
////                                        if (chck_ongkir.equalsIgnoreCase("0")){
//                                            c = Integer.parseInt(totalOrder) - Integer.parseInt(ongkir);
////                                        }
//                                        intent.putExtra("ongkir", chck_ongkir);
//                                        intent.putExtra("platform", chck_platform);
//                                        intent.putExtra("typehost", chck_typeHost);
//                                        intent.putExtra("diskon", chck_diskon);
//                                        intent.putExtra("subtotal", String.valueOf(c));
//                                        intent.putExtra("subtotalfinal", String.valueOf(c));
//                                        intent.putExtra("namabank", chck_namaBank);
//
//                                        startActivity(intent);
//                                        finish();
//
////                                        confirmCheckout();
////                                        toXendit(id_user, email, nama, alamat, notelp, paymentType, deliveryType, totalOrder, kuponPublic, String.valueOf(Integer.parseInt(ongkir) - Integer.parseInt(diskon_ongkir)), preorder, diskon);
//                                    }
//                                })
//                                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                        finish();
//                                    }
//                                })
//                                .show();
//
////                        finish();
//
//
//
//
////
////                        AlertDialog.Builder alert = new AlertDialog.Builder(CheckOutActivity.this);
////                        AlertDialog dialog = alert.create();
////
////                        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
////                        dialog.setTitle("Pembayaran Via Kartu Kredit");
////
////
////                        WebView wv = new WebView(CheckOutActivity.this);
////                        wv.getSettings().setJavaScriptEnabled(true);
////                        wv.getSettings().setPluginState(WebSettings.PluginState.ON);
////                        wv.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
////                        wv.clearCache(true);
////                        wv.clearHistory();
////                        wv.loadUrl(ccUrl);
////                        wv.setWebChromeClient(new WebChromeClient());
////                        wv.setWebViewClient(new WebViewClient() {
////                            @Override
////                            public boolean shouldOverrideUrlLoading(WebView view, String url) {
////                                view.loadUrl(url);
////                                return true;
////                            }
////                        });
////
////                        dialog.setView(wv);
////                        dialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
////                            @Override
////                            public void onClick(DialogInterface dialog, int id) {
////                                dialog.dismiss();
////                            }
////                        });
//
////                        dialog.show();
//
////                        String pass = myWebView.getUrl();
////
////                        if(pass.equals("https://kecipir.com/KecipirThanks.php")){
////                            myWebView.goBack();
////                        }
//
//                    }
//                }
//            }
//        });




//    private void checkOut(final String id_user, final String email, final String nama, final String alamat,
//                          final String notelp, final String paymentType, final String deliveryType, final String totalOrder,
//                          final String kupon, final String ongkir, final String preorder, final String diskon) {
//
//        String tag_string_req = "req_login";
//        StringRequest strReq = new StringRequest(Request.Method.POST,
//                AppConfig.URL_LOGIN, new Response.Listener<String>() {
//
//            @Override
//            public void onResponse(String response) {
//                Log.d("TAG", "View chckout Response: " + response.toString());
//                try {
//                    if (response.equalsIgnoreCase("")) {
//                        frameBuffer.setVisibility(View.INVISIBLE);
//                    }
//                    else {
//                        JSONObject jsonObject = new JSONObject(response);
//                        boolean error = jsonObject.getBoolean("error");
//                        if (!error){
//                            frameBuffer.setVisibility(View.INVISIBLE);
//                            mTracker.send(new HitBuilders.EventBuilder()
//                                    .setCategory("Action")
//                                    .setAction("Checkout")
//                                    .build());
//                            checkoutFinish();
//                        }
//                        else {
//                            frameBuffer.setVisibility(View.INVISIBLE);
//                            Toast.makeText(CheckOutActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    retry();
//                }
//            }
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("TAG", "Checkout Error: " + error.getMessage());
//                NetworkResponse networkResponse = error.networkResponse;
//                String errorMessage = "Unknown error";
//                if (networkResponse == null) {
//                    if (error.getClass().equals(TimeoutError.class)) {
//                        errorMessage = "Request timeout";
//                        finish();
//                    } else if (error.getClass().equals(NoConnectionError.class)) {
//                        errorMessage = "Failed to connect server";
//                    }
//                } else {
//                    String result = new String(networkResponse.data);
//                    try {
//                        JSONObject response = new JSONObject(result);
//                        String status = response.getString("status");
//                        String message = response.getString("message");
//
//                        Log.e("Error Status", status);
//                        Log.e("Error Message", message);
//
//                        if (networkResponse.statusCode == 404) {
//                            errorMessage = "Resource not found";
//                        } else if (networkResponse.statusCode == 401) {
//                            errorMessage = message+" Please retry again";
//                        } else if (networkResponse.statusCode == 400) {
//                            errorMessage = message+ " error 400";
//                        } else if (networkResponse.statusCode == 500) {
//                            errorMessage = message+" Something is getting wrong";
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//                Log.i("Error", errorMessage);
//                error.printStackTrace();
//                frameBuffer.setVisibility(View.INVISIBLE);
////                retry();
//            }
//        }) {
//
//            @Override
//            protected Map<String, String> getParams() {
//                // Posting parameters to login url
//                Map<String, String> params = new HashMap<String, String>();
//
//                params.put("tag", "checkout");
//                params.put("id_user", id_user);
//                params.put("email", email);
//                params.put("nama", nama);
//                params.put("alamat", alamat);
//                params.put("no_telp", notelp);
//                params.put("payment_type", paymentType);
//                params.put("delivery_type", deliveryType);
//                params.put("subtotal", totalOrder);
//                params.put("kupon", kupon);
//                params.put("ongkir", ongkir);
//                params.put("preorder", preorder);
//                params.put("diskon", diskon);
//
//                return params;
//            }
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> params = new HashMap<String, String>();
//                String creds = String.format("%s:%s","green","web-indonesia");
//                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
//                params.put("Authorization", auth);
//                return params;
//            }
//
//        };
//
//
//        strReq.setRetryPolicy(new DefaultRetryPolicy(AppConfig.TIMEOUT_NETWORK, AppConfig.RETRY_NETWORK, AppConfig.MULTI_NETWORK));
//        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
//    }



//    private void checkoutFinish() {
//        new AlertDialog.Builder(this)
//                .setTitle("Transaksi Berhasil")
//                .setMessage("Terima Kasih telah berbelanja di Kecipir")
//                .setPositiveButton("Lihat Pembelian", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
////                        Intent intent = new Intent(CheckOutActivity.this, HistoryActivity.class );
////                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////                        startActivity(intent);
//                        Toast.makeText(CheckOutActivity.this, "message", Toast.LENGTH_SHORT).show();
//                        finish();
//                    }
//                })
////                .setNegativeButton("Lanjut Belanja", new DialogInterface.OnClickListener() {
////                    @Override
////                    public void onClick(DialogInterface dialogInterface, int i) {
////                        Intent intent = new Intent(CheckOutActivity.this, MainActivity.class );
////                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////                        startActivity(intent);
////                        finish();
////                    }
////                })
//                .show();
//    }





//    private void toXendit(final String id_user, final String email, final String nama, final String alamat,
//                          final String notelp, final String paymentType, final String deliveryType, final String totalOrder,
//                          final String kupon, final String ongkir, final String preorder, final String diskon) {
//
//        String tag_string_req = "req_login";
//        StringRequest strReq = new StringRequest(Request.Method.POST,
//                AppConfig.URL_XENDITCHECKOUT, new Response.Listener<String>() {
//
//            @Override
//            public void onResponse(String response) {
//                Log.d("TAG", "View chckout Response: " + response.toString());
//                try {
//                    if (response.equalsIgnoreCase("")) {
//                        frameBuffer.setVisibility(View.INVISIBLE);
//                    }
//                    else {
//
//                        JSONObject jsonObject = new JSONObject(response);
//                        boolean error = jsonObject.getBoolean("error");
//
//                        if (!error){
//                            frameBuffer.setVisibility(View.INVISIBLE);
//                            mTracker.send(new HitBuilders.EventBuilder()
//                                    .setCategory("Action")
//                                    .setAction("Xendit Checkout")
//                                    .build());
//
//                            Intent intent = new Intent(CheckOutActivity.this, XenditConfirmationActivity.class);
//                            intent.putExtra("action", "checkout");
//                            intent.putExtra("no_nota", jsonObject.getString("no_nota"));
//                            startActivity(intent);
//                        }
//                        else {
//                            frameBuffer.setVisibility(View.INVISIBLE);
//                            Toast.makeText(CheckOutActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    retry();
//                }
//            }
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("TAG", "Checkout Error: " + error.getMessage());
//                NetworkResponse networkResponse = error.networkResponse;
//                String errorMessage = "Unknown error";
//                if (networkResponse == null) {
//                    if (error.getClass().equals(TimeoutError.class)) {
//                        errorMessage = "Request timeout";
//                        finish();
//                    } else if (error.getClass().equals(NoConnectionError.class)) {
//                        errorMessage = "Failed to connect server";
//                    }
//                } else {
//                    String result = new String(networkResponse.data);
//                    try {
//                        JSONObject response = new JSONObject(result);
//                        String status = response.getString("status");
//                        String message = response.getString("message");
//
//                        Log.e("Error Status", status);
//                        Log.e("Error Message", message);
//
//                        if (networkResponse.statusCode == 404) {
//                            errorMessage = "Resource not found";
//                        } else if (networkResponse.statusCode == 401) {
//                            errorMessage = message+" Please retry again";
//                        } else if (networkResponse.statusCode == 400) {
//                            errorMessage = message+ " error 400";
//                        } else if (networkResponse.statusCode == 500) {
//                            errorMessage = message+" Something is getting wrong";
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//                Log.i("Error", errorMessage);
//                error.printStackTrace();
//                frameBuffer.setVisibility(View.INVISIBLE);
////                retry();
//            }
//        }) {
//
//            @Override
//            protected Map<String, String> getParams() {
//                // Posting parameters to login url
//                Map<String, String> params = new HashMap<String, String>();
//
//                params.put("tag", "submit");
//                params.put("id_user", id_user);
//                params.put("email", email);
//                params.put("nama", nama);
//                params.put("alamat", alamat);
//                params.put("no_telp", notelp);
//                params.put("payment_type", paymentType);
//                params.put("delivery_type", deliveryType);
//                params.put("subtotal", totalOrder);
//                params.put("kupon", kupon);
//                params.put("ongkir", ongkir);
//                params.put("preorder", preorder);
//                params.put("diskon", diskon);
//                params.put("platform", "1");
//
//                return params;
//            }
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> params = new HashMap<String, String>();
//                String creds = String.format("%s:%s","green","web-indonesia");
//                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
//                params.put("Authorization", auth);
//                return params;
//            }
//
//        };
//
//        strReq.setRetryPolicy(new DefaultRetryPolicy(AppConfig.TIMEOUT_NETWORK, AppConfig.RETRY_NETWORK, AppConfig.MULTI_NETWORK));
//        // Adding request to request queue
//        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
//    }





    private void sendScreenName() {
        String name = "Checkout";

        // [START screen_view_hit]
        Log.i("Checkout", "Screen name: " + name);
        mTracker.setScreenName("Screen" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // [END screen_view_hit]
    }
}
