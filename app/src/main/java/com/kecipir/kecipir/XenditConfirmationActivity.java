package com.kecipir.kecipir;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.kecipir.kecipir.adapter.ShoppingCartAdapter;
import com.kecipir.kecipir.adapter.XenditCartAdapter;
import com.kecipir.kecipir.data.AppConfig;
import com.kecipir.kecipir.data.AppController;
import com.kecipir.kecipir.data.ShoppingCart;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XenditConfirmationActivity extends AppCompatActivity {


    Toolbar toolbar;

    List<ShoppingCart> data;
    RelativeLayout frameLoading, frameBuffer;
    ShoppingCart cart;
    XenditCartAdapter adapter;

    String tag,chck_idUser,chck_namaBank, chck_nama, chck_email, chck_alamat, chck_telp, chck_paymentType, chck_deliveryType, chck_ongkir, chck_platform, chck_typeHost, chck_diskon, chck_subTotal, chck_subTotalFinal;

    String id_wishlist;
    String id_barang;
    String tgl_panen;
    String nama_petani;
    String quantity;
    String foto;
    String grade;
    String satuan;
    String nama_barang;
    String harga_jual;
    String subtotal;
    String harga_jualrp;
    String subtotalrp;
    String total;
    String diskon = "0";
    String ongkir = "0" +
            "";
    int intTotal =0;

    SessionManager sessionManager;
    HashMap<String, String> user;

    RecyclerView recyclerView;
    TextView txtPaymentDesc;
//    TextView txtTotalHarga;
//    TextView txtKodeUnik;
    TextView txtTotalPembayaran;
    TextView txtBankTrans;
    TextView txtExpiry;
    TextView namaBank;
    LinearLayout layout1,layout2,layout3;
    Button btnHistory, btnStore;


    ImageView img1,img2,img3,img4;

    private CoordinatorLayout coordinatorLayout;
    private Tracker mTracker;

    String action, url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xendit_confirmation);
        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(this);
        user = sessionManager.getUser();
        final String email = user.get("email");
        final String idUser = user.get("uid");

        namaBank = (TextView) findViewById(R.id.txt_namabank);

//        layout1.findViewById(R.id.layout_exp);
//        layout2.findViewById(R.id.layout_harga_pembayaran);
//        layout3.findViewById(R.id.layout_bank);

        Log.d("username", email+" - "+idUser);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_detail);

//        txtTotalHarga = (TextView) findViewById(R.id.txt_total_harga);
        txtPaymentDesc = (TextView) findViewById(R.id.txt_paymentdesc);
//        txtKodeUnik = (TextView) findViewById(R.id.txt_kode_unique);
        txtTotalPembayaran = (TextView) findViewById(R.id.txt_total_pembayaran);
        txtBankTrans = (TextView) findViewById(R.id.txt_transfer_bank);
        txtExpiry = (TextView) findViewById(R.id.txt_expiry);

        frameLoading = (RelativeLayout) findViewById(R.id.frame_loading);
        frameBuffer = (RelativeLayout) findViewById(R.id.frame_buffer);
        btnHistory = (Button) findViewById(R.id.btn_history);
        btnStore = (Button) findViewById(R.id.btn_store);

        AppController appController = (AppController) getApplication();
        mTracker = appController.getDefaultTracker();
        sendScreenName();








        JSONObject Pem1 = new JSONObject();
        try {
            Pem1.put("msg", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }






        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);

//        readXenditCheckout(idUser, email, b.getString("no_nota"));

        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                frameBuffer.setVisibility(View.VISIBLE);
