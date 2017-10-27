package com.kecipir.kecipir;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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

public class DaftarHostActivity extends AppCompatActivity {

    EditText edtNama, edtEmail, edtNoTelp, edtAlamat, edtKelurahan, edtKecamatan, edtKodePos;
    Spinner spKota, spJenis, spinnerPengantaran;
    TextView txtSyarat;
    Button btnLanjut;

    List<String> kotaList;
    List<String> kecamatanList;
    List<String> kelurahanList;

    String jnsHost, nama_kota,nama_kecamatan,nama_kelurahan, privasi_host = "N", pengantaran_host = "N";

    Toolbar toolbar;

    RelativeLayout frameLoading;
    RelativeLayout frameBuffer;
    CheckBox chkBox, chkPrivasi;
    Dialog dialog;
    WebView wv;

    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_host);
        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edtNama = (EditText) findViewById(R.id.edt_reghost_nama);
        edtEmail = (EditText) findViewById(R.id.edt_reghost_email);
        edtNoTelp = (EditText) findViewById(R.id.edt_reghost_notelp);
        edtAlamat = (EditText) findViewById(R.id.edt_reghost_alamat);

        edtKelurahan = (EditText) findViewById(R.id.edt_reghost_kelurahan);
        edtKecamatan = (EditText) findViewById(R.id.edt_reghost_kecamatan);
        edtKodePos = (EditText) findViewById(R.id.edt_reghost_kodepos);
        txtSyarat = (TextView) findViewById(R.id.txt_syarat_ketentuan);

        spKota = (Spinner) findViewById(R.id.spinnerKotaHost);
        spJenis = (Spinner) findViewById(R.id.spinnerJenisHost);
        chkBox = (CheckBox) findViewById(R.id.chk_reghost);
        chkPrivasi = (CheckBox) findViewById(R.id.chk_privasi_reghost);
        spinnerPengantaran = (Spinner) findViewById(R.id.spinner_reghost_pengantaran);

        btnLanjut = (Button) findViewById(R.id.btn_reghost_lanjut1);

        frameLoading = (RelativeLayout) findViewById(R.id.frame_loading);
        frameBuffer = (RelativeLayout) findViewById(R.id.frame_buffer);
        AppController appController = (AppController) getApplication();
        mTracker = appController.getDefaultTracker();
        sendScreenName();

        List<String> jenisList = new ArrayList<String>();
//        jenisList.add("Private ");
        jenisList.add("Perumahan");
        jenisList.add("Tempat Kerja");
        jenisList.add("Kampus/Sekolah");

        ArrayAdapter<String> jenisAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, jenisList);
        jenisAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spJenis.setAdapter(jenisAdapter);

        spJenis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                jnsHost = parent.getItemAtPosition(position).toString();
