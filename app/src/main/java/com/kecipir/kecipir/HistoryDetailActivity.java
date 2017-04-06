package com.kecipir.kecipir;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kecipir.kecipir.adapter.HistoryDetailAdapter;
import com.kecipir.kecipir.data.AppConfig;
import com.kecipir.kecipir.data.AppController;
import com.kecipir.kecipir.data.HistoryDetail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryDetailActivity extends AppCompatActivity {

    Toolbar toolbar;
    SessionManager sessionManager;

    HistoryDetail detail;
    List<HistoryDetail> dataHistoryDetail;
    RecyclerView historyDetailRecyclerView;
    HistoryDetailAdapter hitoryDetailAdapter;
    RelativeLayout frameLoading;

    TextView txtNoNota, txtNamaHost, txtNamaMember, txtPembayaran, txtDelivery, txtStatus, txtDiskon, txtOngkir, txtTotalPembelian;
    TextView lblDiskon, lblOngkir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);
        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Detail Belanjaan");

        frameLoading = (RelativeLayout) findViewById(R.id.frame_loading);
        historyDetailRecyclerView = (RecyclerView) findViewById(R.id.recycler_historydetail);
        Bundle b = getIntent().getExtras();
        txtNoNota = (TextView) findViewById(R.id.txt_nonota);
        txtDiskon = (TextView) findViewById(R.id.txt_diskon);
        txtOngkir = (TextView) findViewById(R.id.txt_ongkir);
        txtNamaHost = (TextView) findViewById(R.id.txt_namahost);
        txtNamaMember = (TextView) findViewById(R.id.txt_namamember);
        txtPembayaran = (TextView) findViewById(R.id.txt_paymentdesc);
        txtDelivery = (TextView) findViewById(R.id.txt_delivery);
        txtStatus = (TextView) findViewById(R.id.txt_bayar);

        lblDiskon = (TextView) findViewById(R.id.lbl_diskon);
        lblOngkir = (TextView) findViewById(R.id.lbl_ongkir);
        txtTotalPembelian = (TextView) findViewById(R.id.txt_total_order);


        dataHistoryDetail = new ArrayList<>();

        parseHistoryDetail(b.getString("nota"));

        txtNoNota.setText(b.getString("nota"));
    }

    private void parseHistoryDetail(final String nota) {

        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "Login Response: " + response.toString());
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    if (!error){
                        String namahost = jsonObject.getString("nama_host");
                        String namamember = jsonObject.getString("nama_member");
                        String paymentdesc = jsonObject.getString("payment_desc");
                        String delivery = jsonObject.getString("delivery_name");
                        String deliveryType = jsonObject.getString("delivery_type");
                        String bayar = jsonObject.getString("bayar");
                        String ongkir = jsonObject.getString("ongkir");
                        String diskon = jsonObject.getString("diskon");
                        String total_pembelianrp = jsonObject.getString("total_pembelianrp");
                        JSONArray jsonArray = jsonObject.getJSONArray("barang");
                        int length = jsonArray.length();
                        for (int i =0;i<length;i++) {
                            JSONObject jObj = (JSONObject) jsonArray.get(i);
//                    // Check for error node in json
                            detail = new HistoryDetail();
                            String namaBarang = jObj.getString("nama_barang");
                            String foto = AppConfig.IMAGE_LINK + "" + jObj.getString("foto");
                            String grade = jObj.getString("grade");
                            String satuan = jObj.getString("satuan");
                            String quantity = jObj.getString("qty");
                            String namaPetani = jObj.getString("nama_petani");
                            String harga = jObj.getString("harga_jualrp");
                            String total = jObj.getString("subtotalrp");
                            detail = new HistoryDetail(foto, namaBarang, namaPetani, grade, satuan, quantity, harga, total);
                            dataHistoryDetail.add(detail);
                        }
                        txtStatus.setText(bayar);
                        txtDelivery.setText(delivery);
                        txtPembayaran.setText(paymentdesc);
                        txtNamaHost.setText(namahost);
                        txtNamaMember.setText(namamember);
                        txtTotalPembelian.setText("Rp. "+total_pembelianrp);
                        txtOngkir.setText("Rp. "+ongkir);
                        txtDiskon.setText("Rp. "+diskon);
                        frameLoading.setVisibility(View.INVISIBLE);
                        hitoryDetailAdapter = new HistoryDetailAdapter(HistoryDetailActivity.this, dataHistoryDetail);
                        historyDetailRecyclerView.setAdapter(hitoryDetailAdapter);
                        historyDetailRecyclerView.setLayoutManager(new LinearLayoutManager(HistoryDetailActivity.this));

                        if (!deliveryType.equalsIgnoreCase("2")){
                            txtOngkir.setVisibility(View.GONE);
                            lblOngkir.setVisibility(View.GONE);
                        }
                        if (diskon.equalsIgnoreCase("0")){
                            lblDiskon.setVisibility(View.GONE);
                            txtDiskon.setVisibility(View.GONE);
                        }
                    }else {
                        // Error in login. Get the error message
                        String errorMsg = jsonObject.getString("error_msg");
                        Toast.makeText(HistoryDetailActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }

                }
                catch(JSONException e)
                {
                    e.printStackTrace();
                }

            }
        },new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse (VolleyError error){
                Log.e("TAG", "Login Error: " + error.getMessage());
                Toast.makeText(HistoryDetailActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams () {
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "newhistorydetail");
                params.put("nota", nota);

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
        AppController.getInstance(). addToRequestQueue(strReq, tag_string_req);

    }
}
