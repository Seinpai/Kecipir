package com.kecipir.kecipir;

import android.*;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
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
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kecipir.kecipir.data.AppConfig;
import com.kecipir.kecipir.data.AppController;
import com.scottyab.showhidepasswordedittext.ShowHidePasswordEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DaftarMemberActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText edtNama, edtEmail;
    TextView txtSyarat;
    ShowHidePasswordEditText edtPass, edtPass2;
    Button btnRegMember;
    CheckBox chkRegMember;
    Spinner spinnerRegMember;
    Spinner spinnerRegKota;
    SupportMapFragment mapFragment;

    String nama, email, pass, pass2, hostId;
    String tmp_kode_kota;
    List<String> hostList;

    GoogleMap map;
    MarkerOptions markerOptions;

    Marker marker;

    RelativeLayout frameLoading, frameBuffer;

    Dialog dialog;

    UiSettings uiSettings;
    SessionManager session;

    Spinner spinnerTglPanen, spinnerKota, spinnerHost;
    List<String> tglPanenList, kotaList;
    String tglpanen, id_kota, id_host;

    private Tracker mTracker;

    private GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    final int PERMISSION_ACCESS_FINE_LOCATION = 1;
    WebView wv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_member);
        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        session = new SessionManager(this);

        edtNama = (EditText) findViewById(R.id.edt_regmember_nama);
        edtEmail = (EditText) findViewById(R.id.edt_regmember_email);
        edtPass = (ShowHidePasswordEditText) findViewById(R.id.edt_regmember_password);
        edtPass2 = (ShowHidePasswordEditText) findViewById(R.id.edt_regmember_password2);
        spinnerKota = (Spinner) findViewById(R.id.spinner_regkota);
        spinnerRegMember = (Spinner) findViewById(R.id.spinner_regmember);
        btnRegMember = (Button) findViewById(R.id.btn_regmember);
        chkRegMember = (CheckBox) findViewById(R.id.chk_regmember);
        txtSyarat = (TextView) findViewById(R.id.txt_syarat_ketentuan);

        frameLoading = (RelativeLayout) findViewById(R.id.frame_loading);
        frameBuffer = (RelativeLayout) findViewById(R.id.frame_buffer);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_regmember);

        AppController appController = (AppController) getApplication();
        mTracker = appController.getDefaultTracker();
        sendScreenName();

        SpannableString ss = new SpannableString("Saya Setuju dengan Syarat dan Ketentuan Berlaku");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                dialog = new Dialog(DaftarMemberActivity.this, R.style.MyDialogTheme);
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


                loadTerms("member");
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
        };
        ss.setSpan(clickableSpan, 19, 39, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        ss.setSpan(new ForegroundColorSpan(Color.GREEN), 19, 39, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new UnderlineSpan(), 19, 39, 0);

        txtSyarat.setText(ss);
        txtSyarat.setMovementMethod(LinkMovementMethod.getInstance());
//        txtSyarat.setHighlightColor(Color.BLACK);


        Log.i("MSG : ", "ID HOST : "+id_host+" ID KOTA : "+ id_kota);



        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap gmap) {
//                uiSettings.setZoomControlsEnabled(true);
                uiSettings = gmap.getUiSettings();
                map = gmap;


                gmap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {
                        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                        boolean gps_enabled = false;
                        boolean network_enabled = false;

                        try {
                            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                        } catch(Exception ex) {}

                        try {
                            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                        } catch(Exception ex) {}

                        if(!gps_enabled && !network_enabled) {
                            // notify user
                            AlertDialog.Builder dialog = new AlertDialog.Builder(DaftarMemberActivity.this);
                            dialog.setTitle("Aktifkan GPS");
                            dialog.setMessage("Kondisi GPS tidak aktif");
                            dialog.setPositiveButton("Aktifkan", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                    // TODO Auto-generated method stub
                                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(myIntent);
                                    //get gps
                                }
                            });
                            dialog.setNegativeButton("Batal", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                    // TODO Auto-generated method stub
                                    Toast.makeText(DaftarMemberActivity.this, ", Diperlukan untuk mendapatkan Lokasi anda", Toast.LENGTH_SHORT).show();
                                }
                            });
                            dialog.show();
                            return  true;
                        }
                        else {
                            return false;
                        }
                    }
                });

                if (ContextCompat.checkSelfPermission(DaftarMemberActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DaftarMemberActivity.this, new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION },
                            PERMISSION_ACCESS_FINE_LOCATION);
                }
                else {
                    gmap.setMyLocationEnabled(true);

//            map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
//                @Override
//                public void onMyLocationChange(Location location) {
//                    LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
//                    double longitude = location.getLongitude();
//                    double latitude = location.getLatitude();
//
////                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
//                   map.animateCamera(CameraUpdateFactory.newCameraPosition(
//                    new CameraPosition.Builder().
//                            target(loc).
//                            zoom(12.0f).
//                            tilt(45).
//                            build()));
//
//
//                }
//            });


                }


                hostList = new ArrayList<>();

