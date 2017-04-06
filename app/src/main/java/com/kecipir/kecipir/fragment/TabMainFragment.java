package com.kecipir.kecipir.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.kecipir.kecipir.DetailProdukActivity;
import com.kecipir.kecipir.MainActivity;
import com.kecipir.kecipir.MemberStartActivity;
import com.kecipir.kecipir.NavigationDrawerFragment;
import com.kecipir.kecipir.R;
import com.kecipir.kecipir.SessionManager;
import com.kecipir.kecipir.ShoppingCartActivity;
import com.kecipir.kecipir.adapter.StorelistAdapter;
import com.kecipir.kecipir.data.AppConfig;
import com.kecipir.kecipir.data.AppController;
import com.kecipir.kecipir.data.ClickListener;
import com.kecipir.kecipir.data.MenuDrawer;
import com.kecipir.kecipir.data.ModelClickListener;
import com.kecipir.kecipir.data.StoreList;
import com.kecipir.kecipir.data.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Albani on 10/20/2015.
 */
public class TabMainFragment extends Fragment implements ModelClickListener {

    private static final String ARG_POSITION = "position";
    private int position;

    TextView txtUser, txtAktif;
    RecyclerView recyclerView;
    StorelistAdapter storelistAdapter;
    HashMap<String, String> user;

    SessionManager sessionManager;

    List<StoreList> data;
    StoreList storeList;

    private int mNotificationCount;
    RelativeLayout relativeLayout, aktifLayout;
    String id;
    String tgl;
    String nama_petani;
    String quantity;
    String quantity_awal;
    String quantity_final;
    String foto;
    String grade;
    String satuan;
    String nama_barang;
    String harga_jual;
    String harga_promo;
    String harga_promorp;
    String harga_jualrp;
    boolean sale;

    public static TabMainFragment newInstance(int position) {
        TabMainFragment f = new TabMainFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tabmain, container, false);

        sessionManager = new SessionManager(getActivity());
        user = sessionManager.getUser();

