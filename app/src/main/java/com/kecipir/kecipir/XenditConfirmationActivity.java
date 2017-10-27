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
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
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
    TextView txtTotalHarga;
    TextView txtKodeUnik;
    TextView txtTotalPembayaran;
    TextView txtBankTrans;
    TextView txtExpiry;
    Button btnHistory, btnStore;

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

        final Bundle b = getIntent().getExtras();

        Log.d("username", email+" - "+idUser);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_detail);

        txtTotalHarga = (TextView) findViewById(R.id.txt_total_harga);
        txtPaymentDesc = (TextView) findViewById(R.id.txt_paymentdesc);
        txtKodeUnik = (TextView) findViewById(R.id.txt_kode_unique);
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

        SpannableString ss = new SpannableString(b.getString("no_nota"));
        ss.setSpan(new StyleSpan(Typeface.BOLD), 0,ss.length(), 0);

        txtPaymentDesc.setText("Transaksi No. "+ss+" yang perlu dibayarkan sebesar");

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);

        readXenditCheckout(idUser, email, b.getString("no_nota"));

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
    }

    private void readXenditCheckout(final String id_user, final String email, final String no_nota) {

        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_XENDITCHECKOUT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("TAG", "Login Response: " + response.toString());
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");

                    txtTotalHarga.setText("Total Belanja : Rp. "+jsonObject.getString("total_belanja"));
                    txtKodeUnik.setText("Nomor unik : Rp. "+jsonObject.getString("identity_amount"));
                    txtTotalPembayaran.setText("Jumlah Transfer : Rp. "+jsonObject.getString("transfer_amount"));
                    txtBankTrans.setText(jsonObject.getString("bank_desc"));
                    txtExpiry.setText(jsonObject.getString("expiry_date"));
                    diskon = jsonObject.getString("diskon");
                    ongkir = jsonObject.getString("ongkir");
                    data = new ArrayList<>();
                    if(!error){
                        JSONArray jArr = jsonObject.getJSONArray("wishlist");
                        int length = jArr.length();
                        cart = new ShoppingCart();

                        for (int i = 0 ; i <length ;i++){
                            JSONObject jObj = (JSONObject) jArr.get(i);
                            id_wishlist = jObj.getString("id_wishlist");
                            id_barang = jObj.getString("id_barang_panen");
                            tgl_panen = jObj.getString("tgl_panen");
                            nama_petani = jObj.getString("nama_petani");
                            quantity = jObj.getString("qty");
                            foto = AppConfig.IMAGE_LINK+ "" + jObj.getString("foto");
                            grade = jObj.getString("grade");
                            satuan = jObj.getString("satuan");
                            nama_barang = jObj.getString("nama_barang");
                            harga_jual = jObj.getString("harga_jual");
                            subtotal = jObj.getString("subtotal");
                            subtotalrp = jObj.getString("subtotalrp");
                            harga_jualrp = jObj.getString("harga_jualrp");

                            int jumlah_harga = Integer.parseInt(harga_jual)*Integer.parseInt(quantity);
                            cart = new ShoppingCart(id_wishlist, id_barang, tgl_panen, nama_petani, quantity, foto, grade, satuan, nama_barang, harga_jual,subtotal, harga_jualrp, jumlah_harga+"", subtotalrp);
                            data.add(cart);
                            intTotal = jumlah_harga + intTotal;
                        }


                        adapter = new XenditCartAdapter(XenditConfirmationActivity.this, data);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(XenditConfirmationActivity.this));

                        frameLoading.setVisibility(View.INVISIBLE);
                    }
                    else {
                        frameLoading.setVisibility(View.INVISIBLE);
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
                Toast.makeText(XenditConfirmationActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "view");
                params.put("id_user", id_user);
                params.put("email", email);
                params.put("no_nota", no_nota);

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

    private void setXenditCheckout(final String id_user, final String email, final String no_nota,
                                   final String subtotal, final String diskon, final String ongkir) {

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
                                    .setAction("Checkout")
                                    .build());
                            new AlertDialog.Builder(XenditConfirmationActivity.this)
                                    .setTitle("Transaksi Berhasil")
                                    .setMessage("Terima Kasih telah berbelanja di Kecipir")
                                    .setCancelable(false)
                                    .setPositiveButton("Lihat Riwayat Belanja", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent = new Intent(XenditConfirmationActivity.this, HistoryActivity.class );
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                                    .setNegativeButton("Lanjut Belanja", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent = new Intent(XenditConfirmationActivity.this, MainActivity.class );
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                                    .show();
                        }
                        else {
                            frameBuffer.setVisibility(View.INVISIBLE);
                            Toast.makeText(XenditConfirmationActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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

                params.put("tag", "confirm");
                params.put("id_user", id_user);
                params.put("email", email);
                params.put("subtotal", subtotal);
                params.put("diskon", diskon);
                params.put("no_nota", no_nota);
                params.put("ongkir", ongkir);

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
}