//                setXenditCheckout(idUser, email, b.getString("no_nota"), String.valueOf(intTotal), diskon, ongkir);
                Intent intent = new Intent(XenditConfirmationActivity.this, HistoryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        btnStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(XenditConfirmationActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        final Bundle b = getIntent().getExtras();

        chck_namaBank = b.getString("namabank");
        chck_paymentType = b.getString("paytype");

        SpannableString ss = new SpannableString(b.getString("nota"));
        ss.setSpan(new StyleSpan(Typeface.BOLD), 0,ss.length(), 0);
        txtPaymentDesc.setText("Transaksi No. "+ss+" yang perlu dibayarkan sebesar");
        txtPaymentDesc.setGravity(Gravity.CENTER);
        txtTotalPembayaran.setText("Jumlah Transfer : Rp. "+b.getString("amount"));
        txtBankTrans.setText(b.getString("virtual"));
        txtExpiry.setText(b.getString("expired"));
        frameBuffer.setVisibility(View.INVISIBLE);


//
//
//        Log.i("Post confirm :->",
//                " email->" + chck_email +
//                        " nama->" + chck_nama +
//                        " alamat->" + chck_alamat +
//                        " telp->" + chck_telp +
//                        " paytype->" + chck_paymentType +
//                        " deltype->" + chck_deliveryType +
//                        " iduser->" + chck_idUser +
//                        " ongkir->" + chck_ongkir +
//                        " platform->" + chck_platform +
//                        " typehost->" + chck_typeHost +
//                        " diskon->" + chck_diskon +
//                        " subtotal->" + chck_subTotal +
//                        " subfinal->" + chck_subTotalFinal+
//                        " namabank->" + chck_namaBank
//
//        );
//        frameBuffer.setVisibility(View.VISIBLE);
//
//        if (tag.equalsIgnoreCase("virtual_account")){
//            readVACheckout(tag, chck_email,chck_nama,chck_alamat,chck_telp,chck_paymentType,chck_deliveryType,chck_idUser,chck_ongkir
//                    , chck_platform, chck_typeHost, chck_diskon, chck_subTotal, chck_subTotalFinal);
//
//        }else{
////            layout1.setVisibility(View.GONE);
////            layout2.setVisibility(View.GONE);
////            layout3.setVisibility(View.GONE);
//
//            greenCashCheckout(tag, chck_email,chck_nama,chck_alamat,chck_telp,chck_paymentType,chck_deliveryType,chck_idUser,chck_ongkir
//                    , chck_platform, chck_typeHost, chck_diskon, chck_subTotal, chck_subTotalFinal);
//
//        }




        img1 = (ImageView) findViewById(R.id.img_bank1);
        img2 = (ImageView) findViewById(R.id.img_bank2);
        img3 = (ImageView) findViewById(R.id.img_bank3);
        img4 = (ImageView) findViewById(R.id.img_bank4);

        namaBank.setText(chck_namaBank);

        final int res1,res2,res3;

        if (chck_paymentType.equals("12")){
            img1.setImageDrawable(getResources().getDrawable(R.drawable.img_bca_1));
            img2.setImageDrawable(getResources().getDrawable(R.drawable.img_bca_2));
            img3.setImageDrawable(getResources().getDrawable(R.drawable.img_bca_3));
            namaBank.setText("Bank BCA");
        }
        else if(chck_paymentType.equals("13")){
            img1.setImageDrawable(getResources().getDrawable(R.drawable.img_mandiri_1));
            img2.setImageDrawable(getResources().getDrawable(R.drawable.img_mandiri_2));
            img3.setImageDrawable(getResources().getDrawable(R.drawable.img_mandiri_3));
            namaBank.setText("Bank Mandiri");
        }else if(chck_paymentType.equals("14")){
            img1.setImageDrawable(getResources().getDrawable(R.drawable.img_bni_1));
            img2.setImageDrawable(getResources().getDrawable(R.drawable.img_bni_2));
            img3.setImageDrawable(getResources().getDrawable(R.drawable.img_bni_3));
            img4.setImageDrawable(getResources().getDrawable(R.drawable.img_bni_4));
            namaBank.setText("Bank BNI");
        }else if(chck_paymentType.equals("15")){
            img1.setImageDrawable(getResources().getDrawable(R.drawable.img_bri_1));
            img2.setImageDrawable(getResources().getDrawable(R.drawable.img_bri_2));
            img3.setImageDrawable(getResources().getDrawable(R.drawable.img_bri_3));
            namaBank.setText("Bank BRI");
        }else if(chck_paymentType.equals("16")){
            img1.setImageDrawable(getResources().getDrawable(R.drawable.img_cimb_1));
            img2.setImageDrawable(getResources().getDrawable(R.drawable.img_cimb_2));
            img3.setImageDrawable(getResources().getDrawable(R.drawable.img_cimb_3));
            namaBank.setText("Bank CIMB NIAGA");
        }else if(chck_paymentType.equals("17")){
            img1.setImageDrawable(getResources().getDrawable(R.drawable.img_permata_1));
            img2.setImageDrawable(getResources().getDrawable(R.drawable.img_permata_2));
            img3.setImageDrawable(getResources().getDrawable(R.drawable.img_permata_3));
            namaBank.setText("Bank Permata");
        }else if(chck_paymentType.equals("18")){
            img1.setImageDrawable(getResources().getDrawable(R.drawable.bii));
            img2.setImageDrawable(getResources().getDrawable(R.drawable.bii2));
            img3.setImageDrawable(getResources().getDrawable(R.drawable.bii3));
            namaBank.setText("Bank BII Maybank");
        }else if(chck_paymentType.equals("19")){
            img1.setImageDrawable(getResources().getDrawable(R.drawable.img_danamon_1));
            img2.setImageDrawable(getResources().getDrawable(R.drawable.img_danamon_2));
            namaBank.setText("Bank Danamon");
        }else if(chck_paymentType.equals("20")){
            img1.setImageDrawable(getResources().getDrawable(R.drawable.img_hana_1));
            img2.setImageDrawable(getResources().getDrawable(R.drawable.img_hana_2));
            namaBank.setText("Bank KEB Hana Bank");
        }else if(chck_paymentType.equals("21")){
            img1.setImageDrawable(getResources().getDrawable(R.drawable.img_hana_1));
            img2.setImageDrawable(getResources().getDrawable(R.drawable.img_hana_2));
            namaBank.setText("ATM BERSAMA");
        }
    }

//
//    private void greenCashCheckout(final String VA_tag, final String VA_email, final String VA_nama, final String VA_alamat
//            ,final String VA_telp, final String VA_payType, final String VA_delType
//            , final String VA_idUser, final String VA_ongkir, final String VA_platform
//            , final String VA_typeHost, final String VA_diskon, final String VA_subTotal, final String VA_subTotalFinal)
//    {
//
//        String tag_string_req = "req_login";
//        StringRequest strReq = new StringRequest(Request.Method.POST,
//                AppConfig.URL_NICEPAYCHECKOUT, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.d("TAG", "Login Response: " + response.toString());
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//
//                    new AlertDialog.Builder(XenditConfirmationActivity.this)
//                            .setTitle("Konfirmasi Belanja")
//                            .setMessage(jsonObject.getString("msg"))
//                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    Intent intent = new Intent(XenditConfirmationActivity.this, MainActivity.class);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                    startActivity(intent);
//                                    finish();
//                                }
//                            })
//                            .show();
//
//
//                    frameBuffer.setVisibility(View.INVISIBLE);
//
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    frameLoading.setVisibility(View.INVISIBLE);
//                }
//
//            }
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("TAG", "Login Error: " + error.getMessage());
//                Toast.makeText(XenditConfirmationActivity.this,
//                        error.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        }) {
//
//            @Override
//            protected Map<String, String> getParams() {
//                // Posting parameters to login url
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("tag", "green_cash");
//                params.put("email", VA_email);
//                params.put("nama", VA_nama);
//                params.put("alamat", VA_alamat);
//                params.put("no_telp", VA_telp);
//                params.put("payment_type", VA_payType);
//                params.put("delivery_type", VA_delType);
//                params.put("id_user", VA_idUser);
//                params.put("ongkir", VA_ongkir);
//                params.put("platform", VA_platform);
//                params.put("type_host", VA_typeHost);
//                params.put("diskon", VA_diskon);
//                params.put("subtotal", VA_subTotal);
//                params.put("subtotalfinal", VA_subTotalFinal);
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
//
//
//
//    private void readVACheckout(final String VA_tag, final String VA_email, final String VA_nama, final String VA_alamat
//                                    ,final String VA_telp, final String VA_payType, final String VA_delType
//                                    , final String VA_idUser, final String VA_ongkir, final String VA_platform
//                                    , final String VA_typeHost, final String VA_diskon, final String VA_subTotal, final String VA_subTotalFinal)
//    {
//
//        String tag_string_req = "req_login";
//        StringRequest strReq = new StringRequest(Request.Method.POST,
//                AppConfig.URL_NICEPAYCHECKOUT, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.d("TAG", "Login Response: " + response.toString());
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//
//
//                        SpannableString ss = new SpannableString(jsonObject.getString("nota"));
//                        ss.setSpan(new StyleSpan(Typeface.BOLD), 0,ss.length(), 0);
//                        txtPaymentDesc.setText("Transaksi No. "+ss+" yang perlu dibayarkan sebesar");
//                        txtTotalPembayaran.setText("Jumlah Transfer : Rp. "+jsonObject.getString("amount"));
//                        txtBankTrans.setText(jsonObject.getString("virtual"));
//                        txtExpiry.setText(jsonObject.getString("expired"));
//                        frameBuffer.setVisibility(View.INVISIBLE);
//
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    frameLoading.setVisibility(View.INVISIBLE);
//                }
//
//            }
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("TAG", "Login Error: " + error.getMessage());
//                Toast.makeText(XenditConfirmationActivity.this,
//                        error.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        }) {
//
//            @Override
//            protected Map<String, String> getParams() {
//                // Posting parameters to login url
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("tag", "virtual_account");
//                params.put("email", VA_email);
//                params.put("nama", VA_nama);
//                params.put("alamat", VA_alamat);
//                params.put("no_telp", VA_telp);
//                params.put("payment_type", VA_payType);
//                params.put("delivery_type", VA_delType);
//                params.put("id_user", VA_idUser);
//                params.put("ongkir", VA_ongkir);
//                params.put("platform", VA_platform);
//                params.put("type_host", VA_typeHost);
//                params.put("diskon", VA_diskon);
//                params.put("subtotal", VA_subTotal);
//                params.put("subtotalfinal", VA_subTotalFinal);
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
//


    private void sendScreenName() {
        String name = "Shopping Cart";

        // [START screen_view_hit]
        Log.i("ShoopingCartActivity", "Screen name: " + name);
        mTracker.setScreenName("Screen" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // [END screen_view_hit]
    }

    public void onResume(){
        super.onResume();
        user = sessionManager.getUser();
        String email = user.get("email");
        String idUser = user.get("uid");
    }

//    private void setXenditCheckout(final String id_user, final String email, final String no_nota,
//                                   final String subtotal, final String diskon, final String ongkir) {
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
//                                    .setAction("Checkout")
//                                    .build());
//                            new AlertDialog.Builder(XenditConfirmationActivity.this)
//                                    .setTitle("Transaksi Berhasil")
//                                    .setMessage("Terima Kasih telah berbelanja di Kecipir")
//                                    .setCancelable(false)
//                                    .setPositiveButton("Lihat Riwayat Belanja", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialogInterface, int i) {
//                                            Intent intent = new Intent(XenditConfirmationActivity.this, HistoryActivity.class );
//                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                            startActivity(intent);
//                                            finish();
//                                        }
//                                    })
//                                    .setNegativeButton("Lanjut Belanja", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialogInterface, int i) {
//                                            Intent intent = new Intent(XenditConfirmationActivity.this, MainActivity.class );
//                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                            startActivity(intent);
//                                            finish();
//                                        }
//                                    })
//                                    .show();
//                        }
//                        else {
//                            frameBuffer.setVisibility(View.INVISIBLE);
//                            Toast.makeText(XenditConfirmationActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
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
//                params.put("tag", "confirm");
//                params.put("id_user", id_user);
//                params.put("email", email);
//                params.put("subtotal", subtotal);
//                params.put("diskon", diskon);
//                params.put("no_nota", no_nota);
//                params.put("ongkir", ongkir);
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
}