        txtUser = (TextView) rootView.findViewById(R.id.txt_username);
        relativeLayout = (RelativeLayout) rootView.findViewById(R.id.frame_loading);
        aktifLayout = (RelativeLayout) rootView.findViewById(R.id.frame_aktif);
        txtAktif = (TextView) rootView.findViewById(R.id.txt_aktif);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_storelist);

        storelistAdapter = new StorelistAdapter(getActivity(), data);
        data = new ArrayList<>();

        if (sessionManager.isLoggedIn()){

            String tgl_panen = user.get("tglpanen");
            if (user.get("tglpanen")== null){
                tgl_panen = "";
            }
            checkAktif(position + "", user.get("id_host"),  tgl_panen);
        }
        else {
            parseBarangNonLogin(position + "");
        }
        storelistAdapter = new StorelistAdapter(getActivity(), data);
        storelistAdapter.setOnClickListener(this);
        Log.d("SET ADAPTER", "TRUE");

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);

        final MenuItem itemSearch = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(itemSearch);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                final List<StoreList> filteredModelList = filter(data, newText);
                storelistAdapter.setFilter(filteredModelList);
                storelistAdapter.setOnClickListener(TabMainFragment.this);
                return true;
            }
        });

        MenuItemCompat.setOnActionExpandListener(itemSearch,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        // Do something when collapsed
                        storelistAdapter.setFilter(data);
                        return true; // Return true to collapse action view
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        // Do something when expanded
                        return true; // Return true to expand action view
                    }
                });
    }

    private List<StoreList> filter(List<StoreList> models, String query) {
        query = query.toLowerCase();

        final List<StoreList> filteredModelList = new ArrayList<>();
        for (StoreList model : models) {
            final String text = model.getNama().toLowerCase();
            final String petani = model.getPetani().toLowerCase();

            if (text.contains(query)|| petani.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void checkAktif(final String position, final String id_host, final String tglpanen) {

        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "StoreList Response: " + response.toString());
                try {
                    if (response.equalsIgnoreCase("[]")) {
                        relativeLayout.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.INVISIBLE);
                        txtUser.setText("Mohon Maaf, Stock Sedang Kosong");
                    }
                    else {

                        JSONObject jsonObject = new JSONObject(response);

                        if(!jsonObject.getBoolean("error")){
                            aktifLayout.setVisibility(View.GONE);
                            parseBarang(position, id_host, tglpanen);
                        }
                        else{
                            aktifLayout.setVisibility(View.VISIBLE);
                            relativeLayout.setVisibility(View.INVISIBLE);
                            txtAktif.setText(jsonObject.getString("message"));
                        }
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
                Toast.makeText(getActivity(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                retry();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "check_aktif");
                params.put("id_host", id_host);

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
//        strReq.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    public void parseBarang(final String position, final String id_host, final String tglpanen) {

        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "StoreList Response: " + response.toString());
                try {
                    if (response.equalsIgnoreCase("[]")) {
                        relativeLayout.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.INVISIBLE);
                        txtUser.setText("Mohon Maaf, Stock Sedang Kosong");
                    }
                    else {

                        JSONArray jArr = new JSONArray(response);

                        int length = jArr.length();

                        storeList = new StoreList();
                        for (int i = 0; i < length; i++) {
                            JSONObject jObj = (JSONObject) jArr.get(i);
                            boolean error = jObj.getBoolean("error");
                            if (!error) {
                                id = jObj.getString("id_barang");
                                JSONObject detail = jObj.getJSONObject("detail_barang");
                                tgl = detail.getString("tgl");
                                nama_petani = detail.getString("nama_petani");
                                quantity = detail.getString("quantity");
                                quantity_awal = detail.getString("quantity_awal");
                                quantity_final = detail.getString("quantity_final");
                                foto = AppConfig.IMAGE_LINK+"" + detail.getString("foto");
                                grade = detail.getString("grade");
                                satuan = detail.getString("satuan");
                                nama_barang = detail.getString("nama_barang");
                                harga_jual = detail.getString("harga_jual");
                                harga_promo = detail.getString("harga_promo");
                                harga_promorp = detail.getString("harga_promorp");
                                harga_jualrp = detail.getString("harga_jualrp");

                                if (Integer.valueOf(harga_jual) > Integer.valueOf(harga_promo)){
                                    harga_jualrp = harga_promorp;
                                    sale = true;
                                }
                                else {
                                    sale = false;
                                }

                                storeList = new StoreList(id, nama_barang, harga_jualrp, tgl, foto, quantity_final, satuan, grade, nama_petani, sale);
                                data.add(storeList);
                            } else {
                                // Error in login. Get the error message
                                String errorMsg = jObj.getString("error_msg");
                                Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
                                relativeLayout.setVisibility(View.INVISIBLE);
                                retry();
                            }

                        }
                        relativeLayout.setVisibility(View.INVISIBLE);
                        txtUser.setVisibility(View.INVISIBLE);
                        recyclerView.setAdapter(storelistAdapter);
                        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));

                        Log.d("DATA ::", data.toString());
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
                Toast.makeText(getActivity(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                retry();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "storelist");
                params.put("position", position);
                params.put("id_host", id_host);
                params.put("tgl_panen", tglpanen);

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


        strReq.setRetryPolicy(new DefaultRetryPolicy(AppConfig.TIMEOUT_NETWORK, AppConfig.RETRY_NETWORK, AppConfig.MULTI_NETWORK));// Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void parseBarangNonLogin(final String position) {

        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "StoreList Response: " + response.toString());
                try {
                    if (response.equalsIgnoreCase("[]")) {
                        relativeLayout.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.INVISIBLE);
                        txtUser.setText("Mohon Maaf, Stock Sedang Kosong");
                    }
                    else {

                        JSONArray jArr = new JSONArray(response);

                        int length = jArr.length();

                        storeList = new StoreList();
                        for (int i = 0; i < length; i++) {
                            JSONObject jObj = (JSONObject) jArr.get(i);
                            boolean error = jObj.getBoolean("error");
                            if (!error) {
                                id = jObj.getString("id_barang");
                                JSONObject detail = jObj.getJSONObject("detail_barang");
                                tgl = detail.getString("tgl");
                                nama_petani = detail.getString("nama_petani");
                                quantity = detail.getString("quantity");
                                quantity_awal = detail.getString("quantity_awal");
                                quantity_final = detail.getString("quantity_final");
//                                foto = pref.getString("HOST", null)+"/images/images_product/" + detail.getString("foto");
                                foto = AppConfig.IMAGE_LINK+"" + detail.getString("foto");
                                grade = detail.getString("grade");
                                satuan = detail.getString("satuan");
                                nama_barang = detail.getString("nama_barang");
                                harga_jual = detail.getString("harga_jual");
                                harga_promo = detail.getString("harga_promo");
                                harga_promorp = detail.getString("harga_promorp");
                                harga_jualrp = detail.getString("harga_jualrp");

                                if (Integer.valueOf(harga_jual) > Integer.valueOf(harga_promo)){
                                    harga_jualrp = harga_promorp;
                                    sale = true;
                                }
                                else {
                                    sale = false;
                                }

                                storeList = new StoreList(id, nama_barang, harga_jualrp, tgl, foto, quantity_final, satuan, grade, nama_petani, sale);
                                data.add(storeList);
                                txtUser.setVisibility(View.INVISIBLE);
                            } else {
                                // Error in login. Get the error message
                                String errorMsg = jObj.getString("error_msg");
                                Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
                                retry();
                            }

                        }

                        txtUser.setVisibility(View.INVISIBLE);
                        relativeLayout.setVisibility(View.INVISIBLE);
                        recyclerView.setAdapter(storelistAdapter);
                        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));

                        Log.d("DATA ::", data.toString());
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
                Toast.makeText(getActivity(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                retry();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "storelistnonloggin");
                params.put("position", position);

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
    public void itemClicked(List<StoreList> model, View view, int position) {
        Intent i = new Intent(getActivity(), DetailProdukActivity.class);
        String id = model.get(position).getId();
        String nama = model.get(position).getNama();
        i.putExtra("id_barang", id);
        i.putExtra("nama_produk", nama);
        startActivity(i);
    }

    private void retry() {

        relativeLayout.setVisibility(View.INVISIBLE);
        new AlertDialog.Builder(getActivity())
                .setTitle("Koneksi Bermasalah Saat parder barang")
                .setMessage("Coba lagi")
                .setPositiveButton("Coba lagi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (sessionManager.isLoggedIn()){

                            String tgl_panen = user.get("tglpanen");
                            if (user.get("tglpanen")== null){
                                tgl_panen = "";
                            }
                            parseBarang(position + "", user.get("id_host"),  tgl_panen);
                        }
                        else {
                            parseBarangNonLogin(position + "");
                        }
                    }
                })
                .setNegativeButton("batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getActivity().finish();
                    }
                })
                .show();
    }

}
