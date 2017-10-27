package com.kecipir.kecipir;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
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
import com.bumptech.glide.Glide;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kecipir.kecipir.data.AppConfig;
import com.kecipir.kecipir.data.AppController;
import com.kecipir.kecipir.data.DetailProduk;
import com.kecipir.kecipir.data.HistoryList;
import com.scottyab.showhidepasswordedittext.ShowHidePasswordEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    Toolbar toolbar;
    SessionManager sessionManager;
    HashMap<String, String> user;

    String email, id_user,id_host, loginAs, gender, idKota, pengantaran = "N", privasi = "N", cod ="1";
    EditText edtKode, edtNama, edtEmail, edtUsername, edtAlamat, edtKota, edtNoTelp, edtNamaHost;
    TextView txtUsername, txtNamaHost, txtGender, txtPengantaran, txtPrivasi, txtCOD;

    Spinner spinnerGender, spinnerPengantaran, spinnerCOD;
    CheckBox chkPrivasi;
    Button btnGantiPass, btnEditProfil, btnAkunBank;

    Dialog dialog;

    RelativeLayout frameLoading, frameBuffer;
    private Tracker mTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        toolbar = (Toolbar) findViewById(R.id.appbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(this);
        user = sessionManager.getUser();
        email = user.get("email");
        id_user = user.get("uid");
        loginAs = user.get("loginAs");
        id_host = user.get("id_host");

        txtGender = (TextView) findViewById(R.id.txt_profil_jnskelamin);
        txtNamaHost = (TextView) findViewById(R.id.txt_profil_namahost);
        txtUsername = (TextView) findViewById(R.id.txt_profil_username);
        txtPrivasi = (TextView) findViewById(R.id.txt_profil_privasi);

        edtKode = (EditText) findViewById(R.id.edt_profil_kode);
        edtEmail = (EditText) findViewById(R.id.edt_profil_email);
        edtNama = (EditText) findViewById(R.id.edt_profil_nama);
        edtKota = (EditText) findViewById(R.id.edt_profil_kota);
        edtNamaHost = (EditText) findViewById(R.id.edt_profil_namahost);
        edtNoTelp = (EditText) findViewById(R.id.edt_profil_notelp);
        spinnerGender = (Spinner) findViewById(R.id.spinner_jnskelamin);
        edtAlamat = (EditText) findViewById(R.id.edt_profil_alamat);
        edtUsername = (EditText) findViewById(R.id.edt_profil_username);
        spinnerCOD = (Spinner) findViewById(R.id.spinner_profil_cod);
        txtCOD = (TextView) findViewById(R.id.txt_profil_cod);

        txtPengantaran = (TextView) findViewById(R.id.txt_profil_pengantaran);
        spinnerPengantaran = (Spinner) findViewById(R.id.spinner_profil_pengantaran);
        chkPrivasi = (CheckBox) findViewById(R.id.chk_profil_privasi);
        frameLoading = (RelativeLayout) findViewById(R.id.frame_loading);
        frameBuffer = (RelativeLayout) findViewById(R.id.frame_buffer);

        btnGantiPass = (Button) findViewById(R.id.btn_gantipass);
        btnEditProfil = (Button) findViewById(R.id.btn_edtprofil);
        btnAkunBank = (Button) findViewById(R.id.btn_akunbank_profil);

        AppController appController = (AppController) getApplication();
        mTracker = appController.getDefaultTracker();
        sendScreenName();

        List<String> genderList = new ArrayList<String>();
        List<String> pengantaranList = new ArrayList<>();
        List<String> codList = new ArrayList<>();

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
                        pengantaran = "Y";
                        break;
                    case 1:
                        pengantaran = "N";
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        codList.add("Ya");
        codList.add("Tidak");

        ArrayAdapter<String> codAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, codList);
        codAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCOD.setAdapter(codAdapter);
        spinnerCOD.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        cod = "1";
                        break;
                    case 1:
                        cod = "0";
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        chkPrivasi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    privasi = "Y";
                }
                else{
                    privasi = "N";
                }
            }
        });
        if (user.get("loginAs").equalsIgnoreCase("host")){
            genderList.add("Perumahan");
            genderList.add("Tempat Kerja");
            genderList.add("Sekolah/Kampus");
            txtGender.setText("Wilayah Cakupan");
            txtNamaHost.setVisibility(View.GONE);
            txtUsername.setVisibility(View.GONE);
            edtNamaHost.setVisibility(View.GONE);
            edtUsername.setVisibility(View.GONE);
        }
        else if (user.get("loginAs").equalsIgnoreCase("member")){
            genderList.add("Laki - Laki ");
            genderList.add("Perempuan");
            chkPrivasi.setVisibility(View.GONE);
            spinnerPengantaran.setVisibility(View.GONE);
            spinnerCOD.setVisibility(View.GONE);
            txtCOD.setVisibility(View.GONE);
            txtPengantaran.setVisibility(View.GONE);
            txtPrivasi.setVisibility(View.GONE);
        }

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genderList);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);

        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (user.get("loginAs").equalsIgnoreCase("host")){
                    gender = parent.getItemAtPosition(position).toString();
                }
                else if (user.get("loginAs").equalsIgnoreCase("member")) {
                    switch (position) {
                        case 0:
                            gender = "L";
                            break;
                        case 1:
                            gender = "P";
                            break;
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        viewProfile(id_user, email, loginAs);

        btnEditProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frameBuffer.setVisibility(View.VISIBLE);
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Edit Profile")
                        .build());

                editProfile(id_user, id_host, email, edtNama.getText().toString(), edtUsername.getText().toString(),
                        idKota, edtAlamat.getText().toString(), edtNoTelp.getText().toString(),
                        gender, edtKode.getText().toString(), pengantaran, privasi, cod);
            }
        });

        btnAkunBank.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent i = new Intent(ProfileActivity.this, AkunBankActivity.class);
                   startActivity(i);
               }
           }
        );

        btnGantiPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(ProfileActivity.this, R.style.MyDialogTheme);
                dialog.setContentView(R.layout.dialog_gantipass);
                dialog.setTitle("Ganti Password");

                final ShowHidePasswordEditText edtOldPass = (ShowHidePasswordEditText) dialog.findViewById(R.id.gantipass_old);
                final ShowHidePasswordEditText edtNewPass = (ShowHidePasswordEditText) dialog.findViewById(R.id.gantipass_new);
                final ShowHidePasswordEditText edtNewPass2 = (ShowHidePasswordEditText) dialog.findViewById(R.id.gantipass_new2);
                Button btnGantiPass = (Button) dialog.findViewById(R.id.confirm_gantipass);

                dialog.show();

                btnGantiPass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String oldpass = edtOldPass.getText().toString();
                        String newpass = edtNewPass.getText().toString();
                        String newpass2 = edtNewPass2.getText().toString();

                        if (newpass2.equals(newpass)) {
                            frameBuffer.setVisibility(View.VISIBLE);
                            gantiPass(id_user, email, oldpass, newpass, newpass2);
                        } else {
                            Toast.makeText(ProfileActivity.this, "Password Harus Sama", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }

            private void gantiPass(final String id_user, final String email, final String oldPass, final String newPass, final String newPass2) {
                String tag_string_req = "req_login";
                StringRequest strReq = new StringRequest(Request.Method.POST,
                        AppConfig.URL_LOGIN, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d("TAG", "Edit Pass Response: " + response.toString());
                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");
                            if (!error) {
                                String msg = jObj.getString("msg");
                                Toast.makeText(ProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
                                SharedPreferences sharedPreferences = getSharedPreferences("Preferences", 0);
                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                editor.putString("password", MD5(newPass));
                                editor.apply();
                                frameBuffer.setVisibility(View.INVISIBLE);
                                dialog.dismiss();
                            }
                            else {
                                String msg = jObj.getString("msg");
                                Toast.makeText(ProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
                                frameBuffer.setVisibility(View.INVISIBLE);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            frameBuffer.setVisibility(View.INVISIBLE);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAG", "StoreLIst Error: " + error.getMessage());
                        Toast.makeText(ProfileActivity.this,
                                error.getMessage(), Toast.LENGTH_LONG).show();
                        frameBuffer.setVisibility(View.INVISIBLE);
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        // Posting parameters to login url
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("tag", "editpassword");
                        params.put("id_user", id_user);
                        params.put("email", email);
                        params.put("oldpass", oldPass);
                        params.put("newpass", newPass);
                        params.put("renewpass", newPass2);

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

                strReq.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                // Adding request to request queue
                AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
            }
        });

    }

    private void viewProfile(final String id_user, final String email, final String loginAs) {

        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "Profile Response: " + response.toString());
                try {
                    if (response.equalsIgnoreCase("")) {
                    }
                    else {

                        JSONObject jsonObject = new JSONObject(response);

                        String kode = jsonObject.getString("kode");
                        String emailu = jsonObject.getString("email");
                        String alamat = jsonObject.getString("alamat");
                        String idKota = jsonObject.getString("id_kota");
                        String kota = jsonObject.getString("kota");
                        String nama = jsonObject.getString("nama");
                        if (user.get("loginAs").equalsIgnoreCase("host")){
                            String cakupan = jsonObject.getString("cakupan");
                            String antar = jsonObject.getString("antar");
                            String privasipr = jsonObject.getString("privasi");
                            String cod = jsonObject.getString("cod");
                            if (cakupan.equalsIgnoreCase("Perumahan")){
                                spinnerGender.setSelection(0);
                            }
                            else if (cakupan.equalsIgnoreCase("Tempat Kerja")){
                                spinnerGender.setSelection(1);
                            }
                            else if (cakupan.equalsIgnoreCase("Sekolah/Kampus")){
                                spinnerGender.setSelection(2);
                            }
                            if (antar.equalsIgnoreCase("Y")){
                                spinnerPengantaran.setSelection(0);
                            }
                            else if (antar.equalsIgnoreCase("N")){
                                spinnerPengantaran.setSelection(1);
                            }
                            if (cod.equalsIgnoreCase("1")){
                                spinnerCOD.setSelection(0);
                            }
                            else if (cod.equalsIgnoreCase("0")){
                                spinnerCOD.setSelection(1);
                            }

                            if (privasipr.equalsIgnoreCase("Y")){
                                chkPrivasi.setChecked(true);
                            }
                            else if (privasipr.equalsIgnoreCase("N")){
                                chkPrivasi.setChecked(false);
                            }

                            pengantaran = antar;
                            privasi = privasipr;
                        }
                        else if (user.get("loginAs").equalsIgnoreCase("member")){
                            String jnsKel = jsonObject.getString("jenis_kelamin");
                            String username = jsonObject.getString("username");
                            String namaHost = jsonObject.getString("nama_host");

                            edtUsername.setText(username);
                            edtNamaHost.setText(namaHost);
                            if (jnsKel.equalsIgnoreCase("P")){
                                spinnerGender.setSelection(1);
                            }
                            else if (jnsKel.equalsIgnoreCase("L")){
                                spinnerGender.setSelection(0);
                            }
                        }
                        String noTelp = jsonObject.getString("no_telp");

                        edtKode.setText(kode);
                        edtNama.setText(nama);
                        edtKota.setText(kota);
                        edtNoTelp.setText(noTelp);
                        edtEmail.setText(emailu);
                        edtAlamat.setText(alamat);
                        getIdKota(idKota);

                        frameLoading.setVisibility(View.INVISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "StoreLIst Error: " + error.getMessage());
                Toast.makeText(ProfileActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "viewprofile");
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

        strReq.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void editProfile(final String id_user,final String idHost, final String email, final String nama,
                             final String username, final String idKota, final String alamat, final String noTelp,
                             final String gender, final String kode, final String antar, final String privasi,
                             final String cod) {

        String tag_string_req = "req_login";
        Log.e("idUser", id_user+"-");
        Log.e("idHost", idHost+"-");
        Log.e("emai;l", email+"-");
        Log.e("nama", nama+"-");
        Log.e("username", username+"-");
        Log.e("idKota", idKota+"-");
        Log.e("alaamt", alamat+"-");
        Log.e("notelp", noTelp+"-");
        Log.e("gender", gender+"-");
        Log.e("kode", kode+"-");
        Log.e("antar", antar+"-");
        Log.e("privasi", privasi+"-");
                StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "Profile Response: " + response.toString());
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    if (!error){
                        frameBuffer.setVisibility(View.INVISIBLE);
                        Toast.makeText(ProfileActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else {
                        frameBuffer.setVisibility(View.INVISIBLE);
                        Toast.makeText(ProfileActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    frameBuffer.setVisibility(View.INVISIBLE);

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "StoreLIst Error: " + error.getMessage());
                Toast.makeText(ProfileActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "editprofil");
                params.put("id_user", id_user);
                params.put("email", email);
                params.put("nama", nama);
                params.put("username", username);
                params.put("kota", idKota);
                params.put("alamat", alamat);
                params.put("no_telp", noTelp);
                params.put("id_host", idHost);
                params.put("jenis_kelamin", gender);
                params.put("kode", kode);
                params.put("antar", antar);
                params.put("privasi", privasi);
                params.put("cod", cod);

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

        strReq.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void getIdKota(String idkota){
        this.idKota = idkota;
    }

    private void retry() {

        frameBuffer.setVisibility(View.INVISIBLE);
        new AlertDialog.Builder(this)
                .setTitle("Koneksi Bermasalah")
                .setMessage("Coba lagi")
                .setPositiveButton("Coba lagi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        frameBuffer.setVisibility(View.VISIBLE);
                        editProfile(id_user, id_host, email, edtNama.getText().toString(), edtUsername.getText().toString(),
                                idKota, edtAlamat.getText().toString(), edtNoTelp.getText().toString(),
                                gender, edtKode.getText().toString(), pengantaran, privasi, cod);

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

    public String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }
    private void sendScreenName() {
        String name = "Profile";

        // [START screen_view_hit]
        Log.i("ProfileActivity", "Screen name: " + name);
        mTracker.setScreenName("Screen" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // [END screen_view_hit]
    }

}
