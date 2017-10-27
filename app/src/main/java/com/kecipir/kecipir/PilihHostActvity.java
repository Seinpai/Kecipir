package com.kecipir.kecipir;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kecipir.kecipir.data.AppConfig;
import com.kecipir.kecipir.data.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PilihHostActvity extends AppCompatActivity {

    Spinner spinnerPilihKota;
    Spinner spinnerPilihHost;
    Button btnPilihLokasi, btnPilihLogin;
    List<String> kotaList, hostList;
    String id_kota, id_host;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilih_host_actvity);

        spinnerPilihKota = (Spinner) findViewById(R.id.spinner_pilihkota);
        spinnerPilihHost = (Spinner) findViewById(R.id.spinner_pilihhost);
        btnPilihLokasi = (Button) findViewById(R.id.btn_pilihlokasi);
        btnPilihLogin = (Button) findViewById(R.id.btn_pilihlogin);
        kotaList = new ArrayList<String>();
        hostList = new ArrayList<String>();

        getKota();

        btnPilihLokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("Preferences", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                Intent i = new Intent(PilihHostActvity.this, MainActivity.class);
                startActivity(i);
            }


        });

        btnPilihLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("Preferences", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                Intent i = new Intent(PilihHostActvity.this, MemberStartActivity.class);
                startActivity(i);
            }


        });
    }

    private void getKota() {
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "Kota List Response: " + response.toString());
                try {
                    if (response.equalsIgnoreCase("")) {

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

                        ArrayAdapter<String> kotaAdapter = new ArrayAdapter<String>(PilihHostActvity.this, android.R.layout.simple_spinner_item, kotaList);
                        kotaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerPilihKota.setAdapter(kotaAdapter);


                        spinnerPilihKota.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                for (int i = 0; i < length; i++) {
                                    if (position == i) {
                                        id_kota = kota[i];
                                        getHostKota(id_kota);
//                                                Toast.makeText(getActivity(), id_kota, Toast.LENGTH_SHORT).show();
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
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "StoreLIst Error: " + error.getMessage());
                Toast.makeText(PilihHostActvity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
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

    private void getHostKota(final String id_kota) {
        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "getHostKota Response: " + response.toString());
                try {
                    if (response.equalsIgnoreCase("")) {
                    } else {

                        JSONArray jArr = new JSONArray(response);

                        final int length = jArr.length();
                        hostList = new ArrayList<String>();

                        final String[] idhost = new String[length];
                        for (int i = 0; i < length; i++) {
                            JSONObject jObj = (JSONObject) jArr.get(i);
                            String idHost = jObj.getString("id_host");
                            String namaHost = jObj.getString("nama_host");
                            idhost[i] = idHost;
                            hostList.add(namaHost);

                        }

                        ArrayAdapter<String> hostAdapter = new ArrayAdapter<String>(PilihHostActvity.this, android.R.layout.simple_spinner_item, hostList);
                        hostAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerPilihHost.setAdapter(hostAdapter);


                        spinnerPilihHost.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                for (int i = 0; i < length; i++) {
                                    if (position == i) {
                                        id_host = idhost[i];
//                                                Toast.makeText(getActivity(), id_host, Toast.LENGTH_SHORT).show();
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
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "getTanggal Error: " + error.getMessage());
                Toast.makeText(PilihHostActvity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
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
}
