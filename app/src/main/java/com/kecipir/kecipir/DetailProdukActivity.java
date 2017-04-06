package com.kecipir.kecipir;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;
import com.kecipir.kecipir.data.AppConfig;
import com.kecipir.kecipir.data.AppController;
import com.kecipir.kecipir.data.DetailProduk;
import com.kecipir.kecipir.data.StoreList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailProdukActivity extends AppCompatActivity {

    Toolbar toolbar;

    String id_barang_panen;
    String tgl_panen;
    String foto;
    String nama_barang;
    String nama_petani;
    String grade;
    String kategori;
    String satuan;
    String stock;
    String harga_jual;
    boolean sale;
    String ket;
    int jml;

    DetailProduk detailProduk;
    List<DetailProduk> data;

    ImageView fotoProduk, imageSale, btnFavorite, btnShare;
    TextView namaProduk, hargaJual, namaPetani, stockBarang,
            satuanBarang, kategoriBarang, ketBarang,
            gradeProduk, tglPengiriman;
    TextView txtJml;
    Button btnMin, btnPlus, btnAddCart;
    RelativeLayout frameLoading, frameBuffer;

    SessionManager sessionManager;
    String email, id_user, id_barangtoCart, tgl_value;


    private Tracker mTracker;
    Product viewedProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_produk);
        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        Bundle b =getIntent().getExtras();
        final String id_barang = b.getString("id_barang");
        String nama_barang = b.getString("nama_produk");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(nama_barang);

        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUser();
        email = user.get("email");
        id_user = user.get("uid");
        id_barangtoCart = id_barang;
        tgl_value = user.get("tglpanen");
        if (user.get("tglpanen")== null){
            tgl_value = "";
        }


        frameLoading = (RelativeLayout) findViewById(R.id.frame_loading);
        frameBuffer = (RelativeLayout) findViewById(R.id.frame_buffer);
        fotoProduk = (ImageView) findViewById(R.id.foto_produk);