//                getListHost();

                kotaList = new ArrayList<String>();
                getKota();
            }
        });

        frameLoading.setVisibility(View.INVISIBLE);

        chkRegMember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){

                }
            }


        });
        btnRegMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                nama = edtNama.getText().toString();
                email = edtEmail.getText().toString();
                pass = edtPass.getText().toString();
                pass2 = edtPass2.getText().toString();
                if (chkRegMember.isChecked()){
//                    Toast.makeText(DaftarMemberActivity.this, "CHECKED", Toast.LENGTH_SHORT).show();
                    if (nama.equalsIgnoreCase("")||email.equalsIgnoreCase("")|| pass.equalsIgnoreCase("")||pass2.equalsIgnoreCase("")){
                        Toast.makeText(DaftarMemberActivity.this, "Kolom tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    }
                    else if(!isValidEmail(email)){
                        Toast.makeText(DaftarMemberActivity.this, "Email tidak valid", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if (pass.equals(pass2)){
                            frameBuffer.setVisibility(View.VISIBLE);
                            mTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("Action")
                                    .setAction("Daftar member")
                                    .build());

                            daftarMember(hostId, nama, email, pass, pass2);
//                            Toast.makeText(DaftarMemberActivity.this, "DO DAFTAR", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(DaftarMemberActivity.this, "Password harus sama", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else {
                    Toast.makeText(DaftarMemberActivity.this, "Anda harus menyetujui Syarat dan Ketentuan yang Berlaku", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


//
//    private void getKota() {
//        String tag_string_req = "req_login";
//
//        StringRequest strReq = new StringRequest(Request.Method.POST,
//                AppConfig.URL_LOGIN, new Response.Listener<String>() {
//
//            @Override
//            public void onResponse(String response) {
//                Log.d("TAG", "Kota List Response: " + response.toString());
//                try {
//                    if (response.equalsIgnoreCase("")) {
//
//                    } else {
//
//                        JSONArray jArr = new JSONArray(response);
//
//                        final int length = jArr.length();
//
//                        final String[] kota = new String[length];
//                        for (int i = 0; i < length; i++) {
//                            JSONObject jObj = (JSONObject) jArr.get(i);
//                            String idKota = jObj.getString("id_kota");
//                            String namaKota = jObj.getString("kota");
//                            kota[i] = idKota;
//                            kotaList.add(namaKota);
//
//                        }
//
//                        ArrayAdapter<String> kotaAdapter = new ArrayAdapter<String>(DaftarMemberActivity.this, android.R.layout.simple_spinner_item, kotaList);
//                        kotaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                        spinnerKota.setAdapter(kotaAdapter);
//
//                        spinnerKota.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                            @Override
//                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                                for (int i = 0; i < length; i++) {
//                                    if (position == i) {
//                                        id_kota = kota[i];
//                                        getListHost("06");
////                                                Toast.makeText(getActivity(), id_kota, Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            }
//
//                            @Override
//                            public void onNothingSelected(AdapterView<?> parent) {
//
//                            }
//                        });
//
//
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    retry();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("TAG", "StoreLIst Error: " + error.getMessage());
//                Toast.makeText(DaftarMemberActivity.this,
//                        error.getMessage(), Toast.LENGTH_LONG).show();
//                retry();
//            }
//        }) {
//
//            @Override
//            protected Map<String, String> getParams() {
//                // Posting parameters to login url
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("tag", "getCmbKota");
//
//                return params;
//            }
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> params = new HashMap<String, String>();
//                String creds = String.format("%s:%s", "green", "web-indonesia");
//                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
//                params.put("Authorization", auth);
//                return params;
//            }
//        };
//
//        strReq.setRetryPolicy(new DefaultRetryPolicy(AppConfig.TIMEOUT_NETWORK, AppConfig.RETRY_NETWORK, AppConfig.MULTI_NETWORK));
//        // Adding request to request queue
//        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
//    }



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
                            tmp_kode_kota = idKota;
                            kota[i] = idKota;
                            kotaList.add(namaKota);
                        }

                        ArrayAdapter<String> tglPanenAdapter = new ArrayAdapter<String>(DaftarMemberActivity.this, android.R.layout.simple_spinner_item, kotaList);
                        tglPanenAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerKota.setAdapter(tglPanenAdapter);
                        frameLoading.setVisibility(View.INVISIBLE);

                        spinnerKota.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                for (int i = 0; i < length; i++) {
                                    if (position == i) {
                                        tmp_kode_kota = kota[i];
//                                        Toast.makeText(DaftarMemberActivity.this, tmp_kode_kota, Toast.LENGTH_SHORT).show();
                                        getListHost(tmp_kode_kota);
                                    }
                                }
//                                kecamatanList.clear();
//                                getKecamatan();
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
                Toast.makeText(DaftarMemberActivity.this,
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




    private void getListHost(final String id_kota) {
        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "LIstHost Response: " + response.toString());
                try {
                    if (response.equalsIgnoreCase("")) {
                        frameLoading.setVisibility(View.INVISIBLE);
                    } else {

                        JSONArray jArr = new JSONArray(response);

                        final int length = jArr.length();

                        final String[] idHost = new String[length];
                        final String[] areaHost = new String[length];
                        final String[] lati = new String[length];
                        final String[] langi = new String[length];
                        hostList = new ArrayList<>();
                        for (int i = 0; i < length; i++) {
                            JSONObject jObj = (JSONObject) jArr.get(i);
                            final String id_host = jObj.getString("id_host");
                            String namaHost = jObj.getString("nama_host");
                            String areaLayanan = jObj.getString("area_layanan");
                            String lat = jObj.getString("lat");
                            String lng = jObj.getString("lng");

                            idHost[i] = id_host;
                            areaHost[i] = areaLayanan;
                            lati[i] = lat;
                            langi[i] = lng;

                            hostList.add(namaHost+" - "+areaLayanan);

                            markerOptions= new MarkerOptions()
                                    .title(i+"-"+namaHost+" - "+areaLayanan)
                                    .position(new LatLng(Double.parseDouble(lati[i]),Double.parseDouble(langi[i])))
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                    ;

                            map.addMarker(markerOptions);
                        }


//                        map.getUiSettings();
//                        map.setMyLocationEnabled(true);
                        ArrayAdapter<String> HostListAdapter = new ArrayAdapter<String>(DaftarMemberActivity.this, android.R.layout.simple_spinner_item, hostList);
                        HostListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerRegMember.setAdapter(HostListAdapter);

                        frameLoading.setVisibility(View.INVISIBLE);

                        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker) {
                                    String pos = marker.getTitle().substring(0, marker.getTitle().indexOf("-"));
                                    marker.showInfoWindow();
                                    spinnerRegMember.setSelection(Integer.parseInt(pos));
                                    mTracker.send(new HitBuilders.EventBuilder()
                                            .setCategory("Action")
                                            .setAction("Click Host di Map")
                                            .build());

                                    return false;
                                }
                            });

                        spinnerRegMember.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                for (int i = 0; i < length; i++) {

                                    if (position == i) {
                                        hostId = idHost[i];
//                                        Toast.makeText(DaftarMemberActivity.this, hostId, Toast.LENGTH_SHORT).show();
                                        LatLng currentLoc = new LatLng(Double.parseDouble(lati[i]), Double.parseDouble(langi[i]));
                                        map.moveCamera(CameraUpdateFactory.newLatLng(currentLoc));
//                                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 12.0f));
                                        map.animateCamera(CameraUpdateFactory.newCameraPosition(
                                                new CameraPosition.Builder().
                                                        target(currentLoc).
                                                        zoom(13.0f).
                                                        tilt(20).
                                                        build()));

                                        mTracker.send(new HitBuilders.EventBuilder()
                                                .setCategory("Action")
                                                .setAction("Klik Host di Select Box")
                                                .build());


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
                    frameLoading.setVisibility(View.INVISIBLE);
                    retry();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "StoreLIst Error: " + error.getMessage());
                Toast.makeText(DaftarMemberActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
                retry();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "combohostkota");
                params.put("id_kota", id_kota);


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

    private void daftarMember(final String id_host, final String nama, final String email, final String pass, final String pass2) {

        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_DAFTARMEMBER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "Daftar Response: " + response.toString());
                try {
                    if (response.equalsIgnoreCase("")) {
//                        frameLoading.setVisibility(View.INVISIBLE);
                        frameBuffer.setVisibility(View.INVISIBLE);
                    }
                    else {

                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("error")){
                            frameBuffer.setVisibility(View.INVISIBLE);
                            Toast.makeText(DaftarMemberActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                        else {
                            JSONObject user = jsonObject.getJSONObject("user");


                            session.setLogin(true, jsonObject.getString("id"), user.getString("tabel"),user.getString("username"), user.getString("email"), MD5(pass), user.getString("id_host"), "member", jsonObject.getString("tgl_panen"));
                            // Launch main activity
                            daftarFinish();

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    frameBuffer.setVisibility(View.INVISIBLE);
                    retryDaftarMember();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "View Checkout Error: " + error.getMessage());
                Toast.makeText(DaftarMemberActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
                retryDaftarMember();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                params.put("tag", "daftarmember");
                params.put("id_host", id_host);
                params.put("email", email);
                params.put("nama", nama);
                params.put("password", pass);
                params.put("repassword", pass2);

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

        frameBuffer.setVisibility(View.INVISIBLE);
        new AlertDialog.Builder(this)
                .setTitle("Daftar Berhasil")
                .setMessage("Terima Kasih telah mendaftar")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(DaftarMemberActivity.this,
                                MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                })
                .show();
    }

    private void retry() {

        frameLoading.setVisibility(View.VISIBLE);
        new AlertDialog.Builder(this)
                .setTitle("Koneksi Bermasalah")
                .setMessage("Coba lagi")
                .setPositiveButton("Coba lagi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getListHost(id_kota);
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

    private void retryDaftarMember() {


        new AlertDialog.Builder(this)
                .setTitle("Daftar Member Koneksi Bermasalah ")
                .setMessage("Coba lagi")
                .setPositiveButton("Coba lagi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        frameBuffer.setVisibility(View.VISIBLE);
                        daftarMember(hostId, nama, email, pass, pass2);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

//                    map.setMyLocationEnabled(true);

                } else {

                }

                break;
        }
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
        String name = "Daftar Member";

        // [START screen_view_hit]
        Log.i("DaftarMember", "Screen name: " + name);
        mTracker.setScreenName("Screen" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // [END screen_view_hit]
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

}
