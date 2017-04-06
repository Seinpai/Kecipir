package com.kecipir.kecipir;

import io.smooch.core.User;
import io.smooch.ui.ConversationActivity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.kecipir.kecipir.adapter.DrawerAdapter;
import com.kecipir.kecipir.adapter.NavigationDrawerAdapter;
import com.kecipir.kecipir.data.AppConfig;
import com.kecipir.kecipir.data.AppController;
import com.kecipir.kecipir.data.ClickListener;
import com.kecipir.kecipir.data.MenuDrawer;
import com.kecipir.kecipir.data.NavigationDrawerGenerator;
import com.kecipir.kecipir.data.ParentDrawer;
import com.kecipir.kecipir.fragment.TabMainFragment;
import com.thoughtbot.expandablerecyclerview.listeners.OnGroupClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Albani on 10/19/2015.
 */
public class NavigationDrawerFragment extends Fragment {

    RecyclerView recyclerView;
    DrawerAdapter drawerAdapter;
    TextView txtHost, txtTglPanen, txtUser, txtEmail, txtTglOrder;
    ImageView imgTglPanen, imgHost;
    LinearLayout relativeLayout;
    Spinner spinnerTglPanen, spinnerKota, spinnerHost;

    String namaUser, emailUser, idUser, loginAs, tgl_value;
    String tglpanen, id_kota, id_host;

    List<String> tglPanenList, kotaList, hostList;

    private static String PREF_NAME = "DrawerPref";
    private static String KEY_DRAWER = "key_drawer";

    private ActionBarDrawerToggle actionBarDrawerToggle;
    DrawerLayout mDrawerLayout;
    private View conteinerView;

    Context context;

    private boolean mUserLearnedDrawer, mFromSavedInstate;

    SessionManager sessionManager;HashMap<String, String> user;
    Dialog dialog, dialogHost;
    List<ParentDrawer> dataDrawer;

    int payment;


