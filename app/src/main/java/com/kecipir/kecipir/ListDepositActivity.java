package com.kecipir.kecipir;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kecipir.kecipir.adapter.DepositAdapter;
import com.kecipir.kecipir.adapter.PembayaranAdapter;
import com.kecipir.kecipir.data.AppConfig;
import com.kecipir.kecipir.data.AppController;
import com.kecipir.kecipir.data.ClickListener;
import com.kecipir.kecipir.data.DepositList;
import com.kecipir.kecipir.data.PembayaranList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListDepositActivity extends AppCompatActivity implements ClickListener{


    RelativeLayout frameLoading,frameBuffer, layoutSpanner;
    RecyclerView recyclerView;
    TextView txtMessage, txtTotal, txtTotalDepo, txtTotalBonus, txtTotalKomisi, lblTotalKomisi;
    Button btnTarik;

    DepositAdapter adapter;
    SessionManager sessionManager;
    ImageView imgSpanner;

    String email, id_user;
    String total;


    DepositList list;
    List<DepositList> data;

    boolean spanned = false;
    private Tracker mTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_deposit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        recyclerView = (RecyclerView) findViewById(R.id.recycler_greencash);
        frameLoading = (RelativeLayout) findViewById(R.id.frame_loading);
        frameBuffer = (RelativeLayout) findViewById(R.id.frame_buffer);
        txtMessage = (TextView) findViewById(R.id.txt_message);
        txtTotal = (TextView) findViewById(R.id.txt_total_depo);
        txtTotalDepo = (TextView) findViewById(R.id.txt_total_depo_utama);
        txtTotalBonus = (TextView) findViewById(R.id.txt_total_bonus);
        txtTotalKomisi = (TextView) findViewById(R.id.txt_total_komisi);
        lblTotalKomisi = (TextView) findViewById(R.id.lbl_total_komisi);
        layoutSpanner = (RelativeLayout) findViewById(R.id.layout_spanner);
        imgSpanner = (ImageView) findViewById(R.id.img_spanner);
        btnTarik = (Button) findViewById(R.id.btnPenarikan);

        AppController appController = (AppController) getApplication();
        mTracker = appController.getDefaultTracker();
        sendScreenName();

        data = new ArrayList<>();
        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUser();
        email = user.get("email");
        id_user = user.get("uid");

        String loginAs = user.get("loginAs");
        if(loginAs.equalsIgnoreCase("member")){
            lblTotalKomisi.setVisibility(View.GONE);
            txtTotalKomisi.setVisibility(View.GONE);
        }

        parseDeposit(email, id_user);
        adapter = new DepositAdapter(this, data);

        adapter.setOnClickListener(this);
        imgSpanner.setImageResource(R.drawable.ic_hardware_keyboard_arrow_right);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ListDepositActivity.this, AddGreencashActivity.class);
                startActivity(i);

//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        layoutSpanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!spanned){
                    recyclerView.setVisibility(View.VISIBLE);
                    imgSpanner.setImageResource(R.drawable.ic_hardware_keyboard_arrow_down);
                    spanned = true;
                }
                else {
                    recyclerView.setVisibility(View.INVISIBLE);
                    imgSpanner.setImageResource(R.drawable.ic_hardware_keyboard_arrow_right);
                    spanned = false;
                }
            }
        });


        btnTarik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ListDepositActivity.this, TarikDepositActivity.class);
                i.putExtra("deposit", txtTotalDepo.getText().toString());
                startActivity(i);
            }
        });
    }

    private void parseDeposit(final String email, final String id) {

        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_DEPOSIT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "greencash List Response: " + response.toString());
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    String totalrp = jsonObject.getString("totalrp");
                    total = jsonObject.getString("total");
                    if (total.equalsIgnoreCase("0")){
//                        btnTarik.setVisibility(View.INVISIBLE);
                        btnTarik.setEnabled(false);
                    }
                    String totalDeporp = jsonObject.getString("total_deporp");
                    String totalBonusrp = jsonObject.getString("total_bonusrp");
                    String totalKomisirp = jsonObject.getString("komisirp");
                    String listDeposit = jsonObject.getString("listdeposit");

                    txtTotal.setText("Rp. "+totalrp);
                    txtTotalDepo.setText("Rp. "+totalDeporp);
                    txtTotalBonus.setText("Rp. "+totalBonusrp);
                    txtTotalKomisi.setText("Rp. "+totalKomisirp);
                    JSONArray jsonArray = new JSONArray(listDeposit);
                    int length = jsonArray.length();
                    if (length == 0){
                        frameLoading.setVisibility(View.INVISIBLE);
                        txtMessage.setVisibility(View.VISIBLE);
                    }
                    else{
                        txtMessage.setVisibility(View.INVISIBLE);
                        for (int i =0;i<length;i++){
                            JSONObject jObj = (JSONObject) jsonArray.get(i);
                            list = new DepositList();

                                String nodeposit = jObj.getString("no_deposit");
                                String tanggal = jObj.getString("tanggal");
                                String amount = jObj.getString("amount");
                                String sts = jObj.getString("sts");
                                String ket = jObj.getString("ket");
                                String payment = jObj.getString("payment");
                                String payment_type = jObj.getString("payment_type");

                                list = new DepositList(nodeposit, amount, tanggal, sts, ket, payment,payment_type);
                                data.add(list);
                                // Error in login. Get the error message
//                                String errorMsg = jObj.getString("error_msg");
//                                Toast.makeText(ListDepositActivity.this, errorMsg, Toast.LENGTH_LONG).show();
//
                        }
                        frameLoading.setVisibility(View.INVISIBLE);

//                        adapter = new DepositAdapter(ListDepositActivity.this, data);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(ListDepositActivity.this));
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
                Toast.makeText(ListDepositActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams () {
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "viewdeposit");
                params.put("email", email);
                params.put("id_user", id);

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
        String no_deposit = data.get(position).getNoDeposit();
        String total = data.get(position).getAmount();
        String tujuan = data.get(position).getPayment();
        String payment_type = data.get(position).getPaymentType();

        if (data.get(position).getStatus().equalsIgnoreCase("Belum dikonfirmasi")) {
            if (payment_type.equalsIgnoreCase("8")){

                Intent i = new Intent(ListDepositActivity.this, DetailGreencashActivity.class);
//            Intent i = new Intent(ListDepositActivity.this, KonfirmDepositActivity.class);
                i.putExtra("no_deposit", no_deposit);
                i.putExtra("total", total);
                i.putExtra("tujuan", tujuan);
                startActivity(i);
            }
            else {
//                Intent i = new Intent(ListDepositActivity.this, DetailGreencashActivity.class);
                Intent i = new Intent(ListDepositActivity.this, KonfirmDepositActivity.class);
                i.putExtra("no_deposit", no_deposit);
                i.putExtra("total", total);
                i.putExtra("tujuan", tujuan);
                startActivity(i);
            }
        }
    }

    private void sendScreenName() {
        String name = "list Deposit";

        // [START screen_view_hit]
        Log.i("List DepositActivity", "Screen name: " + name);
        mTracker.setScreenName("Screen" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // [END screen_view_hit]
    }
}