//                Toast.makeText(DaftarHostActivity.this, jnsHost, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        List<String> pengantaranList = new ArrayList<>();

        pengantaranList.add("Ya");
        pengantaranList.add("Tidak");

        ArrayAdapter<String> pengantaranAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, pengantaranList);
        pengantaranAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPengantaran.setAdapter(pengantaranAdapter);
        spinnerPengantaran.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        pengantaran_host = "Y";
                        break;
                    case 1:
                        pengantaran_host = "N";
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        kotaList = new ArrayList<>();
        kecamatanList = new ArrayList<>();
        kelurahanList = new ArrayList<>();
        getKota();
        getKecamatan();
        getKelurahan();

        chkPrivasi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    privasi_host = "Y";
                }
                else{
                    privasi_host = "N";
                }
            }
        });

        SpannableString ss = new SpannableString("Saya Setuju dengan Syarat dan Ketentuan Berlaku");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                dialog = new Dialog(DaftarHostActivity.this, R.style.MyDialogTheme);
                dialog.setContentView(R.layout.dialog_terms);
                dialog.setTitle("Syarat dan Ketentuan Member");
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);

                wv = (WebView) dialog.findViewById(R.id.webview_syarat_dialog);
                Button btnOk = (Button) dialog.findViewById(R.id.btn_ok);
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Syarat dan Ketentuan Member")
                        .build());

                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });


                loadTerms("host");
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
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
        };
        ss.setSpan(clickableSpan, 19, 39, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        ss.setSpan(new ForegroundColorSpan(Color.GREEN), 19, 39, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new UnderlineSpan(), 19, 39, 0);

        txtSyarat.setText(ss);
        txtSyarat.setMovementMethod(LinkMovementMethod.getInstance());
//        txtSyarat.setHighlightColor(Color.BLACK);


        chkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }


        });

        btnLanjut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chkBox.isChecked()){
                    String nama = edtNama.getText().toString();
                    String email = edtEmail.getText().toString();
                    String notelp = edtNoTelp.getText().toString();
                    String alamat = edtAlamat.getText().toString();
                    String kelurahan = edtKelurahan.getText().toString();
                    String kecamatan = edtKecamatan.getText().toString();
                    String kodepos = edtKodePos.getText().toString();

                    if (nama.equals("")||email.equals("")||notelp.equals("")||alamat.equals("")){
                        Toast.makeText(DaftarHostActivity.this, "Kolom tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    }
                    else if (kelurahan.equals("")){
                        Toast.makeText(DaftarHostActivity.this, "Kelurahan tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        frameBuffer.setVisibility(View.VISIBLE);
                        daftarHost(nama, email, notelp, alamat, nama_kota, jnsHost, privasi_host, pengantaran_host, kelurahan, kecamatan, kodepos);
                    }
                }
                else{
                    Toast.makeText(DaftarHostActivity.this, "Anda Harus Menyetujui Syarat dan Ketentuan yang Berlaku", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void daftarHost(final String nama, final String email, final String notelp, final String alamat, final String namaKota,
                            final String jnsHost, final String privasi_host, final String pengantaran_host,
                            final String kelurahan, final String kecamatan, final String kode_pos) {

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
                            Toast.makeText(DaftarHostActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
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
                Log.e("TAG", "View DaftarHost Error: " + error.getMessage());
                Toast.makeText(DaftarHostActivity.this,
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
                params.put("privasi", privasi_host);
                params.put("antar", pengantaran_host);
                params.put("kelurahan", kelurahan);
                params.put("kecamatan", kecamatan);
                params.put("kodepos", kode_pos);

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



    private void getKota() {
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "Dftar Host Response: " + response.toString());
                try {
                    if (response.equalsIgnoreCase("")) {
//                                txtUser.setText("JUMLAH KOSONG");
                        frameLoading.setVisibility(View.INVISIBLE);
                        retry();
                    } else {

                        JSONArray jArr = new JSONArray(response);

                        final int length = jArr.length();

                        final String[] kota = new String[length];
                        for (int i = 0; i < length; i++) {
                            JSONObject jObj = (JSONObject) jArr.get(i);
                            String idKota = jObj.getString("id_kota");
                            String namaKota = jObj.getString("kota");
                            kota[i] = idKota;
                            kotaList.add(namaKota);

                        }

                        ArrayAdapter<String> tglPanenAdapter = new ArrayAdapter<String>(DaftarHostActivity.this, android.R.layout.simple_spinner_item, kotaList);
                        tglPanenAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spKota.setAdapter(tglPanenAdapter);
                        frameLoading.setVisibility(View.INVISIBLE);


                        spKota.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                for (int i = 0; i < length; i++) {
                                    if (position == i) {
                                        nama_kota = kota[i];
//                                        Toast.makeText(DaftarHostActivity.this, nama_kota, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    retry();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "StoreLIst Error: " + error.getMessage());
                Toast.makeText(DaftarHostActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
                retry();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "getCmbKota");

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

    private void getKecamatan() {
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "Dftar Host Response: " + response.toString());
                try {
                    if (response.equalsIgnoreCase("")) {
//                                txtUser.setText("JUMLAH KOSONG");
                        frameLoading.setVisibility(View.INVISIBLE);
                        retry();
                    } else {

                        JSONArray jArr = new JSONArray(response);

                        final int length = jArr.length();

                        final String[] kota = new String[length];
                        for (int i = 0; i < length; i++) {
                            JSONObject jObj = (JSONObject) jArr.get(i);
                            String idKota = jObj.getString("id_kota");
                            String namaKota = jObj.getString("kota");
                            kota[i] = idKota;
                            kotaList.add(namaKota);

                        }

                        ArrayAdapter<String> tglPanenAdapter = new ArrayAdapter<String>(DaftarHostActivity.this, android.R.layout.simple_spinner_item, kotaList);
                        tglPanenAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spKota.setAdapter(tglPanenAdapter);
                        frameLoading.setVisibility(View.INVISIBLE);


                        spKota.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                for (int i = 0; i < length; i++) {
                                    if (position == i) {
                                        nama_kota = kota[i];
//                                        Toast.makeText(DaftarHostActivity.this, nama_kota, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    retry();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "StoreLIst Error: " + error.getMessage());
                Toast.makeText(DaftarHostActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
                retry();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "getCmbKota");

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

    private void getKelurahan() {
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "Dftar Host Response: " + response.toString());
                try {
                    if (response.equalsIgnoreCase("")) {
//                                txtUser.setText("JUMLAH KOSONG");
                        frameLoading.setVisibility(View.INVISIBLE);
                        retry();
                    } else {

                        JSONArray jArr = new JSONArray(response);

                        final int length = jArr.length();

                        final String[] kota = new String[length];
                        for (int i = 0; i < length; i++) {
                            JSONObject jObj = (JSONObject) jArr.get(i);
                            String idKota = jObj.getString("id_kota");
                            String namaKota = jObj.getString("kota");
                            kota[i] = idKota;
                            kotaList.add(namaKota);

                        }

                        ArrayAdapter<String> tglPanenAdapter = new ArrayAdapter<String>(DaftarHostActivity.this, android.R.layout.simple_spinner_item, kotaList);
                        tglPanenAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spKota.setAdapter(tglPanenAdapter);
                        frameLoading.setVisibility(View.INVISIBLE);


                        spKota.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                for (int i = 0; i < length; i++) {
                                    if (position == i) {
                                        nama_kota = kota[i];
//                                        Toast.makeText(DaftarHostActivity.this, nama_kota, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    retry();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "StoreLIst Error: " + error.getMessage());
                Toast.makeText(DaftarHostActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
                retry();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "getCmbKota");

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



    private void retry() {

        frameLoading.setVisibility(View.VISIBLE);
        new AlertDialog.Builder(this)
                .setTitle("Koneksi Bermasalah")
                .setMessage("Coba lagi")
                .setPositiveButton("Coba lagi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getKota();
                        getKecamatan();
                        getKelurahan();
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
        String name = "Daftar Host";

        // [START screen_view_hit]
        Log.i("DaftarHost", "Screen name: " + name);
        mTracker.setScreenName("Screen" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // [END screen_view_hit]
    }

    private void daftarFinish() {
        new AlertDialog.Builder(this)
                .setTitle("Daftar Berhasil")
                .setMessage("Terima Kasih telah Mendaftar, Kami Akan Menghubungi anda kembali setelah diproses")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(DaftarHostActivity.this, MainActivity.class );
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                })
                .show();
    }
}