    GoogleApiClient mGoogleApiClient;
    ClickListener clickListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(getActivity());
        user = sessionManager.getUser();
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext())
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {

                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API)

                .build();

        if (!mGoogleApiClient.isConnected()){
            mGoogleApiClient.connect();
        }
        mUserLearnedDrawer = Boolean.valueOf(readfromPreference(getActivity(), KEY_DRAWER, "true"));
        if (savedInstanceState != null) {
            mUserLearnedDrawer = false;
        }

        clickListener = new ClickListener() {
            @Override
            public void itemClicked(View view, int position) {
                if (sessionManager.isLoggedIn()){
                    if (position==0){
                        Intent i = new Intent(getActivity(), ProfileActivity.class);
                        startActivity(i);
                    }
                    if (position==1){
                        //TODO Pembayaran
                        Intent i = new Intent(getActivity(), NotificationActivity.class);
                        startActivity(i);
                    }
                    if (position==2){
                        //TODO Pembayaran
                        Intent i = new Intent(getActivity(), PembayaranActivity.class);
                        startActivity(i);
                    }
                    if (loginAs.equalsIgnoreCase("member")){

                        if (position==3){
                            Intent i = new Intent(getActivity(), HistoryActivity.class);
                            startActivity(i);
                        }
                        if (position==4){
                            Intent i = new Intent(getActivity(), ListDepositActivity.class);
                            startActivity(i);
                        }
                        if (position==5){
                            ConversationActivity.show(getActivity());
                            User.getCurrentUser().setFirstName(namaUser);
                            User.getCurrentUser().setEmail(emailUser);

                        }
                        if (position==6){
                            Intent i = new Intent(getActivity(), NotificationDetailActivity.class);
                            i.putExtra("url", "http://blog.kecipir.com");
                            i.putExtra("title", "Blog");
                            startActivity(i);
                        }
                        if(position==7){
                            Intent i = new Intent(getActivity(), AboutUsActivity.class);
                            startActivity(i);
                        }
                        if(position==8){
                            if (mGoogleApiClient.isConnected()){
                                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                                        new ResultCallback<Status>() {
                                            @Override
                                            public void onResult(Status status) {
                                                // ...
                                            }
                                        });

                            }
                            sessionManager.logoutUser();
                        }
                    }
                    else if(loginAs.equalsIgnoreCase("host")){

                        if (position==3){
                            Intent i = new Intent(getActivity(), BayarMemberActivity.class);
                            startActivity(i);
                        }
                        if (position==4){
                            Intent i = new Intent(getActivity(), HistoryActivity.class);
                            startActivity(i);
                        }
                        if (position==5){
                            Intent i = new Intent(getActivity(), ListDepositActivity.class);
                            startActivity(i);
                        }
                        if (position==6){
                            //TODO Komisi
                            Intent i = new Intent(getActivity(), KomisiActivity.class);
                            startActivity(i);
                        }
                        if (position==7){
                            Intent i = new Intent(getActivity(), MemberAgenActivity.class);
                            startActivity(i);
                        }
                        if (position==8){
                            ConversationActivity.show(getActivity());
                            User.getCurrentUser().setFirstName(namaUser);
                            User.getCurrentUser().setEmail(emailUser);

                        }
                        if (position==9){
                            Intent i = new Intent(getActivity(), NotificationDetailActivity.class);
                            i.putExtra("url", "http://blog.kecipir.com");
                            i.putExtra("title", "Blog");
                            startActivity(i);
                        }
                        if(position==10){
                            Intent i = new Intent(getActivity(), AboutUsActivity.class);
                            startActivity(i);
                        }
                        if(position==11){
                            if (mGoogleApiClient.isConnected()){
                                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                                        new ResultCallback<Status>() {
                                            @Override
                                            public void onResult(Status status) {
                                                // ...
                                            }
                                        });

                            }

                            sessionManager.logoutUser();
                        }
                    }

                }
                else {
                    if(position==0){
                        Intent i = new Intent(getActivity(), MemberStartActivity.class);
                        startActivity(i);
                    }
                    if (position==1){
                        ConversationActivity.show(getActivity());
                        User.getCurrentUser().setFirstName(namaUser);
                        User.getCurrentUser().setEmail(emailUser);

                    }
                    if(position==2){

                        Intent i = new Intent(getActivity(), NotificationDetailActivity.class);
                        i.putExtra("url", "http://blog.kecipir.com");
                        i.putExtra("title", "Blog");
                        startActivity(i);
                    }
                    if(position==3){
                        Intent i = new Intent(getActivity(), AboutUsActivity.class);
                        startActivity(i);
                    }
                }
            }
        };

    }

    public NavigationDrawerFragment() {

    }
