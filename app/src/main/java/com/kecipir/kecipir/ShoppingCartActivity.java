package com.kecipir.kecipir;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.kecipir.kecipir.adapter.ShoppingCartAdapter;
import com.kecipir.kecipir.data.AppConfig;
import com.kecipir.kecipir.data.AppController;
import com.kecipir.kecipir.data.ClickListener;
import com.kecipir.kecipir.data.ShoppingCart;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

public class ShoppingCartActivity extends AppCompatActivity implements ClickListener{

    Toolbar toolbar;

    List<ShoppingCart> data;
    RelativeLayout frameLoading, frameBuffer;
    ShoppingCart cart;
    ShoppingCartAdapter adapter;

    String id_wishlist;
    String id_barang;
    String tgl_panen;
    String nama_petani;
    String quantity;
    String quantity_awal;
    String quantity_final;
    String foto;
    String grade;
    String satuan;
    String nama_barang;
    String harga_jual;
    String subtotal;
    String harga_promo;
    String harga_promorp;
    String harga_jualrp;
    String subtotalrp;

    String kc_harga_asli;
    String kc_harga_promo;

    String hargaTotal2;

    SessionManager sessionManager;
    HashMap<String, String> user;

    RecyclerView recyclerView;
    TextView totalOrder;
    TextView hargaTotal;
    TextView txtNamaHost;
    TextView txtAlamatHost;
    TextView txthargabarangawal_cart;
    TextView hargaTotalAwal;
    Button btnCheckout;

    String hargaFix;


    private CoordinatorLayout coordinatorLayout;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(this);
        user = sessionManager.getUser();
        String email = user.get("email");
        String idUser = user.get("uid");

        Log.d("username", email+" - "+idUser);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_cart);

        txtNamaHost = (TextView) findViewById(R.id.nama_host);
        txtAlamatHost = (TextView) findViewById(R.id.alamat_host);
        hargaTotal = (TextView) findViewById(R.id.harga_total);
        hargaTotalAwal = (TextView) findViewById(R.id.harga_total_awal);
        frameLoading = (RelativeLayout) findViewById(R.id.frame_loading);
        frameBuffer = (RelativeLayout) findViewById(R.id.frame_buffer);
        btnCheckout = (Button) findViewById(R.id.btn_check_out);


        hargaTotalAwal.setVisibility(View.INVISIBLE);


        AppController appController = (AppController) getApplication();
        mTracker = appController.getDefaultTracker();
        sendScreenName();

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.isEmpty()){
                    Toast.makeText(ShoppingCartActivity.this, "Anda Belum Belanja", Toast.LENGTH_SHORT).show();
                }
                else {
                    mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("To Checkout View")
                        .build());

                    cekCheckout(user.get("uid"),user.get("email"));

                }
            }
        });
    }

    private void cekCheckout(final String id_user, final String email) {

        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "Login Response: " + response.toString());
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    String message = jsonObject.getString("msg");

                    if(!error){
                        Intent intent = new Intent(ShoppingCartActivity.this, CheckOutActivity.class);
                        intent.putExtra("subtotal", hargaTotal.getText().toString().substring(4));
                        intent.putExtra("subtotalFix", hargaTotalAwal.getText().toString());
                        startActivity(intent);
                    }
                    else {
                        JSONArray jsonArray = jsonObject.getJSONArray("barang");
                        JSONObject object = jsonArray.getJSONObject(0);
                        String namaBarang = object.getString("nama");
                        String sisa = object.getString("sisa");
                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, message+", "+namaBarang+" tersisa sekarang :"+sisa, Snackbar.LENGTH_LONG)
                                .setAction("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                    }
                                });
                        View snackbarView = snackbar.getView();
                        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.WHITE);
                        textView.setMaxLines(5);  // show multiple line
                        snackbar.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    frameLoading.setVisibility(View.INVISIBLE);
                    retry();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Login Error: " + error.getMessage());
                Toast.makeText(ShoppingCartActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
                retry();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "precheckout");
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
    private void shoppingCart(final String id_user, final String email) {

        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "Login Response: " + response.toString());
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    String jml_panen = jsonObject.getString("jml_tglpanen");
                    String jml_tagihan = jsonObject.getString("jml_tagihan");
                    String nama_host = jsonObject.getString("nama_host");
                    String alamat_host = jsonObject.getString("alamat_host");

                    txtNamaHost.setText(nama_host);
                    txtAlamatHost.setText(alamat_host);

                    if (Integer.parseInt(jml_panen) > 1){
                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, "Tidak dapat Checkout karena terdapat tanggal panen berbada dalam keranjang anda", Snackbar.LENGTH_LONG)
                                .setAction("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                    }
                                });
                        snackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
                        View snackbarView = snackbar.getView();
                        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.WHITE);
                        textView.setMaxLines(5);  // show multiple line
                        snackbar.show();
                        btnCheckout.setEnabled(false);
                    }
                    else if (Integer.parseInt(jml_tagihan) > 0){
                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, "Tidak dapat Checkout karena terdapat tagihan di pengiriman sebelumnya", Snackbar.LENGTH_LONG)
                                .setAction("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                    }
                                });
                        snackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
                        View snackbarView = snackbar.getView();
                        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.WHITE);
                        textView.setMaxLines(5);  // show multiple line
                        snackbar.show();
                        btnCheckout.setEnabled(false);
                    }

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
                            kc_harga_asli = jObj.getString("kc_harga_asli");
                            kc_harga_promo = jObj.getString("kc_harga_promo");
                            hargaFix = jObj.getString("kc_subtotal_asli");


                            int jumlah_harga = Integer.parseInt(harga_jual)*Integer.parseInt(quantity);
                            int jumlah_harga_awal = Integer.parseInt(kc_harga_asli)*Integer.parseInt(quantity);


                            cart = new ShoppingCart(id_wishlist, id_barang, tgl_panen, nama_petani, quantity, foto, grade, satuan, nama_barang, harga_jual,subtotal, harga_jualrp, jumlah_harga+"", subtotalrp, kc_harga_asli, kc_harga_promo, jumlah_harga_awal+"");
                            data.add(cart);


                        }

                        hargaTotal.setText("Rp. "+cart.getSubtotal());
                        hargaTotalAwal.setText(hargaFix);

                        adapter = new ShoppingCartAdapter(ShoppingCartActivity.this, data);