//        btnFavorite = (ImageView) findViewById(R.id.btn_favorite);
//        btnShare = (ImageView) findViewById(R.id.btn_share);
        imageSale = (ImageView) findViewById(R.id.image_detailsale);

        namaProduk = (TextView) findViewById(R.id.nama_produk);
        gradeProduk = (TextView) findViewById(R.id.grade_produk);
        hargaJual = (TextView) findViewById(R.id.harga_jual);
        satuanBarang = (TextView) findViewById(R.id.satuan);
        ketBarang = (TextView) findViewById(R.id.ket);
        kategoriBarang = (TextView) findViewById(R.id.kategori_barang);
        tglPengiriman = (TextView) findViewById(R.id.tgl_pengiriman);


        txtJml = (TextView) findViewById(R.id.txt_jml);

        btnMin = (Button) findViewById(R.id.btn_min);
        btnPlus = (Button) findViewById(R.id.btn_plus);
        btnAddCart = (Button) findViewById(R.id.btn_addcart);

        AppController appController = (AppController) getApplication();
        mTracker = appController.getDefaultTracker();
        sendScreenName();

        jml = 0;

        btnMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jml -= 1;

                if (jml < 0 ){
                    jml = 0;
                    Toast.makeText(DetailProdukActivity.this, "Jumlah tidak bisa minus", Toast.LENGTH_SHORT).show();
                }
                else {
                    txtJml.setText(""+jml);
                }
            }
        });

        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jml += 1;
                txtJml.setText(""+jml);
            }
        });

        btnAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sessionManager.isLoggedIn()){
                    if (jml > Integer.valueOf(stock)){
                        Toast.makeText(DetailProdukActivity.this, "Anda tidak dapat memesan melebihi stock yang ada!", Toast.LENGTH_SHORT).show();
                    }
                    else if (jml == 0){
                        Toast.makeText(DetailProdukActivity.this, "Anda belum memesan!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        frameBuffer.setVisibility(View.VISIBLE);
                        addToCart(email, id_user, ""+jml, id_barang_panen, tgl_value);
                    }
                }
                else {
                    Intent i = new Intent(DetailProdukActivity.this, MemberStartActivity.class);
                    startActivity(i);
                }

            }
        });

        data = new ArrayList<>();

        detailBarang(id_barang, tgl_value);


    }

    private void detailBarang(final String idBarangPanen, final String tgl_value) {

        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "StoreList Response: " + response.toString());
                detailProduk = new DetailProduk();
                try {
                    if (response.equalsIgnoreCase("")) {
                        frameLoading.setVisibility(View.INVISIBLE);
                    }
                    else {

                        JSONObject jsonObject = new JSONObject(response);
                        id_barang_panen = jsonObject.getString("id_barang_panen");
                        tgl_panen = jsonObject.getString("tgl_panen");
                        foto = AppConfig.IMAGE_LINK+""+jsonObject.getString("foto");
                        nama_barang = jsonObject.getString("nama_barang");
                        nama_petani = jsonObject.getString("nama_petani");
                        grade = jsonObject.getString("grade");
                        kategori = jsonObject.getString("kategori");
                        satuan = jsonObject.getString("satuan");
                        stock = jsonObject.getString("qtyfinal");
                        harga_jual = jsonObject.getString("harga_jual");
                        sale = jsonObject.getBoolean("sale");
                        ket = jsonObject.getString("ket");


                        frameLoading.setVisibility(View.INVISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    retryDetail();
                }
                Glide.with(DetailProdukActivity.this).load(foto).into(fotoProduk);

                if (sale){
                    imageSale.setVisibility(View.VISIBLE);
                }
                else {
                    imageSale.setVisibility(View.INVISIBLE);
                }

                namaProduk.setText(nama_barang);
                if (sessionManager.isLoggedIn()){
                    gradeProduk.setText("Grade "+grade+" | "+nama_petani+" | Stock tinggal "+stock);
                    tglPengiriman.setText("Jadwal Pengiriman : "+tgl_panen);
                }
                else {
                    gradeProduk.setText("Grade "+grade+" | "+nama_petani);
                    tglPengiriman.setVisibility(View.GONE);
                }
                hargaJual.setText("Rp. "+harga_jual);
                satuanBarang.setText("/" + satuan);
                kategoriBarang.setText(kategori);
                ketBarang.setText(ket);


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "StoreLIst Error: " + error.getMessage());
                Toast.makeText(DetailProdukActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
                frameLoading.setVisibility(View.INVISIBLE);
                retryDetail();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "detail");
                params.put("id", idBarangPanen);
                params.put("tgl_panen", tgl_value);

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

    private void addToCart(final String email, final String id_user, final String jml, final String id_barang, final String tgl_value) {

        String tag_string_req = "";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "Login Response: " + response.toString());

                try {

                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
//
                    if (!error) {
                        Product viewedProduct =  new Product()
                                .setId(id_barang_panen)
                                .setName(nama_barang)
                                .setCategory(kategori)
                                .setPrice(Long.parseLong(harga_jual))
                                .setPosition(1)
                                .setQuantity(Integer.parseInt(jml));

                        ProductAction productAction = new ProductAction(ProductAction.ACTION_CLICK)
                                .setProductActionList("Add Cart");
                        HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder()
                                .addProduct(viewedProduct)
                                .setProductAction(productAction);
                        mTracker.setScreenName("Add cart");
                        mTracker.set("&cu", "IDR");
                        mTracker.send(builder.build());

                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Action")
                                .setAction("Add to Cart")
                                .build());

                        boolean barang_lain = jObj.getBoolean("barang_lain");
                        addToCartFinish();
                    } else {
                        String errorMsg = jObj.getString("msg");
                        Toast.makeText(DetailProdukActivity.this,errorMsg, Toast.LENGTH_SHORT).show();
//                        retry();
                    }

                    frameBuffer.setVisibility(View.INVISIBLE);

                } catch (JSONException e) {
                    e.printStackTrace();
                    frameBuffer.setVisibility(View.INVISIBLE);
                    retry();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Login Error: " + error.getMessage());
                Toast.makeText(DetailProdukActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
                frameBuffer.setVisibility(View.INVISIBLE);
                retry();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "addtocart");
                params.put("id_barang", id_barang);
                params.put("email", email);
                params.put("id_user", id_user);
                params.put("jml", jml);
                params.put("tgl_panen", tgl_value);

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

    private void addToCartFinish() {
        new AlertDialog.Builder(this)
                .setTitle("Pesan")
                .setMessage("Tambah Keranjang Berhasil")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Intent intent = new Intent(DetailProdukActivity.this, HistoryActivity.class );
//                        startActivity(intent);
                        finish();
                    }
                })
                .show();
    }

    private void retry() {
        frameBuffer.setVisibility(View.VISIBLE);
        new AlertDialog.Builder(this)
                .setTitle("Koneksi Bermasalah")
                .setMessage("Coba lagi")
                .setPositiveButton("Coba lagi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        addToCart(email, id_user, ""+jml, id_barang_panen, tgl_value);
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

    private void retryDetail() {
        frameLoading.setVisibility(View.VISIBLE);
        new AlertDialog.Builder(this)
                .setTitle("Koneksi Bermasalah")
                .setMessage("Coba lagi")
                .setPositiveButton("Coba lagi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        detailBarang(id_barangtoCart, tgl_value);
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
        String name = "Detail Produk";

        // [START screen_view_hit]
        Log.i("DetailProduk", "Screen name: " + name);
        mTracker.setScreenName("Screen" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // [END screen_view_hit]
    }
}