//
//    public static List<MenuDrawer> getDataLogged() {
//        List<MenuDrawer> data = new ArrayList<>();
//
//        for (int i = 0; i < titles.length; i++) {
//            MenuDrawer current = new MenuDrawer();
//            current.title = titles[i];
//            current.badge = badge[i];
//            data.add(current);
//        }
//        return data;
//    }

    public static List<MenuDrawer> getData(boolean isLoggedin, String loginAs, int payment, int codmember) {
        List<MenuDrawer> data = new ArrayList<>();

        String[] titles = {""};
        int[] badge = {0};
        int[] icon = {0};
        if(isLoggedin){
            if (loginAs.equalsIgnoreCase("member"))
            {
                icon = new int[]{R.drawable.ic_account_menu, R.drawable.ic_berita_menu, R.drawable.ic_greencash_menu, R.drawable.ic_history_menu, R.drawable.ic_wallet_menu, R.drawable.ic_chat_menu, R.drawable.ic_blog_menu, R.drawable.ic_info_menu, R.drawable.ic_logout_menu};
                titles = new String[]{"Akun Saya", "Berita", "Pembayaran", "Riwayat Pembelian","Green Cash", "Kecipir Chat","Blog","Tentang Kami", "Log Out"};
                badge = new int[]{0,0,payment,0,0,0,0,0,0};
            }
            else if (loginAs.equalsIgnoreCase("host")){
                icon = new int[]{R.drawable.ic_account_menu, R.drawable.ic_berita_menu, R.drawable.ic_greencash_menu, R.drawable.ic_cod_member_menu, R.drawable.ic_history_menu, R.drawable.ic_wallet_menu, R.drawable.ic_komisi_menu, R.drawable.ic_member_menu, R.drawable.ic_chat_menu, R.drawable.ic_blog_menu, R.drawable.ic_info_menu, R.drawable.ic_logout_menu};
                titles = new String[]{"Akun Saya", "Berita", "Pembayaran", "Pembayaran Member", "Riwayat Pembelian","Green Cash","Komisi", "Member Saya", "Kecipir Chat", "Blog","Tentang Kami", "Log Out"};
                badge = new int[]{0,0,payment,codmember,0,0,0,0,0,0,0,0};
            }
        }
        else{
            icon = new int[]{R.drawable.ic_login_menu, R.drawable.ic_chat_menu, R.drawable.ic_blog_menu, R.drawable.ic_info_menu};
            titles = new String[]{"Login/Sign Up", "Kecipir Chat", "Blog","Tentang Kami"};
            badge = new int[]{0,0,0,0};
        }

        for (int i = 0; i < titles.length; i++) {
            MenuDrawer current = new MenuDrawer();
            current.title = titles[i];
            current.badge = badge[i];
            current.icon = icon[i];
            data.add(current);
        }
        return data;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        txtUser = (TextView) view.findViewById(R.id.namaUser);
        txtEmail = (TextView) view.findViewById(R.id.emailUser);
        txtHost = (TextView) view.findViewById(R.id.txt_hostUser);
        txtTglOrder = (TextView) view.findViewById(R.id.txt_tglorder);
        txtTglPanen = (TextView) view.findViewById(R.id.txt_tglpanen);
        imgTglPanen = (ImageView) view.findViewById(R.id.img_tglpanen);
        imgHost = (ImageView) view.findViewById(R.id.img_host);
        relativeLayout = (LinearLayout) view.findViewById(R.id.layout_login);

        namaUser = user.get("username");
        emailUser = user.get("email");
        idUser = user.get("uid");
        loginAs = user.get("loginAs");

        User.getCurrentUser().setFirstName(namaUser);
        User.getCurrentUser().setEmail(emailUser);

        tgl_value = user.get("tglpanen");

        txtUser.setText(namaUser);
        txtEmail.setText(emailUser);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_nav);
        if (sessionManager.isLoggedIn()){
            if (tgl_value == null){
                tgl_value = "";
            }
            if (loginAs.equalsIgnoreCase("host")){
                imgHost.setVisibility(View.GONE);
            }
            parseMemberHost(emailUser, idUser, loginAs, tgl_value);
        }
        else {
            drawerAdapter = new DrawerAdapter(getActivity(), getData(false, loginAs, 0, 0));
//            drawerAdapter = new DrawerAdapter(NavigationDrawerGenerator.makeParentMember());
            relativeLayout.setVisibility(View.GONE);
            drawerAdapter.setOnClickListener(clickListener);
            recyclerView.setAdapter(drawerAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }

        imgTglPanen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(getActivity(), R.style.MyDialogTheme);
                dialog.setContentView(R.layout.dialog_tglpanen);
                dialog.setTitle("Tanggal Panen");

                spinnerTglPanen = (Spinner) dialog.findViewById(R.id.spiner_tglpanen);
                Button btnGantiPanen = (Button) dialog.findViewById(R.id.btn_gantitglpanen);
                tglPanenList = new ArrayList<String>();

                getTanggalPanen(idUser, emailUser);

                btnGantiPanen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Preferences", 0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

//                        Toast.makeText(getActivity(), tglpanen, Toast.LENGTH_SHORT).show();
//                        dialog.dismiss();
                        editor.putString("tglpanen" , tglpanen);
                        editor.commit();

                        dialog.dismiss();
                        Intent i = new Intent(getActivity(), MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                });

            }

            private void getTanggalPanen(final String id, final String email) {
                String tag_string_req = "req_login";

                final SharedPreferences pref = getActivity().getSharedPreferences("TESPREF", Context.MODE_PRIVATE);
                StringRequest strReq = new StringRequest(Request.Method.POST,
                        AppConfig.URL_LOGIN, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d("TAG", "getTanggal Response: " + response.toString());
                        try {
                            if (response.equalsIgnoreCase("")) {
                            } else {

                                JSONArray jArr = new JSONArray(response);

                                final int length = jArr.length();

                                final String[] tgl = new String[length];
                                for (int i = 0; i < length; i++) {
                                    JSONObject jObj = (JSONObject) jArr.get(i);
                                    String tglValue = jObj.getString("tgl_value");
                                    String tglPanen = jObj.getString("tgl_panen");
                                    tgl[i] = tglValue;
                                    tglPanenList.add(tglPanen);

                                }

                                ArrayAdapter<String> tglPanenAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, tglPanenList);
                                tglPanenAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinnerTglPanen.setAdapter(tglPanenAdapter);

                                dialog.show();

                                spinnerTglPanen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        for (int i = 0; i < length; i++) {
                                            if (position == i) {
                                                tglpanen = tgl[i];
//                                                Toast.makeText(getActivity(), tglpanen, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(),
                                error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        // Posting parameters to login url
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("tag", "getTglpanen");
                        params.put("id", id);
                        params.put("email", email);

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


        imgHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogHost = new Dialog(getActivity(), R.style.MyDialogTheme);
                dialogHost.setContentView(R.layout.dialog_gantihost);
                dialogHost.setTitle("Ganti Agen");

                spinnerKota = (Spinner) dialogHost.findViewById(R.id.spiner_gantikota);
                spinnerHost = (Spinner) dialogHost.findViewById(R.id.spiner_gantihost);
                Button btnHost = (Button) dialogHost.findViewById(R.id.btn_gantihost);

                kotaList = new ArrayList<String>();
                hostList = new ArrayList<String>();

                getKota();
//
                btnHost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Preferences", 0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        String oldHost = sharedPreferences.getString("id_host", null);

//                        Toast.makeText(getActivity(), tglpanen, Toast.LENGTH_SHORT).show();
//                        dialog.dismiss();
                        gantiHost(idUser, emailUser, oldHost, id_kota, id_host);

                    }


                });


            }

            private void gantiHost(final String id_user, final String email, final String oldHost, final String new_kota, final String new_host){
                String tag_string_req = "req_login";
                StringRequest strReq = new StringRequest(Request.Method.POST,
                        AppConfig.URL_LOGIN, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d("TAG", "Ganti Host Response: " + response.toString());
                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");
                            if (!error) {
                                String msg = jObj.getString("msg");
                                String tgl_panen = jObj.getString("tgl_panen");
//                                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Preferences", 0);
                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                editor.putString("id_host",new_host);
                                editor.putString("tglpanen",tgl_panen);
                                editor.commit();
//                                frameBuffer.setVisibility(View.INVISIBLE);
                                dialogHost.dismiss();
//
                                Intent i = new Intent(getActivity(), MainActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                            }
                            else {
                                String msg = jObj.getString("msg");
                                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
//                                frameBuffer.setVisibility(View.INVISIBLE);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAG", "gantiHost Error: " + error.getMessage());
                        Toast.makeText(getActivity(),
                                error.getMessage(), Toast.LENGTH_LONG).show();
//                        frameBuffer.setVisibility(View.INVISIBLE);
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        // Posting parameters to login url
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("tag", "ganti_host");
                        params.put("id_user", id_user);
                        params.put("email", email);
                        params.put("old_host", oldHost);
                        params.put("new_host", new_host);
                        params.put("new_kota", new_kota);

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

                                ArrayAdapter<String> kotaAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, kotaList);
                                kotaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinnerKota.setAdapter(kotaAdapter);
                                dialogHost.show();
//                                frameLoading.setVisibility(View.INVISIBLE);


                                spinnerKota.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

                                ArrayAdapter<String> hostAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, hostList);
                                hostAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinnerHost.setAdapter(hostAdapter);


                                spinnerHost.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                        Toast.makeText(getActivity(),
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
        });
        return view;
    }

    public void setUp(int fragmentid, DrawerLayout drawerLayout, final Toolbar toolbar) {
        conteinerView = getActivity().findViewById(fragmentid);
        mDrawerLayout = drawerLayout;
        actionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                if (!mUserLearnedDrawer) {
                    mUserLearnedDrawer = true;
                    savetoPreferences(getActivity(), PREF_NAME, mUserLearnedDrawer + "");
                }
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

        };

        if (!mUserLearnedDrawer && !mFromSavedInstate) {
            mDrawerLayout.openDrawer(conteinerView);
        }

        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                actionBarDrawerToggle.syncState();
            }
        });

    }

    public static void savetoPreferences(Context context, String preferenceName, String preferenceValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceName, preferenceValue);

        editor.apply();
    }

    public static String readfromPreference(Context context, String preferenceName, String preferenceValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, context.MODE_PRIVATE);
        return sharedPreferences.getString(preferenceName, preferenceValue);
    }

    public void parseMemberHost(final String email, final String id, final String loginAs, final String tgl_value) {

        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "Drawer Response: " + response.toString());
                try {

                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
//                    // Check for error node in json
                    if (!error) {

                        if (tgl_value.equals("")) {
                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Preferences", 0);
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            editor.putString("tglpanen", jObj.getString("tgl_value"));
                            editor.apply();
                        }
                        else {

                        }

//                        dataDrawer = new ArrayList<>();
//                        dataDrawer = NavigationDrawerGenerator.makeParentMember();
//                        drawerAdapter = new NavigationDrawerAdapter(dataDrawer);
                        drawerAdapter = new DrawerAdapter(getActivity(), getData(true, loginAs, jObj.getInt("payment"), jObj.getInt("codmember")));
                        drawerAdapter.setOnClickListener(clickListener);
//                        drawerAdapter.setOnChildClickListener(new ClickListener() {
//                            @Override
//                            public void itemClicked(View view, int position) {
//                                Toast.makeText(getActivity(), dataDrawer.get(position).getTitle(), Toast.LENGTH_SHORT).show();
//                            }
//                        });
//
//                        drawerAdapter.setOnParentClickListener(new ClickListener() {
//                            @Override
//                            public void itemClicked(View view, int position) {
//                                Toast.makeText(getActivity(), dataDrawer.get(position).getTitle(), Toast.LENGTH_SHORT).show();
//                            }
//                        });
                        recyclerView.setAdapter(drawerAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        txtHost.setText(jObj.getString("alamat_host"));
                        txtTglPanen.setText(jObj.getString("tgl_panen"));
                        txtTglOrder.setText(jObj.getString("tgl_akhir")+" WIB ("+jObj.getString("order_akhir")+")");
                        MainActivity activity = (MainActivity)getActivity();
                        activity.setMenuCategory(0);
                        activity.setTanggalPanen(jObj.getString("tgl_panen"));

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
                        retry();
                    }
                }
                catch(JSONException e)
                {
                    e.printStackTrace();
                    retry();
                }

            }
        },new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse (VolleyError error){
                Log.e("TAG", "Drawer Error: " + error.getMessage());
                Toast.makeText(getActivity(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                retry();
            }
        })
        {
            @Override
            protected Map<String, String> getParams () {
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "memberhost");
                params.put("email", email);
                params.put("id", id);
                params.put("loginAs", loginAs);
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

    private void retry() {

        new AlertDialog.Builder(getActivity())
                .setTitle("Koneksi Bermasalah saat mendapat data host")
                .setMessage("Coba lagi")
                .setPositiveButton("Coba lagi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (sessionManager.isLoggedIn()){
                            parseMemberHost(emailUser, idUser, loginAs, tgl_value);
                        }
                        else {

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