//        shoppingCart(idUser, email);
                        adapter.setOnClickListener(ShoppingCartActivity.this);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(ShoppingCartActivity.this));

                        frameLoading.setVisibility(View.INVISIBLE);
                    }
                    else {
                        frameLoading.setVisibility(View.INVISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    frameLoading.setVisibility(View.INVISIBLE);
                    retry();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Login Error: " + error.getMessage());
                Toast.makeText(ShoppingCartActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
                retry();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "shoppingcart");
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

    @Override
    public void itemClicked(View view, int position) {

    }

    public void setLoading(){
        frameBuffer.setVisibility(View.VISIBLE);
    }
    public void dismissLoading(){
        frameBuffer.setVisibility(View.INVISIBLE);
    }

    public void setTotalHarga(String totalHarga){
        hargaTotal.setText(totalHarga);
    }
    public int getTotalHarga(){

        int i = Integer.parseInt(hargaTotal.getText().toString().substring(4));
        return i;
    }

    public int getTotalHargaAwal(){
        int i = Integer.parseInt(hargaTotalAwal.getText().toString());
        return i;
    }

    public void setTotalHargaAwal(String totalHargaAWAL){
        hargaTotalAwal.setText(totalHargaAWAL);
    }

//    public void setTotalHargaAwal(String totalHarga2){
//        this.hargaTotal2 = totalHarga2;
//    }
//    public int getTotalHargaAwal(){
//
////        int i = Integer.parseInt(hargaTotal.getText().toString().substring(4));
//        int i = Integer.parseInt(hargaTotal2);
//        return i;
//    }



    public void setBtnCheckout(boolean param, String text){
        btnCheckout.setEnabled(param);
//
        String message = "";
        if (text.equalsIgnoreCase("jml_tglpanen")){
            message = "Tidak dapat Checkout karena terdapat tanggal panen berbada dalam keranjang anda";
        }
        else{
            message = "Tidak dapat Checkout karena terdapat tagihan di pengiriman sebelumnya";
        }
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, message, Snackbar.LENGTH_LONG)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                });
        if (!param){
            snackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
            View snackbarView = snackbar.getView();
            TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            textView.setMaxLines(5);  // show multiple line
            snackbar.show();
        }
        else {
            snackbar.dismiss();
        }
    }
    private void retry() {

        frameLoading.setVisibility(View.INVISIBLE);
        new AlertDialog.Builder(this)
                .setTitle("Koneksi Bermasalah ")
                .setMessage("Coba lagi")
                .setPositiveButton("Coba lagi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        shoppingCart(user.get("uid"), user.get("email"));
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
        shoppingCart(idUser, email);
    }
}
