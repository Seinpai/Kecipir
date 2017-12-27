package com.kecipir.kecipir;

import android.graphics.Typeface;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.StyleSpan;
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
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kecipir.kecipir.adapter.XenditCartAdapter;
import com.kecipir.kecipir.data.AppConfig;
import com.kecipir.kecipir.data.AppController;
import com.kecipir.kecipir.data.ShoppingCart;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailGreencashActivity extends AppCompatActivity {

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
    String total;
    String chck_paymentType;
    String diskon;
    int intTotal =0;

    ImageView img1,img2,img3,img4;


    SessionManager sessionManager;
    HashMap<String, String> user;

    RecyclerView recyclerView;
    TextView txtPaymentDesc;
    TextView txtTotalHarga;
    TextView txtKodeUnik;
    TextView txtTotalPembayaran;
    TextView txtBankTrans;
    TextView txtExpiry;
    TextView namaBank;
    Button btnCheckout;


    private CoordinatorLayout coordinatorLayout;
    private Tracker mTracker;

    String action, url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_greencash);
        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(this);
        user = sessionManager.getUser();
        final String email = user.get("email");
        final String idUser = user.get("uid");

        final Bundle b = getIntent().getExtras();
        chck_paymentType = b.getString("paymentType");

        Log.d("username", email+" - "+idUser);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_detail);

        txtPaymentDesc = (TextView) findViewById(R.id.txt_paymentdesc);
        txtTotalPembayaran = (TextView) findViewById(R.id.txt_total_pembayaran);
        txtBankTrans = (TextView) findViewById(R.id.txt_transfer_bank);
        txtExpiry = (TextView) findViewById(R.id.txt_expiry);
        namaBank = (TextView)findViewById(R.id.txt_namabank);
        frameLoading = (RelativeLayout) findViewById(R.id.frame_loading);
        frameBuffer = (RelativeLayout) findViewById(R.id.frame_buffer);

        AppController appController = (AppController) getApplication();
        mTracker = appController.getDefaultTracker();
        sendScreenName();

        SpannableString ss = new SpannableString(b.getString("nota"));
        ss.setSpan(new StyleSpan(Typeface.BOLD), 0, ss.length(),0);

        txtPaymentDesc.setText("Transaksi No. "+ss+" yang perlu dibayarkan sebesar");
        txtTotalPembayaran.setText("Jumlah Transfer : Rp. "+b.getString("amount"));
        txtBankTrans.setText(b.getString("virtual"));
        txtExpiry.setText(b.getString("expired"));

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        frameLoading.setVisibility(View.INVISIBLE);

        img1 = (ImageView) findViewById(R.id.img_bank1);
        img2 = (ImageView) findViewById(R.id.img_bank2);
        img3 = (ImageView) findViewById(R.id.img_bank3);
        img4 = (ImageView) findViewById(R.id.img_bank4);


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
        }



//        readXenditCheckout(idUser, email, b.getString("no_deposit"));

    }
    private void readXenditCheckout(final String id_user, final String email, final String no_deposit) {

        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_XENDITGREEN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("TAG", "Login Response: " + response.toString());
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");

                    if(!error){
                        txtTotalHarga.setText("Total Belanja : Rp. "+jsonObject.getString("total_belanja"));
                        txtKodeUnik.setText("Nomor unik : Rp. "+jsonObject.getString("identity_amount"));
                        txtTotalPembayaran.setText("Jumlah Transfer : Rp. "+jsonObject.getString("transfer_amount"));
                        txtBankTrans.setText(jsonObject.getString("bank_desc"));
                        txtExpiry.setText(jsonObject.getString("expiry_date"));

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
                Toast.makeText(DetailGreencashActivity.this,
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
                params.put("no_deposit", no_deposit);

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
        String name = "Detail Greencash via Xendit";

        // [START screen_view_hit]
        Log.i("Detail Greencash Xendit", "Screen name: " + name);
        mTracker.setScreenName("Screen" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // [END screen_view_hit]
    }
}
