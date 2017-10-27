package com.kecipir.kecipir;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DaftarHostSecondActivity extends AppCompatActivity {

    String nama;
    String email;
    String notelp;
    String alamat;
    String namaKota;
    String jnsHost;

    EditText edtNamaMember1, edtEmailMember1, edtNoTelpMember1;
    EditText edtNamaMember2, edtEmailMember2, edtNoTelpMember2;
    EditText edtNamaMember3, edtEmailMember3, edtNoTelpMember3;
    CheckBox chkBox;
    Button btnDaftar;

    Toolbar toolbar;
    RelativeLayout frameBuffer;

    Dialog dialog;
    WebView wv;


    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_host_second);
        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Bundle b = getIntent().getExtras();
        nama = b.getString("nama");
        email = b.getString("email");
        notelp = b.getString("notelp");
        alamat = b.getString("alamat");
        namaKota = b.getString("kota");
        jnsHost = b.getString("jenis");

        edtNamaMember1 = (EditText) findViewById(R.id.edt_reghost_namamember1);
        edtNamaMember2 = (EditText) findViewById(R.id.edt_reghost_namamember2);
        edtNamaMember3 = (EditText) findViewById(R.id.edt_reghost_namamember3);

        edtEmailMember1 = (EditText) findViewById(R.id.edt_reghost_emailmember1);
        edtEmailMember2 = (EditText) findViewById(R.id.edt_reghost_emailmember2);
        edtEmailMember3 = (EditText) findViewById(R.id.edt_reghost_emailmember3);

        edtNoTelpMember1 = (EditText) findViewById(R.id.edt_reghost_notelpmember1);
        edtNoTelpMember2 = (EditText) findViewById(R.id.edt_reghost_notelpmember2);
        edtNoTelpMember3 = (EditText) findViewById(R.id.edt_reghost_notelpmember3);

        chkBox = (CheckBox) findViewById(R.id.chk_reghost);
        btnDaftar = (Button) findViewById(R.id.btn_reghost);
        frameBuffer = (RelativeLayout) findViewById(R.id.frame_buffer);
        AppController appController = (AppController) getApplication();
        mTracker = appController.getDefaultTracker();
        sendScreenName();



        chkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    dialog = new Dialog(DaftarHostSecondActivity.this, R.style.MyDialogTheme);
                    dialog.setContentView(R.layout.dialog_terms);
                    dialog.setTitle("Syarat dan Ketentuan Host");
                    dialog.setCancelable(true);
                    dialog.setCanceledOnTouchOutside(true);

                    wv = (WebView) dialog.findViewById(R.id.webview_syarat_dialog);

                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Action")
                            .setAction("Read Syarat dan Ketentuan Host")
                            .build());

                    loadTerms("host");
                }
            }

            private void loadTerms( final String loginAs) {

                String tag_string_req = "req_login";
                StringRequest strReq = new StringRequest(Request.Method.POST,
                        AppConfig.URL_LOGIN, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d("", "Forgot Pass Response: " + response.toString());

                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");

                            // Check for error node in json
                            if (!error) {
                                String errorMsg = jObj.getString("message");
                                String content = jObj.getString("content");

                                wv.loadData(content, "text/html", "UTF-8");
                                dialog.show();
//                        Toast.makeText(getApplicationContext(),
//                                errorMsg, Toast.LENGTH_LONG).show();
                            } else {
                                // Error in login. Get the error message
                                String errorMsg = jObj.getString("message");
                                Toast.makeText(getApplicationContext(),
                                        errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("", "Login Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        // Posting parameters to login url
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("tag", "syarat");
                        params.put("loginAs", loginAs);

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
        });
        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chkBox.isChecked()){
                    String namaMember1 = edtNamaMember1.getText().toString();
                    String emailMember1 = edtEmailMember1.getText().toString();
                    String notelpMember1 = edtNoTelpMember1.getText().toString();

                    String namaMember2 = edtNamaMember2.getText().toString();
                    String emailMember2 = edtEmailMember2.getText().toString();
                    String notelpMember2 = edtNoTelpMember2.getText().toString();

                    String namaMember3 = edtNamaMember3.getText().toString();
                    String emailMember3 = edtEmailMember3.getText().toString();
                    String notelpMember3 = edtNoTelpMember3.getText().toString();

                    if (namaMember1.equals("")||namaMember2.equals("")||namaMember3.equals("")||
                            emailMember1.equals("")||emailMember2.equals("")||emailMember3.equals("")||
                            notelpMember1.equals("")||notelpMember2.equals("")||notelpMember3.equals("")){
                        Toast.makeText(DaftarHostSecondActivity.this, "Kolom tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Action")
                                .setAction("Daftar Host")
                                .build());

                        frameBuffer.setVisibility(View.VISIBLE);
                        daftarHost(nama, email, notelp, alamat, namaKota, jnsHost
                                , namaMember1, emailMember1, notelpMember1
                                , namaMember2, emailMember2, notelpMember2
                                , namaMember3, emailMember3, notelpMember3);
//                        Toast.makeText(DaftarHostSecondActivity.this, "DO DAFTAR", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(DaftarHostSecondActivity.this, "Anda Harus Menyetujui Syarat dan Ketentuan yang Berlaku", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void daftarHost(final String nama, final String email, final String notelp, final String alamat, final String namaKota,
                            final String jnsHost, final String namaMember1, final String emailMember1, final String notelpMember1,
                            final String namaMember2, final String emailMember2, final String notelpMember2,
                            final String namaMember3, final String emailMember3, final String notelpMember3) {

        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "Daftar Response: " + response.toString());
                try {
                    if (response.equalsIgnoreCase("")) {
                        frameBuffer.setVisibility(View.INVISIBLE);
                    }
                    else {

                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("error")){

                            frameBuffer.setVisibility(View.INVISIBLE);
                            Toast.makeText(DaftarHostSecondActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                        else {

                            frameBuffer.setVisibility(View.INVISIBLE);
                            daftarFinish();
                        }
                    }
                } catch (JSONException e) {

                    frameBuffer.setVisibility(View.INVISIBLE);
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "View Checkout Error: " + error.getMessage());
                Toast.makeText(DaftarHostSecondActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
                frameBuffer.setVisibility(View.INVISIBLE);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                params.put("tag", "daftarhost");
                params.put("nama_host", nama);
                params.put("email_host", email);
                params.put("alamat_host", alamat);
                params.put("no_telp_host", notelp);
                params.put("cakupan", jnsHost);
                params.put("kota", namaKota);

                params.put("nama_member1", namaMember1);
                params.put("email_member1", emailMember1);
                params.put("no_telp_member1", notelpMember1);

                params.put("nama_member2", namaMember2);
                params.put("email_member2", emailMember2);
                params.put("no_telp_member2", notelpMember2);

                params.put("nama_member3", namaMember3);
                params.put("email_member3", emailMember3);
                params.put("no_telp_member3", notelpMember3);

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

    private void daftarFinish() {
        new AlertDialog.Builder(this)
                .setTitle("Daftar Berhasil")
                .setMessage("Terima Kasih telah Mendaftar, Kami Akan Menghubungi anda kembali setelah diproses")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(DaftarHostSecondActivity.this, MainActivity.class );
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                })
                .show();
    }
    private void sendScreenName() {
        String name = "Daftar Host 2";

        // [START screen_view_hit]
        Log.i("Daftar Host", "Screen name: " + name);
        mTracker.setScreenName("Screen" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // [END screen_view_hit]
    }
}
